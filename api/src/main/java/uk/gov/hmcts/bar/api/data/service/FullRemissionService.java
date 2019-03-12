package uk.gov.hmcts.bar.api.data.service;


import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.bar.api.audit.AuditRepository;
import uk.gov.hmcts.bar.api.data.exceptions.BarUserNotFoundException;
import uk.gov.hmcts.bar.api.data.exceptions.PaymentInstructionNotFoundException;
import uk.gov.hmcts.bar.api.data.model.*;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionRepository;
import uk.gov.hmcts.bar.api.data.repository.PaymentInstructionStatusRepository;
import uk.gov.hmcts.bar.api.data.utils.Util;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Optional;

@Service
@Transactional
public class FullRemissionService {

    private static final String[] ALWAYS_UPDATE = new String[]{ "actionComment", "actionReason" };
    private PaymentInstructionRepository paymentInstructionRepository;
    private PaymentInstructionStatusRepository paymentInstructionStatusRepository;
    private BarUserService barUserService;
    private final AuditRepository auditRepository;
    public FullRemissionService(PaymentInstructionRepository paymentInstructionRepository,
                                     BarUserService barUserService,
                                     PaymentInstructionStatusRepository paymentInstructionStatusRepository,
                                     AuditRepository auditRepository

    ) {
        this.paymentInstructionRepository = paymentInstructionRepository;
        this.barUserService = barUserService;
        this.paymentInstructionStatusRepository = paymentInstructionStatusRepository;
        this.auditRepository = auditRepository;
    }

    public PaymentInstruction updateFullRemission(Integer id, FullRemission fullRemission)  {
        String userId = barUserService.getCurrentUserId();
        BarUser barUser = getBarUser();
        Optional<PaymentInstruction> optionalPaymentInstruction = paymentInstructionRepository.findById(id);
        PaymentInstruction existingPaymentInstruction = optionalPaymentInstruction
            .orElseThrow(() -> new PaymentInstructionNotFoundException(id));

        updatePaymentInstructionsProps(existingPaymentInstruction, fullRemission);
        existingPaymentInstruction.setUserId(userId);
        savePaymentInstructionStatus(existingPaymentInstruction, userId);
        PaymentInstruction paymentInstruction = paymentInstructionRepository.saveAndRefresh(existingPaymentInstruction);
        auditRepository.trackPaymentInstructionEvent("FULL_REMISSION_PI_UPDATE_EVENT",existingPaymentInstruction,barUser);
        return paymentInstruction;
    }

    private BarUser getBarUser()  {
        Optional<BarUser> optBarUser = barUserService.getBarUser();
        return optBarUser.orElseThrow(()-> new BarUserNotFoundException("Bar user not found"));
    }

    private void updatePaymentInstructionsProps(PaymentInstruction existingPi, Object updateRequest) {
        String[] nullPropertiesNamesToIgnore = Util.getNullPropertyNames(updateRequest);
        String[] propNamesToIgnore = Arrays.stream(nullPropertiesNamesToIgnore)
            .filter(s -> Arrays.stream(ALWAYS_UPDATE).noneMatch(s::equals))
            .toArray(String[]::new);
        BeanUtils.copyProperties(updateRequest, existingPi, propNamesToIgnore);
    }
    private void savePaymentInstructionStatus(PaymentInstruction pi, String userId) {
        PaymentInstructionStatus pis = new PaymentInstructionStatus(userId, pi);
        paymentInstructionStatusRepository.save(pis);
    }


}
