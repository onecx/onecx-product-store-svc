package org.tkit.onecx.product.store.rs.operator.ms.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.mockito.ArgumentMatchers.any;
import static org.tkit.quarkus.security.test.SecurityTestUtils.getKeycloakClientToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tkit.onecx.product.store.AbstractTest;
import org.tkit.onecx.product.store.domain.daos.MicroserviceDAO;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;

import gen.org.tkit.onecx.product.store.rs.operator.ms.v1.model.UpdateMsRequestMsDTOv1;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OperatorMsRestControllerV1.class)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-ps-ms:write" })
class OperatorMsRestControllerV1ExceptionTest extends AbstractTest {

    @InjectMock
    MicroserviceDAO dao;

    @BeforeEach
    void beforeAll() {
        Mockito.when(dao.findByProductNameAppId(any(), any()))
                .thenThrow(new RuntimeException("Test technical error exception"))
                .thenThrow(new DAOException(MicroserviceDAO.ErrorKeys.ERROR_FIND_MS_PRODUCT_NAME_APP_ID,
                        new RuntimeException("Test")));
    }

    @Test
    void exceptionTest() {

        var dto = new UpdateMsRequestMsDTOv1();
        dto.name("name");
        dto.setDescription("description");

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(dto)
                .pathParam("productName", "product-name")
                .pathParam("appId", "mfe1")
                .put()
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(dto)
                .pathParam("productName", "product-name")
                .pathParam("appId", "mfe1")
                .put()
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());
    }
}
