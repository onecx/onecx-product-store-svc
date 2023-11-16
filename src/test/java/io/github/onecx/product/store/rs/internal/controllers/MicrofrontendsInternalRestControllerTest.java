package io.github.onecx.product.store.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.product.store.rs.internal.model.*;
import io.github.onecx.product.store.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(MicrofrontendsInternalRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class MicrofrontendsInternalRestControllerTest extends AbstractTest {

    @Test
    void createMicrofrontendTest() {

        CreateMicrofrontendDTO createDto = new CreateMicrofrontendDTO();
        createDto.setBasePath("basePath");
        createDto.setMfeId("mfeId");
        createDto.setExposedModule("exposed-module");
        createDto.setRemoteBaseUrl("remote-base-url");
        createDto.setRemoteEntry("remote-entry");
        createDto.setRemoteName("remote-name");
        createDto.setModuleType(ModuleTypeDTO.ANGULAR);
        createDto.setDisplayName("display-name");
        createDto.setProductName("product-name");
        createDto.setRemoteBaseUrl("remote-base-url");

        var dto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(createDto)
                .post()
                .then().log().all()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(MicrofrontendDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getDisplayName()).isNotNull().isEqualTo(createDto.getDisplayName());
        assertThat(dto.getBasePath()).isNotNull().isEqualTo(createDto.getBasePath());

        // create theme without body
        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(RestExceptionDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getMessage()).isEqualTo("createMicrofrontend.createMicrofrontendDTO: must not be null");

        // create theme with existing name
        exception = given().when()
                .contentType(APPLICATION_JSON)
                .body(createDto)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(RestExceptionDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("PERSIST_ENTITY_FAILED");
        assertThat(exception.getMessage()).isEqualTo(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'ps_microfrontend_mfe_id'  Detail: Key (mfe_id)=(mfeId) already exists.]");

    }

    @Test
    void deleteMicrofrontendTest() {
        // delete micro-frontend
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "p1")
                .delete("{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        // check if micro-frontend exists
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "p1")
                .get("{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        // delete micro-frontend
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "p1")
                .delete("{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void getMicrofrontendTest() {

        var dto = given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "m1")
                .get("{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(MicrofrontendDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getProductName()).isEqualTo("product1");
        assertThat(dto.getDisplayName()).isEqualTo("display_name1");

        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "___")
                .get("{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void searchMicrofrontendsTest() {
        var criteria = new MicrofrontendSearchCriteriaDTO();

        var data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicrofrontendPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(2);
        assertThat(data.getStream()).isNotNull().hasSize(2);

        criteria.setProductName("product1");
        criteria.setDisplayName("display_name1");

        data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicrofrontendPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(1);
        assertThat(data.getStream()).isNotNull().hasSize(1);

        criteria.setProductName(" ");
        criteria.setDisplayName(" ");

        data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicrofrontendPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(2);
        assertThat(data.getStream()).isNotNull().hasSize(2);
    }

    @Test
    void searchProductsNoBodyTest() {
        var data = given()
                .contentType(APPLICATION_JSON)
                .post("/search")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(RestExceptionDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getMessage()).isEqualTo("searchMicrofrontends.microfrontendSearchCriteriaDTO: must not be null");
    }

    @Test
    void updateMicrofrontendTest() {
        UpdateMicrofrontendDTO updateDto = new UpdateMicrofrontendDTO();
        updateDto.setBasePath("basePath");
        updateDto.setMfeId("mfeId");
        updateDto.setExposedModule("exposed-module");
        updateDto.setRemoteBaseUrl("remote-base-url");
        updateDto.setRemoteEntry("remote-entry");
        updateDto.setRemoteName("remote-name");
        updateDto.setModuleType(ModuleTypeDTO.ANGULAR);
        updateDto.setDisplayName("display-name");
        updateDto.setProductName("product-name");
        updateDto.setRemoteBaseUrl("remote-base-url");

        given()
                .contentType(APPLICATION_JSON)
                .body(updateDto)
                .when()
                .pathParam("id", "does-not-exists")
                .put("{id}")
                .then().log().all()
                .statusCode(NOT_FOUND.getStatusCode());

        given()
                .contentType(APPLICATION_JSON)
                .body(updateDto)
                .when()
                .pathParam("id", "m1")
                .put("{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        var dto = given().contentType(APPLICATION_JSON)
                .body(updateDto)
                .when()
                .pathParam("id", "m1")
                .get("{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(MicrofrontendDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getProductName()).isEqualTo(updateDto.getProductName());
    }

    @Test
    void updateMicrofrontendWithoutBodyTest() {

        var exception = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "update_create_new")
                .put("{id}")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(RestExceptionDTO.class);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("CONSTRAINT_VIOLATIONS", exception.getErrorCode());
        Assertions.assertEquals("updateMicrofrontend.updateMicrofrontendDTO: must not be null",
                exception.getMessage());
        Assertions.assertNotNull(exception.getValidations());
        Assertions.assertEquals(1, exception.getValidations().size());
    }
}
