package org.tkit.onecx.product.store.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.mockito.ArgumentMatchers.any;
import static org.tkit.quarkus.security.test.SecurityTestUtils.getKeycloakClientToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tkit.onecx.product.store.AbstractTest;
import org.tkit.onecx.product.store.domain.daos.ProductDAO;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;

import gen.org.tkit.onecx.product.store.rs.external.v1.model.ProductItemSearchCriteriaDTOv1;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ProductsRestControllerV1.class)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-ps:read" })
class ProductsRestControllerV1ExceptionTest extends AbstractTest {

    @InjectMock
    ProductDAO dao;

    @BeforeEach
    void beforeAll() {
        Mockito.when(dao.findProductsByCriteria(any()))
                .thenThrow(new RuntimeException("Test technical error exception"))
                .thenThrow(new DAOException(ProductDAO.ErrorKeys.ERROR_FIND_PRODUCT_BY_NAME, new RuntimeException("Test")));
    }

    @Test
    void exceptionTest() {

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(new ProductItemSearchCriteriaDTOv1())
                .post("/search")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());

        var exception = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(new ProductItemSearchCriteriaDTOv1())
                .post("/search")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());
    }

}
