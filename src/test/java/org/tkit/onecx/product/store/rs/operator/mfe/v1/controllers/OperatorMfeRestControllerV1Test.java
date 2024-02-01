package org.tkit.onecx.product.store.rs.operator.mfe.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.product.store.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.product.store.rs.operator.mfe.v1.model.UpdateMfeRequestMDTOv1;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OperatorMfeRestControllerV1.class)
@WithDBData(value = "data/test-operator-mfe.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class OperatorMfeRestControllerV1Test extends AbstractTest {

    @Test
    void createMfeTest() {
        var dto = new UpdateMfeRequestMDTOv1();
        dto.setExposedModule("exposed-module");
        dto.appVersion("0.0.0");
        dto.setRemoteBaseUrl("remote-base-url");
        dto.setRemoteEntry("remote-entry");
        dto.setAppName("display-name");
        dto.setProductName("product-name");
        dto.setRemoteBaseUrl("remote-base-url");

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("appId", "new_mfe_id")
                .body(dto)
                .put()
                .then()
                .statusCode(CREATED.getStatusCode());
    }

    @Test
    void createMfeUniqueErrorTest() {
        var dto = new UpdateMfeRequestMDTOv1();
        dto.appVersion("0.0.0");
        dto.setExposedModule("exposed-module1");
        dto.setRemoteBaseUrl("remote_base_url1");
        dto.setRemoteEntry("remote-entry1");
        dto.setAppName("display-name");
        dto.setProductName("product-name");
        dto.setRemoteBaseUrl("remote_base_url1");

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("appId", "new_mfe_id")
                .body(dto)
                .put()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void updateMfeTest() {
        var dto = new UpdateMfeRequestMDTOv1();
        dto.appVersion("0.0.0");
        dto.setExposedModule("exposed-module");
        dto.setRemoteBaseUrl("remote-base-url");
        dto.setRemoteEntry("remote-entry");
        dto.setAppName("display-name");
        dto.setProductName("product-name");
        dto.setRemoteBaseUrl("remote-base-url");

        dto.setClassifications(Set.of("a", "b"));
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("appId", "mfe1")
                .body(dto)
                .put()
                .then()
                .statusCode(OK.getStatusCode());

        dto.setExposedModule("exposed-module2");
        dto.setRemoteBaseUrl("remote-base-url2");
        dto.setRemoteEntry("remote-entry2");
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("appId", "mfe2")
                .body(dto)
                .put()
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void createOrUpdateMfeNotValidTest() {
        var dto = new UpdateMfeRequestMDTOv1();
        dto.setAppName("base_new");

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("appId", "new_mfe_id")
                .body(dto)
                .put()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void createOrUpdateMfeEmptyBodyTest() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("appId", "new_mfe_id")
                .put()
                .then().statusCode(BAD_REQUEST.getStatusCode());

    }
}
