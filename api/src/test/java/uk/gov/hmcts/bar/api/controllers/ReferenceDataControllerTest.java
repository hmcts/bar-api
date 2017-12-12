package uk.gov.hmcts.bar.api.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.hmcts.bar.api.controllers.refdata.ReferenceDataController;
import uk.gov.hmcts.bar.api.data.model.PaymentType;
import uk.gov.hmcts.bar.api.data.service.PaymentTypeService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReferenceDataControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentTypeService paymentTypeService;

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



    public List<PaymentType> getPaymentTyes() {
        return new ArrayList<PaymentType>() {{
            add(new PaymentType("cheques","Cheque"));
            add(new PaymentType("card","Card"));
            add(new PaymentType("postal-orders","Postal Order"));
            add(new PaymentType("cash","Cash"));
            add(new PaymentType("allpay","AllPay"));
        }};
    }

}
