package uk.gov.hmcts.bar.api.controllers.errors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import uk.gov.hmcts.bar.api.data.exceptions.ResourceNotFoundException;

public class ControllerExceptionHandlerTest {

	private ControllerExceptionHandler controllerExceptionHandler = new ControllerExceptionHandler();

	@Mock
	private MethodArgumentNotValidException methodArgumentNotValidExceptionMock;

	@Mock
	private MethodParameter methodParameterMock;

	@Mock
	private ControllerExceptionHandler controllerExceptionHandlerMock;

	@Mock
	private ResourceNotFoundException resourceNotFoundExceptionMock;
	
	@Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
	}

	@Test
	public void whenResourceNotFound_shouldReturn404() {
		ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException("Payment Instruction", "Id",
				"2");
		ResponseEntity<Error> reError = controllerExceptionHandler.entityNotFoundException(resourceNotFoundException);
		assertThat(reError.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void whenResourceNotFound_shouldReturnErrorMessage() {
		ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException("Payment Instruction", "Id",
				"2");
		ResponseEntity<Error> reError = controllerExceptionHandler.entityNotFoundException(resourceNotFoundException);
		assertThat(reError.getBody().getMessage()).isEqualTo("Payment Instruction for Id=2 was not found");
	}

	@Test
	public void whenMethodArgumentNotMatches_shouldReturn500() {
		when(methodArgumentNotValidExceptionMock.getParameter()).thenReturn(methodParameterMock);
		when(methodArgumentNotValidExceptionMock.getMessage()).thenReturn("Parameter does not match");
		when(controllerExceptionHandlerMock.methodArgumentNotValidException(methodArgumentNotValidExceptionMock))
				.thenReturn(new ResponseEntity<>(new Error("Parameter does not match"), BAD_REQUEST));
		assertThat(controllerExceptionHandlerMock.methodArgumentNotValidException(methodArgumentNotValidExceptionMock)
				.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
}
