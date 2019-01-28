package uk.gov.hmcts.bar.api.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.hmcts.bar.api.controllers.refdata.ReferenceDataController;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionAction;
import uk.gov.hmcts.bar.api.data.model.PaymentType;
import uk.gov.hmcts.bar.api.data.model.Site;
import uk.gov.hmcts.bar.api.data.service.PaymentActionService;
import uk.gov.hmcts.bar.api.data.service.PaymentTypeService;
import uk.gov.hmcts.bar.api.data.service.SiteService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReferenceDataControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentTypeService paymentTypeService;

    @Mock
    private PaymentActionService paymentActionService;

    @Mock
    private SiteService siteService;

    @InjectMocks
    private ReferenceDataController referenceDataController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(referenceDataController).build();

    }

    @Test
    public void testGetPaymentTypes() throws Exception {
        when(paymentTypeService.getAllPaymentTypes()).thenReturn(getPaymentTyes());

        this.mockMvc.perform(get("/payment-types"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(6)))
            .andExpect(jsonPath("$[0].name", is("Cheque")))
            .andExpect(jsonPath("$[1].name", is("Card")))
            .andExpect(jsonPath("$[2].name", is("Postal Order")))
            .andExpect(jsonPath("$[3].name", is("Cash")))
            .andExpect(jsonPath("$[4].name", is("AllPay")))
            .andExpect(jsonPath("$[5].name", is("Full Remission")));

        verify(paymentTypeService, times(1)).getAllPaymentTypes();
        verifyNoMoreInteractions(paymentTypeService);
    }

    @Test
    public void testGetPaymentInstructionActions() throws Exception {
    	when(paymentActionService.getAllPaymentInstructionAction()).thenReturn(getPaymentInstructionActions());
    	this.mockMvc.perform(get("/payment-action"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].action", is("Process")))
        .andExpect(jsonPath("$[1].action", is("Return")))
        .andExpect(jsonPath("$[2].action", is("Suspense")));

    	verify(paymentActionService, times(1)).getAllPaymentInstructionAction();
        verifyNoMoreInteractions(paymentActionService);
    }

    @Test
    public void testGetAllSites() throws Exception {
        when(siteService.getAllSites()).thenReturn(getAllSites());
        this.mockMvc.perform(get("/sites"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].siteId", is("1")))
            .andExpect(jsonPath("$[1].siteId", is("2")))
            .andExpect(jsonPath("$[2].siteId", is("3")));

        verify(siteService, times(1)).getAllSites();
        verifyNoMoreInteractions(siteService);
    }

    @Test
    public void testSaveSite() throws Exception {
        Site site = Site.siteWith().siteId("1").siteName("one").siteNumber("11").build();
        when(siteService.saveSite(site)).thenReturn(site);
        this.mockMvc.perform(post("/sites")
            .content("{ \"siteId\": \"1\", \"siteName\": \"one\", \"siteNumber\": \"11\" }")
            .contentType("application/json"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.siteId", is("1")))
            .andExpect(jsonPath("$.siteName", is("one")))
            .andExpect(jsonPath("$.siteNumber", is("11")));

        verify(siteService, times(1)).saveSite(site);
    }

    public List<PaymentType> getPaymentTyes() {
        return new ArrayList<PaymentType>() {{
            add(new PaymentType("CHEQUE","Cheque"));
            add(new PaymentType("CARD","Card"));
            add(new PaymentType("POSTAL_ORDER","Postal Order"));
            add(new PaymentType("CASH","Cash"));
            add(new PaymentType("ALLPAY","AllPay"));
            add(new PaymentType("FULL_REMISSION","Full Remission"));
        }};
    }

    public List<PaymentInstructionAction> getPaymentInstructionActions() {
        return new ArrayList<PaymentInstructionAction>() {{
            add(new PaymentInstructionAction("Process"));
            add(new PaymentInstructionAction("Return"));
            add(new PaymentInstructionAction("Suspense"));
        }};
    }

    public List<Site> getAllSites() {
        return new ArrayList<Site>() {{
           add(Site.siteWith().siteId("1").siteName("one").siteNumber("11").build());
           add(Site.siteWith().siteId("2").siteName("two").siteNumber("12").build());
           add(Site.siteWith().siteId("3").siteName("three").siteNumber("13").build());
        }};
    }

}
