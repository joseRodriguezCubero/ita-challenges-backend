package com.itachallenge.challenge.exception;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = GlobalExceptionHandlerTest.class)
class GlobalExceptionHandlerTest {
    //VARIABLES
    private final String REQUEST = "Invalid request";
    private final HttpStatus BAD_REQUEST = HttpStatus.BAD_REQUEST;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;
    @MockBean
    private ResponseStatusException responseStatusException;
    @MockBean
    private MethodArgumentNotValidException methodArgumentNotValidException;
    @MockBean
    private ErrorResponseMessage errorResponseMessage;
    @MockBean
    private ErrorMessage errorMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleResponseStatusException() {

        when(responseStatusException.getStatusCode()).thenReturn(BAD_REQUEST);
        when(responseStatusException.getReason()).thenReturn(REQUEST);
        when(errorResponseMessage.getStatusCode()).thenReturn(BAD_REQUEST.value());
        when(errorResponseMessage.getMessage()).thenReturn(REQUEST);

        ErrorResponseMessage expectedErrorMessage = new ErrorResponseMessage(BAD_REQUEST.value(), REQUEST);
        expectedErrorMessage.setStatusCode(BAD_REQUEST.value());
        expectedErrorMessage.setMessage(REQUEST);

        ResponseEntity<ErrorResponseMessage> response = globalExceptionHandler.handleResponseStatusException(responseStatusException);

        StepVerifier.create(Mono.just(response))
                .expectNextMatches(resp -> {
                    assertEquals(BAD_REQUEST, response.getStatusCode());
                    assertEquals(expectedErrorMessage, response.getBody());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void TestHandleMethodArgumentNotValidException() {

        // Arrange
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("object", "field", "message")));
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);

        // Act
        ResponseEntity <ErrorMessage> responseEntity = globalExceptionHandler.handleMethodArgumentNotValidException(methodArgumentNotValidException);

        // Assert
        MatcherAssert.assertThat(responseEntity, notNullValue());
    }

    @Test
    void TestHandleMethodArgumentNotValidException_Return_DefaultMessage() {

        // Arrange
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        FieldError fieldError = Mockito.mock(FieldError.class);
        when(fieldError.getField()).thenReturn("name");
        when(fieldError.getDefaultMessage()).thenReturn("default message");
        when(fieldError.getCodes()).thenReturn(new String[] {"message"});
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);

        // Act
        ResponseEntity<ErrorMessage> responseEntity = globalExceptionHandler.handleMethodArgumentNotValidException(methodArgumentNotValidException);

        // Assert
        MatcherAssert.assertThat(responseEntity, notNullValue());
    }

}





