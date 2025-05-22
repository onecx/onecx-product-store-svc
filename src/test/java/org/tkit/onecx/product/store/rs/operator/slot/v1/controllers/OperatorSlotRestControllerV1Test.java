package org.tkit.onecx.product.store.rs.operator.slot.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.product.store.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.product.store.rs.operator.slot.v1.model.UpdateSlotRequestSlotDTOv1;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OperatorSlotRestControllerV1.class)
@WithDBData(value = "data/test-operator-slot.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-ps-slot:write" })
class OperatorSlotRestControllerV1Test extends AbstractTest {

    @Test
    void createSlotTest() {
        var dto = new UpdateSlotRequestSlotDTOv1();
        dto.description("description");
        dto.name("display-name");

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("productName", "product-name")
                .pathParam("appId", "new_ms_id")
                .body(dto)
                .put()
                .then()
                .statusCode(CREATED.getStatusCode());
    }

    @Test
    void updateSlotTest() {
        var dto = new UpdateSlotRequestSlotDTOv1();
        dto.name("slot1");
        dto.description("description");

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("productName", "product1")
                .pathParam("appId", "a1")
                .body(dto)
                .put()
                .then()
                .statusCode(OK.getStatusCode());

        dto.setDescription("desc2");
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("productName", "product-name")
                .pathParam("appId", "ms2")
                .body(dto)
                .put()
                .then()
                .statusCode(CREATED.getStatusCode());
    }

    @Test
    void createOrUpdateSlotEmptyBodyTest() {
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("productName", "product-name")
                .pathParam("appId", "new_ms_id")
                .put()
                .then().statusCode(BAD_REQUEST.getStatusCode());

    }
}
