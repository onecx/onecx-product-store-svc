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
@WithDBData(value = "data/test-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class MicrofrontendsInternalRestControllerTest extends AbstractTest {

    @Test
    void createMicrofrontendTest() {

        CreateMicrofrontendRequestDTO createDto = new CreateMicrofrontendRequestDTO();
        createDto.setAppId("mfeId");
        createDto.setAppVersion("0.0.0");
        createDto.setExposedModule("exposed-module");
        createDto.setRemoteBaseUrl("remote-base-url");
        createDto.setRemoteEntry("remote-entry");
        createDto.setAppName("display-name");
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
        assertThat(dto.getAppName()).isNotNull().isEqualTo(createDto.getAppName());

        // create theme without body
        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getDetail()).isEqualTo("createMicrofrontend.createMicrofrontendRequestDTO: must not be null");

        // create theme with existing name
        exception = given().when()
                .contentType(APPLICATION_JSON)
                .body(createDto)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("PERSIST_ENTITY_FAILED");
        assertThat(exception.getDetail()).isEqualTo(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'ps_microfrontend_app_id'  Detail: Key (product_name, app_id)=(product-name, mfeId) already exists.]");

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
        assertThat(dto.getAppName()).isEqualTo("display_name1");

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
        criteria.setAppId("mfe1");
        criteria.setAppName("display_name1");

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
        criteria.setAppId(" ");
        criteria.setAppName(" ");

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
                .as(ProblemDetailResponseDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getDetail()).isEqualTo("searchMicrofrontends.microfrontendSearchCriteriaDTO: must not be null");
    }

    @Test
    void updateMicrofrontendTest() {
        UpdateMicrofrontendRequestDTO updateDto = new UpdateMicrofrontendRequestDTO();
        updateDto.setAppId("mfeId");
        updateDto.setAppVersion("0.0.0");
        updateDto.setExposedModule("exposed-module");
        updateDto.setRemoteBaseUrl("remote-base-url");
        updateDto.setRemoteEntry("remote-entry");
        updateDto.setAppName("display-name");
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
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("CONSTRAINT_VIOLATIONS", exception.getErrorCode());
        Assertions.assertEquals("updateMicrofrontend.updateMicrofrontendRequestDTO: must not be null",
                exception.getDetail());
        Assertions.assertNotNull(exception.getInvalidParams());
        Assertions.assertEquals(1, exception.getInvalidParams().size());
    }
}
