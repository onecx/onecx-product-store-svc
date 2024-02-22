package org.tkit.onecx.product.store.rs.operator.mfe.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tkit.onecx.product.store.AbstractTest;
import org.tkit.onecx.product.store.domain.daos.MicrofrontendDAO;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import gen.org.tkit.onecx.product.store.rs.operator.mfe.v1.model.UpdateMfeRequestMDTOv1;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OperatorMfeRestControllerV1.class)
class OperatorMsRestControllerV1ExceptionTest extends AbstractTest {

    @InjectMock
    MicrofrontendDAO dao;

    @BeforeEach
    void beforeAll() {
        Mockito.when(dao.findByAppId(any()))
                .thenThrow(new RuntimeException("Test technical error exception"))
                .thenThrow(new DAOException(MicrofrontendDAO.ErrorKeys.ERROR_FIND_APP_ID, new RuntimeException("Test")));
    }

    @Test
    void exceptionTest() {

        var dto = new UpdateMfeRequestMDTOv1();
        dto.setExposedModule("exposed-module");
        dto.setRemoteBaseUrl("remote-base-url");
        dto.setRemoteEntry("remote-entry");
        dto.setTechnology("angular");
        dto.setAppName("display-name");
        dto.setRemoteBaseUrl("remote-base-url");

        given()
                .contentType(APPLICATION_JSON)
                .body(dto)
                .pathParam("productName", "product-name")
                .pathParam("appId", "mfe1")
                .put()
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());

        given()
                .contentType(APPLICATION_JSON)
                .body(dto)
                .pathParam("productName", "product-name")
                .pathParam("appId", "mfe1")
                .put()
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());
    }
}
