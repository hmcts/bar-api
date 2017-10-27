package uk.gov.hmcts.bar.api.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.hmcts.bar.api.controllers.refdata.ReferenceDataController;
import uk.gov.hmcts.bar.api.model.PaymentType;
import uk.gov.hmcts.bar.api.model.ReferenceDataService;

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
    private ReferenceDataService referenceDataService;

    @InjectMocks
    private ReferenceDataController referenceDataController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(referenceDataController).build();

    }

    @Test
    public void testGetPaymentTypes() throws Exception {
        when(referenceDataService.getAllPaymentTypes()).thenReturn(getPaymentTyes());

        this.mockMvc.perform(get("/payment-types"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(6)))
            .andExpect(jsonPath("$[0].name", is("Cheque")))
            .andExpect(jsonPath("$[1].name", is("Full Remission")))
            .andExpect(jsonPath("$[2].name", is("Card")))
            .andExpect(jsonPath("$[3].name", is("Postal Order")))
            .andExpect(jsonPath("$[4].name", is("Cash")))
            .andExpect(jsonPath("$[5].name", is("AllPay")));

        verify(referenceDataService, times(1)).getAllPaymentTypes();
        verifyNoMoreInteractions(referenceDataService);
    }



    public List<PaymentType> getPaymentTyes() {
        return new ArrayList<PaymentType>() {{
            add(new PaymentType(1,"Cheque"));
            add(new PaymentType(2,"Full Remission"));
            add(new PaymentType(3,"Card"));
            add(new PaymentType(4,"Postal Order"));
            add(new PaymentType(5,"Cash"));
            add(new PaymentType(6,"AllPay"));
        }};
    }

}
