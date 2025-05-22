package org.tkit.onecx.product.store.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.product.store.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.product.store.rs.internal.model.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(MicroservicesInternalRestController.class)
@WithDBData(value = "data/test-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-ps:read", "ocx-ps:write", "ocx-ps:delete", "ocx-ps:all" })
class MicroservicesInternalRestControllerTest extends AbstractTest {

    /**
     * Scenario: Create new microservice (ms) in case product name and app-id path are unique (not yet existing). Full
     * comparison of full defined ms object.
     * Given
     * When I try to create a ms with a non-existing (in backend) product name and app-id
     * Then I get a 'OK' response code back
     * AND created ms is returned
     */
    @Test
    void createMicroservice_shouldAddNewMicroservice_whenProductnameAndAppIdAreUnique() {
        CreateMicroserviceRequestDTO request = createMicroserviceCreateRequest("App-ID-5", "1.0.0",
                "AppName-5", "some description", "ProductNamev5");

        // create
        var responseCreationRequest = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(MicroserviceDTO.class);

        // try to fetch newly created object
        var responseGetRequest = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", responseCreationRequest.getId())
                .get("{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(MicroserviceDTO.class);

        assertThat(responseCreationRequest).isNotNull();
        assertThat(responseGetRequest).isNotNull();
        assertThat(responseGetRequest.getId()).isNotNull();
        assertThat(responseGetRequest.getCreationDate()).isNotNull();
        assertThat(responseGetRequest.getModificationDate()).isNotNull();

        assertThat(responseGetRequest.getModificationCount()).isNotNull()
                .isEqualTo(0);
        assertThat(responseGetRequest.getAppId()).isNotNull()
                .isEqualTo(responseCreationRequest.getAppId());
        assertThat(responseGetRequest.getVersion()).isNotNull()
                .isEqualTo(responseCreationRequest.getVersion());
        assertThat(responseGetRequest.getName()).isNotNull()
                .isEqualTo(responseCreationRequest.getName());
        assertThat(responseGetRequest.getDescription()).isNotNull()
                .isEqualTo(responseCreationRequest.getDescription());
        assertThat(responseGetRequest.getProductName()).isNotNull()
                .isEqualTo(responseCreationRequest.getProductName());
    }

    /**
     * Scenario: Throw 400 Bad Request exception when provided product name and product id are already used for another
     * microservice.
     * Given
     * When I try to create a ms with used app id and product name
     * Then I get a 'Bad Request' response code back
     * AND problem details are within the response body
     */
    @Test
    void createMicroservice_shouldReturnBadRequest_whenViolatingValueUniqueness() {
        CreateMicroserviceRequestDTO request = createMicroserviceCreateRequest("msId", "1.0.0",
                "AppName-5", "some description", "product-1");

        // first shot (success)
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(MicroserviceDTO.class);

        // second shot (violates unique constraint)
        var responseCreationRequest = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract()
                .body().as(ProblemDetailResponseDTO.class);

        assertThat(responseCreationRequest).isNotNull();
        List<ProblemDetailParamDTO> problemDetailsParams = responseCreationRequest.getParams();
        Optional<ProblemDetailParamDTO> paramConstraint = problemDetailsParams.stream()
                .filter(e -> e.getKey().equals("constraint"))
                .findFirst();
        Optional<ProblemDetailParamDTO> paramConstraintName = problemDetailsParams.stream()
                .filter(e -> e.getKey().equals("constraintName"))
                .findFirst();

        assertThat(paramConstraint).isPresent();
        assertThat(paramConstraintName).isPresent();
        assertThat(responseCreationRequest.getErrorCode()).isEqualTo("PERSIST_ENTITY_FAILED");
        assertThat(responseCreationRequest.getDetail()).isEqualTo(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'microservice_app_id'  Detail: Key (product_name, app_id)=(product-1, msId) already exists.]");

    }

    /**
     * Scenario: Throw 400 Bad Request when mandatory field(s) is/are missing.
     * Given
     * When I try to create a ms without setting all mandatory fields
     * Then I get a 'Bad Request' response code back
     * AND problem details are within the response body
     */
    @Test
    void createMicroservice_shouldReturnBadRequest_whenRunningIntoValidationConstraints() {
        CreateMicroserviceRequestDTO request = createMicroserviceCreateRequest(null, "1.0.0",
                "AppName-5", "some description", "");

        var response = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract()
                .body().as(ProblemDetailResponseDTO.class);

        List<ProblemDetailInvalidParamDTO> problemDetailsParams = response.getInvalidParams();
        Optional<ProblemDetailInvalidParamDTO> invalidParamConstraint = problemDetailsParams.stream().findFirst();

        assertThat(response).isNotNull();
        assertThat(invalidParamConstraint).isPresent();

        assertThat(response.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(response.getDetail()).isEqualTo("createMicroservice.createMicroserviceRequestDTO.appId: must not be null");
        assertThat(invalidParamConstraint.get().getMessage()).isEqualTo("must not be null");
        assertThat(invalidParamConstraint.get().getName()).isEqualTo("createMicroservice.createMicroserviceRequestDTO.appId");
        assertThat(response.getParams()).isEmpty();
    }

    /**
     * Scenario: Delete microservice (ms) by existing ms id.
     * Given
     * When I try to delete a ms by existing ms id
     * Then I get a 'No Content' response code back
     */
    @Test
    void deleteMicroservice_shouldDeleteMicroservice() {
        // ensure ms exists
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "m1")
                .get("{id}")
                .then().statusCode(OK.getStatusCode());

        // delete existing ms
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "m1")
                .delete("{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        // verify successful deletion by GET call
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "m1")
                .get("{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

    }

    /**
     * Scenario: Delete microservice (ms) by non-existing ms id.
     * Given
     * When I try to delete a ms by non-existing ms id
     * Then I get a 'No Content' response code back
     */
    @Test
    void deleteMicroservice_shouldReturnNoContent_whenMFEIdDoesNotExist() {

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "notExistingMFEId")
                .delete("{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    /**
     * Scenario: Receives microservice (ms) by ms-id successfully (full attribute check).
     * Given
     * When I query GET endpoint with an existing id
     * Then I get a 'OK' response code back
     * AND associated ms is returned
     */
    @Test
    void getMicroservice_shouldReturnMicroservice() {
        var responseGetRequest = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "ms1")
                .get("{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(MicroserviceDTO.class);

        assertThat(responseGetRequest).isNotNull();
        assertThat(responseGetRequest.getId()).isNotNull();
        assertThat(responseGetRequest.getCreationDate()).isNotNull();
        assertThat(responseGetRequest.getModificationDate()).isNotNull();

        assertThat(responseGetRequest.getModificationCount()).isNotNull()
                .isEqualTo(0);
        assertThat(responseGetRequest.getAppId()).isNotNull()
                .isEqualTo("msOne");
        assertThat(responseGetRequest.getVersion()).isNotNull()
                .isEqualTo("1.0.0");
        assertThat(responseGetRequest.getName()).isNotNull()
                .isEqualTo("displayname-1");
        assertThat(responseGetRequest.getDescription()).isNotNull()
                .isEqualTo("some text");
        assertThat(responseGetRequest.getProductName()).isNotNull()
                .isEqualTo("productOne");
    }

    /**
     * Scenario: Receive 404 Not Found when microservice (ms) is not existing.
     * Given
     * When I query GET endpoint with a non-existing id
     * Then I get a 'Not Found' response code back
     */
    @Test
    void getMicroservice_shouldReturnNotFound_whenMFEIdDoesNotExist() {
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "notExisting")
                .get("{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());
    }

    /**
     * Scenario: Search request for non-matching criteria results into successful response with empty ms list.
     * Given
     * When I search with criteria for which no ms(s) exist(s) matching the search criteria
     * Then I get a 'OK' response code back
     * AND empty ms list is returned within
     */
    @Test
    void searchMicroservices_shouldReturnEmptyList_whenSearchCriteriaDoesNotMatch() {
        var criteria = new MicroserviceSearchCriteriaDTO();
        criteria.setAppId("notExisting");
        criteria.setName("notExisting");

        var response = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicroservicePageResultDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isZero();
        assertThat(response.getStream()).isNotNull().isEmpty();
    }

    @Test
    void searchMicroservices_missing_and_blank_criteria_Test() {
        var criteriaBlank = new MicroserviceSearchCriteriaDTO();
        criteriaBlank.setAppId("");
        criteriaBlank.setName("");
        criteriaBlank.setProductName("");

        var response = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteriaBlank)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicroservicePageResultDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getStream()).isNotNull().hasSize(3);

        var criteriaMissing = new MicroserviceSearchCriteriaDTO();

        var response2 = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteriaMissing)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicroservicePageResultDTO.class);

        assertThat(response2).isNotNull();
        assertThat(response2.getStream()).isNotNull().hasSize(3);
    }

    /**
     * Scenario: Search request with matching criteria results into response with a single microservice (ms) in list matching
     * the
     * criteria, according to the uniqueness (constraints).
     * Given
     * When I search with criteria for which at least a ms exists matching the search criteria
     * Then I get a 'OK' response code back
     * AND corresponding ms is returned within the list
     */
    @Test
    void searchMicroservices_shouldReturnMicroserviceListWithSingleElement_whenSearchCriteriaDoesMatch() {

        // 3 matching search criteria
        var criteria = new MicroserviceSearchCriteriaDTO();
        criteria.setProductName("product1");
        criteria.setAppId("ms1");
        criteria.setName("coolname");

        var response = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicroservicePageResultDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getStream()).isNotNull().hasSize(1);

        // single matching search criteria
        var criteria2 = new MicroserviceSearchCriteriaDTO();
        criteria2.setAppId("ms1");

        var response2 = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteria2)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicroservicePageResultDTO.class);

        assertThat(response2).isNotNull();
        assertThat(response2.getTotalElements()).isEqualTo(1);
        assertThat(response2.getStream()).isNotNull().hasSize(1);

        // 2 matching search criteria
        var criteria3 = new MicroserviceSearchCriteriaDTO();
        criteria3.setAppId("ms1");
        criteria.setProductName("product1");

        var response3 = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteria2)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicroservicePageResultDTO.class);

        assertThat(response3).isNotNull();
        assertThat(response3.getTotalElements()).isEqualTo(1);
        assertThat(response3.getStream()).isNotNull().hasSize(1);
    }

    /**
     * Scenario: Wildcard search to return all or subset of available microservices (ms).
     * Given ms(s) exist(s)
     * When I search with no criteria for the same (wildcard)
     * Then I get a 'OK' response code back
     * AND a product list is returned within the defined range of product-page-size-numbers
     */
    @Test
    void searchMicroservices_shouldReturnMicroserviceList_whenSearchCriteriaIsNotGivenButMFEsAreAvailable() {

        var criteria = new MicroserviceSearchCriteriaDTO();

        var response = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicroservicePageResultDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(3);
        assertThat(response.getStream()).isNotNull().hasSize(3);
    }

    /**
     * Scenario: Throw 400 Bad Request when mandatory field (body) is missing.
     * Given
     * When I try to search for mss without setting all mandatory fields (body in general)
     * Then I get a 'Bad Request' response code back
     * AND problem details are within the response body
     */
    @Test
    void searchMicroservices_shouldReturnBadRequest_whenRunningIntoValidationConstraints() {
        var response = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .post("/search")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProblemDetailResponseDTO.class);

        List<ProblemDetailInvalidParamDTO> problemDetailsParams = response.getInvalidParams();
        Optional<ProblemDetailInvalidParamDTO> invalidParamConstraint = problemDetailsParams.stream().findFirst();

        assertThat(response).isNotNull();
        assertThat(invalidParamConstraint).isPresent();

        assertThat(response.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(response.getDetail()).isEqualTo("searchMicroservice.microserviceSearchCriteriaDTO: must not be null");
        assertThat(invalidParamConstraint.get().getMessage()).isEqualTo("must not be null");
        assertThat(invalidParamConstraint.get().getName()).isEqualTo("searchMicroservice.microserviceSearchCriteriaDTO");
        assertThat(response.getParams()).isEmpty();
    }

    /**
     * Scenario: Update existing microservice (ms) successfully.
     * Given
     * When I try to update a ms by existing ms id
     * Then I get a 'No Content' response code back
     * AND ms entity is updated.
     */
    @Test
    void updateMicroservice_shouldUpdateMicroservice() {
        String msId = "m1";

        UpdateMicroserviceRequestDTO request = createMicroserviceUpdateRequest("ms1", "1.0.0",
                "display_name1", "some description", "product1");

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .pathParam("id", msId)
                .put("{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // Verify update
        var responseGetRequest = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", msId)
                .get("{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(MicroserviceDTO.class);

        assertThat(responseGetRequest).isNotNull();
        assertThat(responseGetRequest.getId()).isNotNull();

        assertThat(responseGetRequest.getModificationCount()).isNotNull()
                .isEqualTo(1);
        assertThat(responseGetRequest.getAppId()).isNotNull()
                .isEqualTo("ms1");
        assertThat(responseGetRequest.getVersion()).isNotNull()
                .isEqualTo("1.0.0");
        assertThat(responseGetRequest.getName()).isNotNull()
                .isEqualTo("display_name1");
        assertThat(responseGetRequest.getDescription()).isNotNull()
                .isEqualTo("some description");
        assertThat(responseGetRequest.getProductName()).isNotNull()
                .isEqualTo("product1");
    }

    /**
     * Scenario: Return 404-status code when trying to update microservice (ms) by a non-existing ms id.
     * Given ms-ID is not existing
     * When I try to update a ms by a non-existing ms id "nonExisting"
     * Then I get a 'Not Found' response code back
     */
    @Test
    void updateMicroservice_shouldReturnNotFound_whenMicroserviceIdDoesNotExist() {
        UpdateMicroserviceRequestDTO request = createMicroserviceUpdateRequest("Somechanges", "1.0.0",
                "AppName-5", "some description", "ProductName");

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .pathParam("id", "notExisting")
                .put("{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    /**
     * Scenario: Return 400-status code with exception when trying to update microservice (ms) by an already used app
     * name for same product association.
     * Given ms-ID is already used by another ms-A
     * When I try to update ms-B by used value for app id for same productName association
     * Then I get a 'Bad Request' response code back
     * AND a ProblemDetailResponseObject is returned
     */
    @Test
    void updateMicroservice_shouldReturnBadRequest_whenRunningIntoUniquenessViolationConstraints() {
        UpdateMicroserviceRequestDTO request = createMicroserviceUpdateRequest("msOne", "1.0.0",
                "AppName-5", "some description", "productOne");

        var response = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .pathParam("id", "m1")
                .put("{id}")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract()
                .body().as(ProblemDetailResponseDTO.class);

        List<ProblemDetailParamDTO> problemDetailsParams = response.getParams();
        Optional<ProblemDetailParamDTO> paramConstraint = problemDetailsParams.stream()
                .filter(e -> e.getKey().equals("constraint"))
                .findFirst();
        Optional<ProblemDetailParamDTO> paramConstraintName = problemDetailsParams.stream()
                .filter(e -> e.getKey().equals("constraintName"))
                .findFirst();

        assertThat(response).isNotNull();
        assertThat(paramConstraint).isPresent();
        assertThat(paramConstraintName).isPresent();
        assertThat(response.getErrorCode()).isEqualTo("MERGE_ENTITY_FAILED");
        assertThat(response.getDetail()).isEqualTo(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'microservice_app_id'  Detail: Key (product_name, app_id)=(productOne, msOne) already exists.]");
        assertThat(paramConstraint.get().getValue()).isEqualTo(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'microservice_app_id'  Detail: Key (product_name, app_id)=(productOne, msOne) already exists.]");
        assertThat(paramConstraint.get().getKey()).isEqualTo("constraint");
        assertThat(paramConstraintName.get().getValue()).isEqualTo("microservice_app_id");
        assertThat(paramConstraintName.get().getKey()).isEqualTo("constraintName");
        assertThat(response.getInvalidParams()).isEmpty();
    }

    /**
     * Scenario: Throw 400 Bad Request when request body is missing.
     * Given
     * When I try to update a ms without request body
     * Then I get a 'Bad Request' response code back
     * AND problem details are within the response body
     */
    @Test
    void updateMicroservice_shouldReturnBadRequest_whenRunningIntoValidationConstraintsEmptyBody() {

        var exception = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "update_create_new")
                .put("{id}")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("CONSTRAINT_VIOLATIONS", exception.getErrorCode());
        Assertions.assertEquals("updateMicroservice.updateMicroserviceRequestDTO: must not be null",
                exception.getDetail());
        Assertions.assertNotNull(exception.getInvalidParams());
        Assertions.assertEquals(1, exception.getInvalidParams().size());
    }

    /**
     * Helper method to create a microservice (ms) create request object (inbound call).
     *
     * @param appId unique id of application
     * @param version version number
     * @param name name of application
     * @param description general ms description
     * @param productName name of associated product
     * @return
     */
    private CreateMicroserviceRequestDTO createMicroserviceCreateRequest(
            String appId,
            String version,
            String name,
            String description,
            String productName) {
        CreateMicroserviceRequestDTO ms = new CreateMicroserviceRequestDTO();
        ms.setAppId(appId);
        ms.version(version);
        ms.setName(name);
        ms.setDescription(description);
        ms.setProductName(productName);
        return ms;
    }

    /**
     * Helper method to create a microservice (ms) update request object (inbound call).
     *
     * @param appId unique id of application
     * @param version version number
     * @param name unique name of application
     * @param description general ms description
     * @param productName name of associated product
     * @return
     */
    private UpdateMicroserviceRequestDTO createMicroserviceUpdateRequest(
            String appId,
            String version,
            String name,
            String description,
            String productName) {

        UpdateMicroserviceRequestDTO ms = new UpdateMicroserviceRequestDTO();
        ms.setAppId(appId);
        ms.setVersion(version);
        ms.setName(name);
        ms.setDescription(description);
        ms.setProductName(productName);
        return ms;
    }

}
