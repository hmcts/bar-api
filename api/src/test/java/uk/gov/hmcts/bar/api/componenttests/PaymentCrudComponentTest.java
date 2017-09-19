package uk.gov.hmcts.bar.api.componenttests;

import org.junit.Test;
import uk.gov.hmcts.bar.api.contract.PaymentDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.bar.api.contract.PaymentDto.paymentDtoWith;
public class PaymentCrudComponentTest extends ComponentTestBase {


    @Test
    public void create() throws Exception {
        PaymentDto.PaymentDtoBuilder proposedPayment = paymentDtoWith()
            .caseReference("new-case")
            .paymentChannel("card")
            .paymentDate("2017-09-14T00:00:00")
            .payeeName("Mr Tony Dowds")
            .amount(100);

        restActions
            .withUser("admin")
            .post("/payments", proposedPayment.build())
            .andExpect(status().isOk())
            .andExpect(body().as(PaymentDto.class, paymentDto -> {
                assertThat(paymentDto.getCaseReference()).isEqualTo("new-case");
                assertThat(paymentDto.getPaymentChannel()).isEqualTo("card");
                assertThat(paymentDto.getPaymentDate()).isEqualTo("2017-09-14T00:00");
                assertThat(paymentDto.getPayeeName()).isEqualTo("Mr Tony Dowds");
                assertThat(paymentDto.getAmount()).isEqualTo(100);
            }));
    }


}
