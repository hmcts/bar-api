package uk.gov.hmcts.bar.api.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

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
import uk.gov.hmcts.bar.api.data.service.PaymentActionService;
import uk.gov.hmcts.bar.api.data.service.PaymentTypeService;

public class ReferenceDataControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentTypeService paymentTypeService;
    
    @Mock
    private PaymentActionService paymentActionService;

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
            .andExpect(jsonPath("$", hasSize(5)))
            .andExpect(jsonPath("$[0].name", is("Cheque")))
            .andExpect(jsonPath("$[1].name", is("Card")))
            .andExpect(jsonPath("$[2].name", is("Postal Order")))
            .andExpect(jsonPath("$[3].name", is("Cash")))
            .andExpect(jsonPath("$[4].name", is("AllPay")));

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



    public List<PaymentType> getPaymentTyes() {
        return new ArrayList<PaymentType>() {{
            add(new PaymentType("CHEQUE","Cheque"));
            add(new PaymentType("CARD","Card"));
            add(new PaymentType("POSTAL_ORDER","Postal Order"));
            add(new PaymentType("CASH","Cash"));
            add(new PaymentType("ALLPAY","AllPay"));
        }};
    }
    
    public List<PaymentInstructionAction> getPaymentInstructionActions() {
        return new ArrayList<PaymentInstructionAction>() {{
            add(new PaymentInstructionAction("Process"));
            add(new PaymentInstructionAction("Return"));
            add(new PaymentInstructionAction("Suspense"));
        }};
    }

}
