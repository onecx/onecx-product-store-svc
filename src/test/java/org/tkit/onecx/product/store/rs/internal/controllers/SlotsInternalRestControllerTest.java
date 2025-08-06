package org.tkit.onecx.product.store.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.product.store.AbstractTest;
import org.tkit.onecx.product.store.rs.internal.mappers.ExceptionMapper;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.product.store.rs.internal.model.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(SlotInternalRestController.class)
@WithDBData(value = "data/test-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-ps:read", "ocx-ps:write", "ocx-ps:delete", "ocx-ps:all" })
class SlotsInternalRestControllerTest extends AbstractTest {

    @Test
    void createSlotsTest() {

        var data = new CreateSlotRequestDTO().appId("n-a1")
                .name("n-1");

        var response = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(data)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract()
                .body().as(ProblemDetailResponseDTO.class);

        assertThat(response).isNotNull();

        assertThat(response.getErrorCode()).isEqualTo(ExceptionMapper.ErrorKeys.CONSTRAINT_VIOLATIONS.name());
        assertThat(response.getDetail()).isEqualTo("createSlot.createSlotRequestDTO.productName: must not be null");

        data.setProductName("p-1");

        var slot = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(data)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(SlotDTO.class);

        assertThat(slot).isNotNull();
        assertThat(slot.getName()).isNotNull().isEqualTo(data.getName());

        response = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(data)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract()
                .body().as(ProblemDetailResponseDTO.class);

        assertThat(response).isNotNull();

        assertThat(response.getErrorCode()).isEqualTo("PERSIST_ENTITY_FAILED");
        assertThat(response.getDetail()).isEqualTo(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'slot_product_name_app_id'  Detail: Key (name, product_name, app_id)=(n-1, p-1, n-a1) already exists.]");
    }

    @Test
    void updateSlotTest() {
        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "s1")
                .get("{id}")
                .then().statusCode(OK.getStatusCode())
                .extract()
                .body().as(SlotDTO.class);

        assertThat(dto).isNotNull();

        var request = new UpdateSlotRequestDTO().appId(dto.getAppId())
                .productName("new-product-1")
                .name(dto.getName())
                .deprecated(true)
                .undeployed(dto.getUndeployed());

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .pathParam("id", "does-not-exists")
                .put("{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .pathParam("id", "s1")
                .put("{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        var dto2 = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "s1")
                .get("{id}")
                .then().statusCode(OK.getStatusCode())
                .extract()
                .body().as(SlotDTO.class);

        assertThat(dto2).isNotNull();
        assertThat(dto2.getDeprecated()).isEqualTo(dto.getDeprecated());
        assertThat(dto2.getOperator()).isEqualTo(dto.getOperator());
        assertThat(dto2.getProductName()).isEqualTo(request.getProductName());

    }

    @Test
    void deleteSlotsTest() {

        // ensure exists
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "s1")
                .get("{id}")
                .then().statusCode(OK.getStatusCode());

        // delete existing
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "s1")
                .delete("{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        // verify successful deletion by GET call
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "s1")
                .get("{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        // delete does not existing
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "does-not-exists")
                .delete("{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

    }

    @Test
    void searchMSlotTest() {
        var criteria = new SlotSearchCriteriaDTO();

        var response = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(SlotPageResultDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(3);
        assertThat(response.getStream()).isNotNull().hasSize(3);

        criteria.appId("notExisting");

        response = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(SlotPageResultDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isZero();
        assertThat(response.getStream()).isNotNull().isEmpty();

        criteria.productName("product1").appId("a1").name("slot1");

        response = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(SlotPageResultDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getStream()).isNotNull().hasSize(1);
    }
}
