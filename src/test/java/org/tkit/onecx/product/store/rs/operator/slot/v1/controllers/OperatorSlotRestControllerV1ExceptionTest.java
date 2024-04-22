package org.tkit.onecx.product.store.rs.operator.slot.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tkit.onecx.product.store.AbstractTest;
import org.tkit.onecx.product.store.domain.daos.SlotDAO;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import gen.org.tkit.onecx.product.store.rs.operator.slot.v1.model.UpdateSlotRequestSlotDTOv1;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OperatorSlotRestControllerV1.class)
class OperatorSlotRestControllerV1ExceptionTest extends AbstractTest {

    @InjectMock
    SlotDAO dao;

    @BeforeEach
    void beforeAll() {
        Mockito.when(dao.findByProductNameAppId(any(), any(), any()))
                .thenThrow(new RuntimeException("Test technical error exception"))
                .thenThrow(new DAOException(SlotDAO.ErrorKeys.ERROR_FIND_SLOT_PRODUCT_NAME_APP_ID_NAME,
                        new RuntimeException("Test")));
    }

    @Test
    void exceptionTest() {

        var dto = new UpdateSlotRequestSlotDTOv1();
        dto.name("name");
        dto.setDescription("description");

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
