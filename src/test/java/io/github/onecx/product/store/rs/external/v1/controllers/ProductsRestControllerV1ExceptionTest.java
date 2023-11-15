package io.github.onecx.product.store.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import gen.io.github.onecx.product.store.rs.external.v1.model.ProductSearchCriteriaDTOV1;
import gen.io.github.onecx.product.store.rs.external.v1.model.RestExceptionDTOV1;
import io.github.onecx.product.store.AbstractTest;
import io.github.onecx.product.store.domain.daos.ProductDAO;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ProductsRestControllerV1.class)
class ProductsRestControllerV1ExceptionTest extends AbstractTest {

    @InjectMock
    ProductDAO dao;

    @BeforeEach
    void beforeAll() {
        Mockito.when(dao.findProductsByCriteria(any()))
                .thenThrow(new RuntimeException("Test technical error exception"))
                .thenThrow(new DAOException(ErrorKey.ERROR_TEST, new RuntimeException("Test")));
    }

    @Test
    void exceptionTest() {

        given()
                .contentType(APPLICATION_JSON)
                .body(new ProductSearchCriteriaDTOV1())
                .post()
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());

        var exception = given()
                .contentType(APPLICATION_JSON)
                .body(new ProductSearchCriteriaDTOV1())
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(RestExceptionDTOV1.class);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorKey.ERROR_TEST.name());
    }

    public enum ErrorKey {
        ERROR_TEST;
    }
}
