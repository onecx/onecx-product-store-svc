package io.github.onecx.product.store.rs.operator.mfe.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.product.store.rs.operator.mfe.v1.model.ModuleTypeDTOV1;
import gen.io.github.onecx.product.store.rs.operator.mfe.v1.model.UpdateMfeRequestDTOV1;
import gen.io.github.onecx.product.store.rs.operator.product.v1.model.UpdateProductRequestDTOV1;
import io.github.onecx.product.store.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OperatorMfeRestControllerV1.class)
@WithDBData(value = "data/testdata-operator-mfe.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class OperatorMfeRestControllerV1Test extends AbstractTest {

    @Test
    void createMfeTest() {
        var dto = new UpdateMfeRequestDTOV1();
        dto.setExposedModule("exposed-module");
        dto.setRemoteBaseUrl("remote-base-url");
        dto.setRemoteEntry("remote-entry");
        dto.setRemoteName("remote-name");
        dto.setModuleType(ModuleTypeDTOV1.ANGULAR);
        dto.setDisplayName("display-name");
        dto.setProductName("product-name");
        dto.setRemoteBaseUrl("remote-base-url");

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("mfeId", "new_mfe_id")
                .body(dto)
                .put()
                .then()
                .statusCode(CREATED.getStatusCode());
    }

    @Test
    void createMfeUniqueErrorTest() {
        var dto = new UpdateMfeRequestDTOV1();
        dto.setExposedModule("exposed-module1");
        dto.setRemoteBaseUrl("remote_base_url1");
        dto.setRemoteEntry("remote-entry1");
        dto.setRemoteName("remote-name");
        dto.setModuleType(ModuleTypeDTOV1.ANGULAR);
        dto.setDisplayName("display-name");
        dto.setProductName("product-name");
        dto.setRemoteBaseUrl("remote_base_url1");

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("mfeId", "new_mfe_id")
                .body(dto)
                .put()
                .then().log().all()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void updateMfeTest() {
        var dto = new UpdateMfeRequestDTOV1();
        dto.setExposedModule("exposed-module");
        dto.setRemoteBaseUrl("remote-base-url");
        dto.setRemoteEntry("remote-entry");
        dto.setRemoteName("remote-name");
        dto.setModuleType(ModuleTypeDTOV1.ANGULAR);
        dto.setDisplayName("display-name");
        dto.setProductName("product-name");
        dto.setRemoteBaseUrl("remote-base-url");

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("mfeId", "mfe1")
                .body(dto)
                .put()
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void createOrUpdateMfeNotValidTest() {
        var dto = new UpdateProductRequestDTOV1();
        dto.setBasePath("/base_new");

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("mfeId", "new_mfe_id")
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
                .pathParam("mfeId", "new_mfe_id")
                .put()
                .then().statusCode(BAD_REQUEST.getStatusCode());

    }
}
