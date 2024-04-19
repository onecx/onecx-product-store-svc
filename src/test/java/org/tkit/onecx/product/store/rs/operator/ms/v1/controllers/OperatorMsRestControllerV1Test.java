package org.tkit.onecx.product.store.rs.operator.ms.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.product.store.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.product.store.rs.operator.ms.v1.model.UpdateMsRequestMsDTOv1;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OperatorMsRestControllerV1.class)
@WithDBData(value = "data/test-operator-ms.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class OperatorMsRestControllerV1Test extends AbstractTest {

    @Test
    void createMsTest() {
        var dto = new UpdateMsRequestMsDTOv1();
        dto.version("0.0.0");
        dto.name("display-name");

        given()
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
    void updateMsTest() {
        var dto = new UpdateMsRequestMsDTOv1();
        dto.name("display-name");

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("productName", "p1")
                .pathParam("appId", "ms1")
                .body(dto)
                .put()
                .then()
                .statusCode(OK.getStatusCode());

        dto.setDescription("desc2");
        given()
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
    void createOrUpdateMsEmptyBodyTest() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("productName", "product-name")
                .pathParam("appId", "new_ms_id")
                .put()
                .then().statusCode(BAD_REQUEST.getStatusCode());

    }
}
