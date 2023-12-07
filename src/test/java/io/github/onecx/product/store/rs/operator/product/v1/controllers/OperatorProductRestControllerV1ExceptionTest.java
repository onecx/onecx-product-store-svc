package io.github.onecx.product.store.rs.operator.product.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import gen.io.github.onecx.product.store.rs.operator.product.v1.model.UpdateProductRequestPDTOv1;
import io.github.onecx.product.store.AbstractTest;
import io.github.onecx.product.store.domain.daos.ProductDAO;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OperatorProductRestControllerV1.class)
class OperatorProductRestControllerV1ExceptionTest extends AbstractTest {

    @InjectMock
    ProductDAO dao;

    @BeforeEach
    void beforeAll() {
        Mockito.when(dao.findProductByName(any()))
                .thenThrow(new RuntimeException("Test technical error exception"))
                .thenThrow(new DAOException(ProductDAO.ErrorKeys.ERROR_FIND_PRODUCT_BY_NAME, new RuntimeException("Test")));
    }

    @Test
    void exceptionTest() {

        var dto = new UpdateProductRequestPDTOv1();
        dto.basePath("/new_product");
        dto.setVersion("0.0.0");

        given()
                .contentType(APPLICATION_JSON)
                .body(dto)
                .pathParam("name", "new_product_name")
                .put()
                .then().log().all()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());

        given()
                .contentType(APPLICATION_JSON)
                .body(dto)
                .pathParam("name", "new_product_name")
                .put()
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());
    }

}
