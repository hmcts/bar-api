package uk.gov.hmcts.bar.api.data.service;


import com.google.common.collect.Lists;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.ff4j.FF4j;
import org.ff4j.exception.FeatureAccessException;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.bar.api.audit.AuditRepository;
import uk.gov.hmcts.bar.api.controllers.payment.PaymentInstructionController;
import uk.gov.hmcts.bar.api.data.enums.PaymentActionEnum;
import uk.gov.hmcts.bar.api.data.enums.PaymentStatusEnum;
import uk.gov.hmcts.bar.api.data.exceptions.BarUserNotFoundException;
import uk.gov.hmcts.bar.api.data.exceptions.InvalidActionException;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentInstructionNotFoundException;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentProcessException;
import uk.gov.hmcts.bar.api.data.model.*;
import uk.gov.hmcts.bar.api.data.repository.*;
import uk.gov.hmcts.bar.api.data.utils.Util;
import uk.gov.hmcts.bar.api.integration.payhub.data.PayhubPaymentInstruction;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@Service
@Transactional
public class PaymentInstructionService {

    public static final String STAT_GROUP_DETAILS = "stat-group-details";
    public static final String STAT_DETAILS = "stat-details";

    private static final Logger LOG = getLogger(PaymentInstructionService.class);

    private static final List<String> GROUPED_TYPES = Arrays.asList("CHEQUE", "POSTAL_ORDER");

    private static final int PAGE_NUMBER = 0;
    private static final int MAX_RECORDS_PER_PAGE = 200;
    private PaymentInstructionRepository paymentInstructionRepository;
    private PaymentInstructionStatusRepository paymentInstructionStatusRepository;
    private PaymentReferenceService paymentReferenceService;
    private UnallocatedAmountService unallocatedAmountService;
    private final BarUserService barUserService;
    private final BankGiroCreditRepository bankGiroCreditRepository;
    private final FF4j ff4j;
    private PaymentTypeService paymentTypeService;
    private final PayhubPaymentInstructionRepository payhubPaymentInstructionRepository;
    private final AuditRepository auditRepository;


    public PaymentInstructionService(PaymentReferenceService paymentReferenceService, PaymentInstructionRepository paymentInstructionRepository,
                                     BarUserService barUserService,
                                     PaymentInstructionStatusRepository paymentInstructionStatusRepository,
                                     FF4j ff4j,
                                     BankGiroCreditRepository bankGiroCreditRepository,
                                     PaymentTypeService paymentTypeService,
                                     UnallocatedAmountService unallocatedAmountService,
                                     PayhubPaymentInstructionRepository payhubPaymentInstructionRepository,
                                     AuditRepository auditRepository

    ) {
        this.paymentReferenceService = paymentReferenceService;
        this.paymentInstructionRepository = paymentInstructionRepository;
        this.barUserService = barUserService;
        this.paymentInstructionStatusRepository = paymentInstructionStatusRepository;
        this.ff4j = ff4j;
        this.bankGiroCreditRepository = bankGiroCreditRepository;
        this.paymentTypeService = paymentTypeService;
        this.unallocatedAmountService = unallocatedAmountService;
        this.payhubPaymentInstructionRepository = payhubPaymentInstructionRepository;
        this.auditRepository = auditRepository;
    }

    public PaymentInstruction createPaymentInstruction(PaymentInstruction paymentInstruction) throws BarUserNotFoundException {
        String userId = barUserService.getCurrentUserId();
        BarUser barUser = getBarUser();
        PaymentReference nextPaymentReference = paymentReferenceService.getNextPaymentReferenceSequenceBySite(barUser.getSiteId());
        paymentInstruction.setSiteId(barUser.getSiteId());
        paymentInstruction.setDailySequenceId(nextPaymentReference.getDailySequenceId());
        paymentInstruction.setStatus(PaymentStatusEnum.DRAFT.dbKey());
        paymentInstruction.setUserId(userId);
        PaymentInstruction savedPaymentInstruction = paymentInstructionRepository.saveAndRefresh(paymentInstruction);
        savePaymentInstructionStatus(savedPaymentInstruction, userId);
        auditRepository.trackPaymentInstructionEvent("CREATE_PAYMENT_INSTRUCTION_EVENT", paymentInstruction, barUser);
        return savedPaymentInstruction;
    }

    public List<PaymentInstruction> getAllPaymentInstructions(PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto) throws BarUserNotFoundException {
        BarUser barUser = getBarUser();
        paymentInstructionSearchCriteriaDto.setSiteId(barUser.getSiteId());
        PaymentInstructionsSpecifications<PaymentInstruction> paymentInstructionsSpecification = new PaymentInstructionsSpecifications<>(paymentInstructionSearchCriteriaDto,paymentTypeService);
        Sort sort = new Sort(Sort.Direction.DESC, "paymentDate");
        Pageable pageDetails = PageRequest.of(PAGE_NUMBER, MAX_RECORDS_PER_PAGE, sort);

        Specification<PaymentInstruction> piSpecification = null;
        if (paymentInstructionSearchCriteriaDto.getMultiplePiIds() != null) {
            piSpecification = paymentInstructionsSpecification.getPaymentInstructionsMultipleIdSpecification();
        } else {
            piSpecification = paymentInstructionsSpecification.getPaymentInstructionsSpecification();
        }

        return Lists.newArrayList(paymentInstructionRepository.findAll(piSpecification, pageDetails).iterator());
    }

    public long getPaymentInstructionsCount(PaymentInstructionStatusCriteriaDto paymentInstructionStatusCriteriaDto) {
        PaymentInstructionStatusSpecifications<PaymentInstructionStatus> paymentInstructionStatusSpecification = new PaymentInstructionStatusSpecifications(paymentInstructionStatusCriteriaDto);
        Specification<PaymentInstructionStatus>  pisSpecification = paymentInstructionStatusSpecification.getPaymentInstructionStatusSpecification();
        return paymentInstructionStatusRepository.count(pisSpecification);

    }

    public List<PayhubPaymentInstruction> getAllPaymentInstructionsForPayhub(
        PaymentInstructionSearchCriteriaDto paymentInstructionSearchCriteriaDto
    ) throws BarUserNotFoundException {
        BarUser barUser = getBarUser();
        paymentInstructionSearchCriteriaDto.setSiteId(barUser.getSiteId());
        PaymentInstructionsSpecifications<PayhubPaymentInstruction> paymentInstructionsSpecification =
            new PaymentInstructionsSpecifications<>(paymentInstructionSearchCriteriaDto, paymentTypeService);

        Specification<PayhubPaymentInstruction> piForPayhubSpecification = paymentInstructionsSpecification.getPaymentInstructionsSpecification();
        return payhubPaymentInstructionRepository.findAll(piForPayhubSpecification);
    }

    public PaymentInstruction getPaymentInstruction(Integer id) {
        Optional<PaymentInstruction> op = paymentInstructionRepository.findById(id);
        return op.orElse(null);
    }

    public void deletePaymentInstruction(Integer id) {
        try {
            paymentInstructionRepository.deleteById(id);
        } catch (EmptyResultDataAccessException erdae) {
            LOG.error("Resource not found: " + erdae.getMessage(), erdae);
            throw new PaymentInstructionNotFoundException(id);
        }

    }

    public PaymentInstruction submitPaymentInstruction(Integer id, PaymentInstructionUpdateRequest paymentInstructionUpdateRequest) throws PaymentProcessException {
        if (!checkIfActionEnabled(paymentInstructionUpdateRequest)) {
            throw new FeatureAccessException(paymentInstructionUpdateRequest.getAction() + " is not allowed");
        }
		if (PaymentActionEnum.PROCESS.displayValue().equals(paymentInstructionUpdateRequest.getAction())
				&& unallocatedAmountService.calculateUnallocatedAmount(id) != 0) {
			throw new PaymentProcessException("Please allocate all amount before processing.");
		}
        String userId = barUserService.getCurrentUserId();
        Optional<PaymentInstruction> optionalPaymentInstruction = paymentInstructionRepository.findById(id);
        PaymentInstruction existingPaymentInstruction = optionalPaymentInstruction
            .orElseThrow(() -> new PaymentInstructionNotFoundException(id));
        String[] nullPropertiesNamesToIgnore = Util.getNullPropertyNames(paymentInstructionUpdateRequest);
        BeanUtils.copyProperties(paymentInstructionUpdateRequest, existingPaymentInstruction, nullPropertiesNamesToIgnore);
        existingPaymentInstruction.setUserId(userId);
        savePaymentInstructionStatus(existingPaymentInstruction, userId);
        PaymentInstruction paymentInstruction = paymentInstructionRepository.saveAndRefresh(existingPaymentInstruction);
        Optional<BarUser> optBarUser = barUserService.getBarUser();
        BarUser barUser = (optBarUser.isPresent())? optBarUser.get(): null;
        auditRepository.trackPaymentInstructionEvent("PAYMENT_INSTRUCTION_UPDATE_EVENT",existingPaymentInstruction,barUser);

        return paymentInstruction;
    }

    public PaymentInstruction updatePaymentInstruction(Integer id, PaymentInstructionRequest paymentInstructionRequest) throws BarUserNotFoundException {
        String userId = barUserService.getCurrentUserId();
        BarUser barUser = getBarUser();
        Optional<PaymentInstruction> optionalPaymentInstruction = paymentInstructionRepository.findById(id);
        PaymentInstruction existingPaymentInstruction = optionalPaymentInstruction
            .orElseThrow(() -> new PaymentInstructionNotFoundException(id));

        // handle bgc number
        if (paymentInstructionRequest.getBgcNumber() != null) {
            BankGiroCredit bgc = bankGiroCreditRepository.findByBgcNumber(paymentInstructionRequest.getBgcNumber())
                .orElseGet(() -> bankGiroCreditRepository.save(new BankGiroCredit(paymentInstructionRequest.getBgcNumber(), barUser.getSiteId())));
            existingPaymentInstruction.setBgcNumber(bgc.getBgcNumber());
        }

        String[] nullPropertiesNamesToIgnore = Util.getNullPropertyNames(paymentInstructionRequest);
        BeanUtils.copyProperties(paymentInstructionRequest, existingPaymentInstruction, nullPropertiesNamesToIgnore);
        existingPaymentInstruction.setUserId(userId);
        savePaymentInstructionStatus(existingPaymentInstruction, userId);
        PaymentInstruction paymentInstruction = paymentInstructionRepository.saveAndRefresh(existingPaymentInstruction);
        auditRepository.trackPaymentInstructionEvent("PAYMENT_INSTRUCTION_UPDATE_EVENT",existingPaymentInstruction,barUser);
        return paymentInstruction;
    }

    public List<PaymentInstruction> getAllPaymentInstructionsByCaseReference(String caseReference) {
        return paymentInstructionRepository.findByCaseReference(caseReference);
    }


    public MultiMap getPaymentInstructionStats(String status,boolean sentToPayhub) {
        List<PaymentInstructionUserStats> paymentInstructionInStatusList = paymentInstructionStatusRepository
            .getPaymentInstructionsByStatusGroupedByUser(status,sentToPayhub);

        return Util.createMultimapFromList(paymentInstructionInStatusList);
    }

    public MultiMap getPaymentInstructionStatsByCurrentStatusGroupedByOldStatus(String currentStatus,
                                                                                String oldStatus) {
        List<PaymentInstructionStaticsByUser> paymentInstructionStaticsByUserObjects = paymentInstructionStatusRepository
            .getPaymentInstructionStatsByCurrentStatusAndByOldStatus(currentStatus, oldStatus);
        paymentInstructionStaticsByUserObjects = Util.getFilteredPisList(paymentInstructionStaticsByUserObjects);
        return Util.createMultimapFromPisByUserList(paymentInstructionStaticsByUserObjects);
    }

    public MultiMap getPaymentStatsByUserGroupByType(String userId, String status, boolean sentToPayhub) {
        List<PaymentInstructionStats> results = paymentInstructionStatusRepository.getStatsByUserGroupByType(userId, status, sentToPayhub);

        MultiMap paymentInstructionStatsGroupedByBgc = new MultiValueMap();
        results.stream().forEach(stat -> {
            Link detailslink = null;


                detailslink = linkTo(methodOn(PaymentInstructionController.class)
                    .getPaymentInstructionsByIdamId(userId, status,
                        null, null, null, null, null,
                        null, null, null, stat.getPaymentType(), null, null, stat.getBgc())
                ).withRel(STAT_DETAILS);



            Resource<PaymentInstructionStats> resource = new Resource<>(stat, detailslink.expand());

            // TODO: this is just a temp solution we have to clarify with PO if we really need to group cheques and postal-orders
            if (GROUPED_TYPES.contains(stat.getPaymentType())) {
                Link groupedLink = null;

                    groupedLink = linkTo(methodOn(PaymentInstructionController.class)
                        .getPaymentInstructionsByIdamId(userId, status,
                            null, null, null, null, null,
                            null, null, null,
                            GROUPED_TYPES.stream().collect(Collectors.joining(",")), null, null, stat.getBgc())
                    ).withRel(STAT_GROUP_DETAILS);

                resource.add(groupedLink.expand());
            }

            paymentInstructionStatsGroupedByBgc.put(
                stat.getBgc() == null ? "0" : stat.getBgc(), resource);
        });
        return paymentInstructionStatsGroupedByBgc;
    }

    private void savePaymentInstructionStatus(PaymentInstruction pi, String userId) {
        PaymentInstructionStatusReferenceKey pisrKey = new PaymentInstructionStatusReferenceKey(pi.getId(),
            pi.getStatus());
        PaymentInstructionStatus pis = new PaymentInstructionStatus(pisrKey, userId);
        paymentInstructionStatusRepository.save(pis);
    }

    public Map<Integer, List<PaymentInstructionStatusHistory>> getStatusHistoryMapForTTB(LocalDate startDate,  LocalDate endDate) {

        if (null != endDate && startDate.isAfter(endDate)) {
            LOG.error("PaymentInstructionService - Error while generating daily fees csv file. Incorrect start and end dates ");
            return Collections.emptyMap();
        }
        LocalDate searchEndDate;

        if (null == endDate || startDate.equals(endDate)) {
            searchEndDate = startDate.plusDays(1);
        } else {
            searchEndDate = endDate.plusDays(1);
        }

        List<PaymentInstructionStatusHistory> statusHistoryList = paymentInstructionStatusRepository.getPaymentInstructionStatusHistoryForTTB
            (startDate.atStartOfDay(), searchEndDate.atStartOfDay());

        final Map<Integer, List<PaymentInstructionStatusHistory>> statusHistoryMapByPaymentInstructionId = new HashMap<>();
        for (final PaymentInstructionStatusHistory statusHistory : statusHistoryList) {
            if (statusHistoryMapByPaymentInstructionId.get(statusHistory.getPaymentInstructionId()) == null) {
                List<PaymentInstructionStatusHistory> listByPaymentInstructionId = new ArrayList<>();
                listByPaymentInstructionId.add(statusHistory);
                statusHistoryMapByPaymentInstructionId.put(statusHistory.getPaymentInstructionId(), listByPaymentInstructionId);
            } else {
                statusHistoryMapByPaymentInstructionId.get(statusHistory.getPaymentInstructionId()).add(statusHistory);
            }

        }
        return statusHistoryMapByPaymentInstructionId;
    }

    public List<PaymentInstruction> getAllPaymentInstructionsByTTB(LocalDate startDate, LocalDate endDate) {
        Map<Integer, List<PaymentInstructionStatusHistory>> statusHistortMapForTTB = getStatusHistoryMapForTTB(startDate, endDate);
        Iterator<Map.Entry<Integer, List<PaymentInstructionStatusHistory>>> iterator = statusHistortMapForTTB.entrySet().iterator();
        List<PaymentInstruction> paymentInstructionsList = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry<Integer, List<PaymentInstructionStatusHistory>> entry = iterator.next();
            PaymentInstruction paymentInstruction = paymentInstructionRepository.getOne(entry.getKey());
            paymentInstruction.setPaymentInstructionStatusHistory(entry.getValue());
            paymentInstructionsList.add(paymentInstruction);

        }
        return paymentInstructionsList;
    }

    private boolean checkIfActionEnabled(PaymentInstructionUpdateRequest paymentInstructionUpdateRequest){
        boolean[] ret = { true };
        String action = paymentInstructionUpdateRequest.getAction();
        PaymentActionEnum.findByDisplayValue(action).ifPresent(paymentActionEnum -> {
            ret[0] = ff4j.check(paymentActionEnum.featureKey());
        });
        return ret[0];
    }

    private BarUser getBarUser() throws BarUserNotFoundException {
        Optional<BarUser> optBarUser = barUserService.getBarUser();
        BarUser barUser = optBarUser.orElseThrow(()-> new BarUserNotFoundException("Bar user not found"));
        return barUser;
    }
}
