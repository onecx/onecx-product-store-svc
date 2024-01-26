package io.github.onecx.product.store.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

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

    /**
     * Scenario: Create new microfrontend (mfe) in case product name and app-id path are unique (not yet existing). Full
     * comparison of full defined mfe object.
     * Given
     * When I try to create a mfe with a non-existing (in backend) product name and app-id
     * Then I get a 'OK' response code back
     * AND created mfe is returned
     */
    @Test
    void createMicrofrontend_shouldAddNewMicrofrontend_whenProductnameAndAppIdAreUnique() {
        List<CreateUIEndpointDTO> uiEndpointSetForRequest = new ArrayList<>();
        CreateUIEndpointDTO uiEndpointItemForRequest = new CreateUIEndpointDTO();
        uiEndpointItemForRequest.setName("Pages");
        uiEndpointItemForRequest.setPath("/pages");
        uiEndpointSetForRequest.add(uiEndpointItemForRequest);
        CreateMicrofrontendRequestDTO request = createMicrofrontendCreateRequest("App-ID-5", "1.0.0",
                "AppName-5", "some description", "", "https://localhost/mfe/core/ah-mgmtv5/",
                "https://localhost/mfe/core/ah-mgmtv5/remoteEntry.js", "ProductNamev5", "Gaming,Test",
                "developers@1000kit.org", "sun", "some notes", "/AnnouncementManagementModulev5", uiEndpointSetForRequest);

        // create
        var responseCreationRequest = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(MicrofrontendDTO.class);

        // try to fetch newly created object
        var responseGetRequest = given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", responseCreationRequest.getId())
                .get("{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(MicrofrontendDTO.class);

        assertThat(responseCreationRequest).isNotNull();
        assertThat(responseGetRequest).isNotNull();
        assertThat(responseGetRequest.getId()).isNotNull();
        assertThat(responseGetRequest.getCreationDate()).isNotNull();
        assertThat(responseGetRequest.getModificationDate()).isNotNull();

        assertThat(responseGetRequest.getModificationCount()).isNotNull()
                .isEqualTo(0);
        assertThat(responseGetRequest.getAppId()).isNotNull()
                .isEqualTo(responseCreationRequest.getAppId());
        assertThat(responseGetRequest.getAppVersion()).isNotNull()
                .isEqualTo(responseCreationRequest.getAppVersion());
        assertThat(responseGetRequest.getAppName()).isNotNull()
                .isEqualTo(responseCreationRequest.getAppName());
        assertThat(responseGetRequest.getDescription()).isNotNull()
                .isEqualTo(responseCreationRequest.getDescription());
        assertThat(responseGetRequest.getTechnology()).isNotNull()
                .isEqualTo(responseCreationRequest.getTechnology());
        assertThat(responseGetRequest.getRemoteBaseUrl()).isNotNull()
                .isEqualTo(responseCreationRequest.getRemoteBaseUrl());
        assertThat(responseGetRequest.getRemoteEntry()).isNotNull()
                .isEqualTo(responseCreationRequest.getRemoteEntry());
        assertThat(responseGetRequest.getProductName()).isNotNull()
                .isEqualTo(responseCreationRequest.getProductName());
        assertThat(responseGetRequest.getContact()).isNotNull()
                .isEqualTo(responseCreationRequest.getContact());
        assertThat(responseGetRequest.getIconName()).isNotNull()
                .isEqualTo(responseCreationRequest.getIconName());
        assertThat(responseGetRequest.getNote()).isNotNull()
                .isEqualTo(responseCreationRequest.getNote());
        assertThat(responseGetRequest.getExposedModule()).isNotNull()
                .isEqualTo(responseCreationRequest.getExposedModule());

        //classifications

        assertThat(responseGetRequest.getClassifications()).isEqualTo("Gaming,Test");

        // endpoints
        List<UIEndpointDTO> endpointsResponse = responseGetRequest.getEndpoints();
        Optional<UIEndpointDTO> endpointsResponseItem = endpointsResponse.stream()
                .filter(e -> e.getName().equals(request.getEndpoints().get(0).getName()))
                .findFirst();
        assertThat(endpointsResponseItem).isPresent();
        assertThat(responseGetRequest.getEndpoints().get(0).getName()).isNotNull()
                .isEqualTo(endpointsResponseItem.get().getName());
        assertThat(responseGetRequest.getEndpoints().get(0).getPath()).isNotNull()
                .isEqualTo(endpointsResponseItem.get().getPath());

    }

    /**
     * Scenario: Throw 400 Bad Request exception when provided product name and product id are already used for another
     * microfrontend.
     * Given
     * When I try to create a mfe with used app id and product name
     * Then I get a 'Bad Request' response code back
     * AND problem details are within the response body
     */
    @Test
    void createMicrofrontend_shouldReturnBadRequest_whenViolatingValueUniqueness() {
        CreateMicrofrontendRequestDTO request = createMicrofrontendCreateRequest("mfeId", "1.0.0",
                "AppName-5", "some description", "", "https://localhost/mfe/core/ah-mgmtv5/",
                "https://localhost/mfe/core/ah-mgmtv5/remoteEntry.js", "ProductName", null,
                "developers@1000kit.org", "sun", "some notes", "/AnnouncementManagementModulev5", null);

        // first shot (success)
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(MicrofrontendDTO.class);

        // second shot (violates unique constraint)
        var responseCreationRequest = given()
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
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'ps_microfrontend_app_id'  Detail: Key (product_name, app_id)=(ProductName, mfeId) already exists.]");

    }

    /**
     * Scenario: Throw 400 Bad Request when mandatory field(s) is/are missing.
     * Given
     * When I try to create a mfe without setting all mandatory fields
     * Then I get a 'Bad Request' response code back
     * AND problem details are within the response body
     */
    @Test
    void createMicrofrontend_shouldReturnBadRequest_whenRunningIntoValidationConstraints() {
        CreateMicrofrontendRequestDTO request = createMicrofrontendCreateRequest(null, "1.0.0",
                "AppName-5", "some description", "", "https://localhost/mfe/core/ah-mgmtv5/",
                "https://localhost/mfe/core/ah-mgmtv5/remoteEntry.js", "ProductName", null,
                "developers@1000kit.org", "sun", "some notes", "/AnnouncementManagementModulev5", null);

        var response = given()
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
        assertThat(response.getDetail()).isEqualTo("createMicrofrontend.createMicrofrontendRequestDTO.appId: must not be null");
        assertThat(invalidParamConstraint.get().getMessage()).isEqualTo("must not be null");
        assertThat(invalidParamConstraint.get().getName()).isEqualTo("createMicrofrontend.createMicrofrontendRequestDTO.appId");
        assertThat(response.getParams()).isNull();
    }

    /**
     * Scenario: Delete microfrontend (mfe) by existing mfe id.
     * Given
     * When I try to delete a mfe by existing mfe id
     * Then I get a 'No Content' response code back
     */
    @Test
    void deleteMicrofrontend_shouldDeleteMicrofrontend() {
        // ensure mfe exists
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "m1")
                .get("{id}")
                .then().statusCode(OK.getStatusCode());

        // delete existing mfe
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "m1")
                .delete("{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        // verify successful deletion by GET call
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "m1")
                .get("{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

    }

    /**
     * Scenario: Delete microfrontend (mfe) by non-existing mfe id.
     * Given
     * When I try to delete a mfe by non-existing mfe id
     * Then I get a 'No Content' response code back
     */
    @Test
    void deleteMicrofrontend_shouldReturnNoContent_whenMFEIdDoesNotExist() {

        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "notExistingMFEId")
                .delete("{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    /**
     * Scenario: Receives microfrontend (mfe) by mfe-id successfully (full attribute check).
     * Given
     * When I query GET endpoint with an existing id
     * Then I get a 'OK' response code back
     * AND associated mfe is returned
     */
    @Test
    void getMicrofrontend_shouldReturnMicrofrontend() {
        var responseGetRequest = given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "mfe1")
                .get("{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(MicrofrontendDTO.class);

        assertThat(responseGetRequest).isNotNull();
        assertThat(responseGetRequest.getId()).isNotNull();
        assertThat(responseGetRequest.getCreationDate()).isNotNull();
        assertThat(responseGetRequest.getModificationDate()).isNotNull();

        assertThat(responseGetRequest.getModificationCount()).isNotNull()
                .isEqualTo(0);
        assertThat(responseGetRequest.getAppId()).isNotNull()
                .isEqualTo("mfeOne");
        assertThat(responseGetRequest.getAppVersion()).isNotNull()
                .isEqualTo("1.0.0");
        assertThat(responseGetRequest.getAppName()).isNotNull()
                .isEqualTo("coolname");
        assertThat(responseGetRequest.getDescription()).isNotNull()
                .isEqualTo("some text");
        assertThat(responseGetRequest.getTechnology()).isNotNull()
                .isEqualTo("Angular");
        assertThat(responseGetRequest.getRemoteBaseUrl()).isNotNull()
                .isEqualTo("https://localhost/mfe/core/ah-mgmt/");
        assertThat(responseGetRequest.getRemoteEntry()).isNotNull()
                .isEqualTo("https://localhost/mfe/core/ah-mgmtv5/remoteEntry.js");
        assertThat(responseGetRequest.getProductName()).isNotNull()
                .isEqualTo("productOne");
        assertThat(responseGetRequest.getContact()).isNotNull()
                .isEqualTo("developers@1000kit.org");
        assertThat(responseGetRequest.getIconName()).isNotNull()
                .isEqualTo("sun");
        assertThat(responseGetRequest.getNote()).isNotNull()
                .isEqualTo("some notes");
        assertThat(responseGetRequest.getExposedModule()).isNotNull()
                .isEqualTo("/AnnouncementManagementModule");

        //classifications
        assertThat(responseGetRequest.getClassifications())
                .hasSize(1);
        assertThat(responseGetRequest.getClassifications()).contains("searching");

        // endpoints
        List<UIEndpointDTO> endpointsResponse = responseGetRequest.getEndpoints();
        Optional<UIEndpointDTO> endpointsResponseItem = endpointsResponse.stream()
                .filter(e -> e.getName().equals("search")).findFirst();
        assertThat(endpointsResponseItem).isPresent();
        assertThat(responseGetRequest.getEndpoints().get(0).getName()).isNotNull()
                .isEqualTo(endpointsResponseItem.get().getName());
        assertThat(responseGetRequest.getEndpoints().get(0).getPath()).isNotNull()
                .isEqualTo(endpointsResponseItem.get().getPath());

    }

    /**
     * Scenario: Receive 404 Not Found when microfrontend (mfe) is not existing.
     * Given
     * When I query GET endpoint with a non-existing id
     * Then I get a 'Not Found' response code back
     */
    @Test
    void getMicrofrontend_shouldReturnNotFound_whenMFEIdDoesNotExist() {
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "notExisting")
                .get("{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());
    }

    /**
     * Scenario: Search request for non-matching criteria results into successful response with empty mfe list.
     * Given
     * When I search with criteria for which no mfe(s) exist(s) matching the search criteria
     * Then I get a 'OK' response code back
     * AND empty mfe list is returned within
     */
    @Test
    void searchMicrofrontends_shouldReturnEmptyList_whenSearchCriteriaDoesNotMatch() {
        var criteria = new MicrofrontendSearchCriteriaDTO();
        criteria.setAppId("notExisting");

        var response = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicrofrontendPageResultDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isZero();
        assertThat(response.getStream()).isNotNull().isEmpty();
    }

    /**
     * Scenario: Search request with matching criteria results into response with a single microfrontend (mfe) in list matching
     * the
     * criteria, according to the uniqueness (constraints).
     * Given
     * When I search with criteria for which at least a mfe exists matching the search criteria
     * Then I get a 'OK' response code back
     * AND corresponding mfe is returned within the list
     */
    @Test
    void searchMicrofrontends_shouldReturnMicrofrontendListWithSingleElement_whenSearchCriteriaDoesMatch() {

        // 3 matching search criteria
        var criteria = new MicrofrontendSearchCriteriaDTO();
        criteria.setProductName("product1");
        criteria.setAppId("mfe1");
        criteria.setAppName("display_name1");

        var response = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicrofrontendPageResultDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getStream()).isNotNull().hasSize(1);

        // single matching search criteria
        var criteria2 = new MicrofrontendSearchCriteriaDTO();
        criteria2.setAppId("mfe1");

        var response2 = given()
                .contentType(APPLICATION_JSON)
                .body(criteria2)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicrofrontendPageResultDTO.class);

        assertThat(response2).isNotNull();
        assertThat(response2.getTotalElements()).isEqualTo(1);
        assertThat(response2.getStream()).isNotNull().hasSize(1);

        // 2 matching search criteria
        var criteria3 = new MicrofrontendSearchCriteriaDTO();
        criteria3.setAppId("mfe1");
        criteria.setProductName("product1");

        var response3 = given()
                .contentType(APPLICATION_JSON)
                .body(criteria2)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicrofrontendPageResultDTO.class);

        assertThat(response3).isNotNull();
        assertThat(response3.getTotalElements()).isEqualTo(1);
        assertThat(response3.getStream()).isNotNull().hasSize(1);

    }

    /**
     * Scenario: Wildcard search to return all or subset of available microfrontends (mfe).
     * Given mfe(s) exist(s)
     * When I search with no criteria for the same (wildcard)
     * Then I get a 'OK' response code back
     * AND a product list is returned within the defined range of product-page-size-numbers
     */
    @Test
    void searchMicrofrontends_shouldReturnMicrofrontendList_whenSearchCriteriaIsNotGivenButMFEsAreAvailable() {

        var criteria = new MicrofrontendSearchCriteriaDTO();

        var response = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicrofrontendPageResultDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(3);
        assertThat(response.getStream()).isNotNull().hasSize(3);
    }

    /**
     * Scenario: Throw 400 Bad Request when mandatory field (body) is missing.
     * Given
     * When I try to search for mfes without setting all mandatory fields (body in general)
     * Then I get a 'Bad Request' response code back
     * AND problem details are within the response body
     */
    @Test
    void searchMicrofrontends_shouldReturnBadRequest_whenRunningIntoValidationConstraints() {
        var response = given()
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
        assertThat(response.getDetail()).isEqualTo("searchMicrofrontends.microfrontendSearchCriteriaDTO: must not be null");
        assertThat(invalidParamConstraint.get().getMessage()).isEqualTo("must not be null");
        assertThat(invalidParamConstraint.get().getName()).isEqualTo("searchMicrofrontends.microfrontendSearchCriteriaDTO");
        assertThat(response.getParams()).isNull();
    }

    /**
     * Scenario: Throw 400 Bad Request when mandatory field(s) is/are missing.
     * Given
     * When I try to update a mfe without setting all mandatory fields
     * Then I get a 'Bad Request' response code back
     * AND problem details are within the response body
     */
    @Test
    void updateMicrofrontend_shouldReturnBadRequest_whenRunningIntoValidationConstraints() {
        UpdateMicrofrontendRequestDTO request = createMicrofrontendUpdateRequest(null, "1.0.0",
                "AppName-5", "some description", "", "https://localhost/mfe/core/ah-mgmtv5/",
                "https://localhost/mfe/core/ah-mgmtv5/remoteEntry.js", "ProductName", null,
                "developers@1000kit.org", "sun", "some notes", "/AnnouncementManagementModulev5", null);

        var response = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .pathParam("id", "mfe1")
                .put("{id}")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract()
                .body().as(ProblemDetailResponseDTO.class);

        List<ProblemDetailInvalidParamDTO> problemDetailsParams = response.getInvalidParams();
        Optional<ProblemDetailInvalidParamDTO> invalidParamConstraint = problemDetailsParams.stream().findFirst();

        assertThat(response).isNotNull();
        assertThat(invalidParamConstraint).isPresent();

        assertThat(response.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(response.getDetail()).isEqualTo("updateMicrofrontend.updateMicrofrontendRequestDTO.appId: must not be null");
        assertThat(invalidParamConstraint.get().getMessage()).isEqualTo("must not be null");
        assertThat(invalidParamConstraint.get().getName()).isEqualTo("updateMicrofrontend.updateMicrofrontendRequestDTO.appId");
        assertThat(response.getParams()).isNull();
    }

    /**
     * Scenario: Update existing microfrontend (mfe) successfully.
     * Given
     * When I try to update a mfe by existing mfe id
     * Then I get a 'No Content' response code back
     * AND mfe entity is updated.
     */
    @Test
    void updateMicrofrontend_shouldUpdateMicrofrontend() {
        String mfeId = "m1";

        List<UpdateUIEndpointDTO> uiEndpointSetForRequest = new ArrayList<>();
        UpdateUIEndpointDTO uiEndpointItemForRequest = new UpdateUIEndpointDTO();
        uiEndpointItemForRequest.setName("search");
        uiEndpointItemForRequest.setPath("/search");
        uiEndpointSetForRequest.add(uiEndpointItemForRequest);

        UpdateMicrofrontendRequestDTO request = createMicrofrontendUpdateRequest("mfe1", "1.0.0",
                "display_name1", "some description", "Angular", "https://localhost/mfe/core/ah-mgmtv5/",
                "https://localhost/mfe/core/ah-mgmtv5/remoteEntry.js", "product1", "Gaming,Test",
                "developers@1000kit.org", "sun", "some notes", "/AnnouncementManagementModulev5", uiEndpointSetForRequest);

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .pathParam("id", mfeId)
                .put("{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // Verify update
        var responseGetRequest = given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", mfeId)
                .get("{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(MicrofrontendDTO.class);

        assertThat(responseGetRequest).isNotNull();
        assertThat(responseGetRequest.getId()).isNotNull();

        assertThat(responseGetRequest.getModificationCount()).isNotNull()
                .isEqualTo(1);
        assertThat(responseGetRequest.getAppId()).isNotNull()
                .isEqualTo("mfe1");
        assertThat(responseGetRequest.getAppVersion()).isNotNull()
                .isEqualTo("1.0.0");
        assertThat(responseGetRequest.getAppName()).isNotNull()
                .isEqualTo("display_name1");
        assertThat(responseGetRequest.getDescription()).isNotNull()
                .isEqualTo("some description");
        assertThat(responseGetRequest.getTechnology()).isNotNull()
                .isEqualTo("Angular");
        assertThat(responseGetRequest.getRemoteBaseUrl()).isNotNull()
                .isEqualTo("https://localhost/mfe/core/ah-mgmtv5/");
        assertThat(responseGetRequest.getRemoteEntry()).isNotNull()
                .isEqualTo("https://localhost/mfe/core/ah-mgmtv5/remoteEntry.js");
        assertThat(responseGetRequest.getProductName()).isNotNull()
                .isEqualTo("product1");
        assertThat(responseGetRequest.getContact()).isNotNull()
                .isEqualTo("developers@1000kit.org");
        assertThat(responseGetRequest.getIconName()).isNotNull()
                .isEqualTo("sun");
        assertThat(responseGetRequest.getNote()).isNotNull()
                .isEqualTo("some notes");
        assertThat(responseGetRequest.getExposedModule()).isNotNull()
                .isEqualTo("/AnnouncementManagementModulev5");

        //classifications
        assertThat(responseGetRequest.getClassifications()).isEqualTo("Gaming,Test");

        // endpoints
        List<UIEndpointDTO> endpointsResponse = responseGetRequest.getEndpoints();
        Optional<UIEndpointDTO> endpointsResponseItem = endpointsResponse.stream()
                .filter(e -> e.getName().equals("search")).findFirst();
        assertThat(endpointsResponseItem).isPresent();
        assertThat(responseGetRequest.getEndpoints().get(0).getName()).isNotNull()
                .isEqualTo(endpointsResponseItem.get().getName());
        assertThat(responseGetRequest.getEndpoints().get(0).getPath()).isNotNull()
                .isEqualTo(endpointsResponseItem.get().getPath());
    }

    /**
     * Scenario: Return 404-status code when trying to update microfrontend (mfe) by a non-existing mfe id.
     * Given mfe-ID is not existing
     * When I try to update a mfe by a non-existing mfe id "nonExisting"
     * Then I get a 'Not Found' response code back
     */
    @Test
    void updateMicrofrontend_shouldReturnNotFound_whenMicrofrontendIdDoesNotExist() {
        UpdateMicrofrontendRequestDTO request = createMicrofrontendUpdateRequest("Somechanges", "1.0.0",
                "AppName-5", "some description", "", "https://localhost/mfe/core/ah-mgmtv5/",
                "https://localhost/mfe/core/ah-mgmtv5/remoteEntry.js", "ProductName", null,
                "developers@1000kit.org", "sun", "some notes", "/AnnouncementManagementModulev5", null);

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .pathParam("id", "notExisting")
                .put("{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    /**
     * Scenario: Return 400-status code with exception when trying to update microfrontend (mfe) by an already used app
     * name for same product association.
     * Given mfe-ID is already used by another mfe-A
     * When I try to update mfe-B by used value for app id for same productName association
     * Then I get a 'Bad Request' response code back
     * AND a ProblemDetailResponseObject is returned
     */
    @Test
    void updateMicrofrontend_shouldReturnBadRequest_whenRunningIntoUniquenessViolationConstraints() {
        UpdateMicrofrontendRequestDTO request = createMicrofrontendUpdateRequest("mfeOne", "1.0.0",
                "AppName-5", "some description", "", "https://localhost/mfe/core/ah-mgmtv5/",
                "https://localhost/mfe/core/ah-mgmtv5/remoteEntry.js", "productOne", null,
                "developers@1000kit.org", "sun", "some notes", "/AnnouncementManagementModulev5", null);

        var response = given()
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
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'ps_microfrontend_app_id'  Detail: Key (product_name, app_id)=(productOne, mfeOne) already exists.]");
        assertThat(paramConstraint.get().getValue()).isEqualTo(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'ps_microfrontend_app_id'  Detail: Key (product_name, app_id)=(productOne, mfeOne) already exists.]");
        assertThat(paramConstraint.get().getKey()).isEqualTo("constraint");
        assertThat(paramConstraintName.get().getValue()).isEqualTo("ps_microfrontend_app_id");
        assertThat(paramConstraintName.get().getKey()).isEqualTo("constraintName");
        assertThat(response.getInvalidParams()).isNull();
    }

    /**
     * Scenario: Throw 400 Bad Request when request body is missing.
     * Given
     * When I try to update a mfe without request body
     * Then I get a 'Bad Request' response code back
     * AND problem details are within the response body
     */
    @Test
    void updateMicrofrontend_shouldReturnBadRequest_whenRunningIntoValidationConstraintsEmptyBody() {

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

    /**
     * Helper method to create a microfrontend (mfe) create request object (inbound call).
     *
     * @param appId unique id of application
     * @param appVersion version number
     * @param appName unique name of application
     * @param description general mfe description
     * @param technology representing used technology (angular, react, ...)
     * @param remoteBaseUrl uri for remote base path
     * @param remoteEntry webpack hook
     * @param productName name of associated product
     * @param classifications tags for mfe
     * @param contact contact details (like mail address) for e.g. application support
     * @param iconName identifier of PrimeNG icon lib, z.b. trash, times
     * @param note additional notes
     * @param exposedModule module information
     * @param endpoints endpoints which can be used to address app pages & services
     * @return
     */
    private CreateMicrofrontendRequestDTO createMicrofrontendCreateRequest(
            String appId,
            String appVersion,
            String appName,
            String description,
            String technology,
            String remoteBaseUrl,
            String remoteEntry,
            String productName,
            String classifications,
            String contact,
            String iconName,
            String note,
            String exposedModule,
            List<CreateUIEndpointDTO> endpoints) {

        CreateMicrofrontendRequestDTO mfe = new CreateMicrofrontendRequestDTO();
        mfe.setAppId(appId);
        mfe.setAppVersion(appVersion);
        mfe.setAppName(appName);
        mfe.setDescription(description);
        mfe.setTechnology(technology);
        mfe.setRemoteBaseUrl(remoteBaseUrl);
        mfe.setRemoteEntry(remoteEntry);
        mfe.setProductName(productName);
        mfe.setClassifications(classifications);
        mfe.setContact(contact);
        mfe.setIconName(iconName);
        mfe.setNote(note);
        mfe.setExposedModule(exposedModule);
        mfe.setEndpoints(endpoints);

        return mfe;
    }

    /**
     * Helper method to create a microfrontend (mfe) update request object (inbound call).
     *
     * @param appId unique id of application
     * @param appVersion version number
     * @param appName unique name of application
     * @param description general mfe description
     * @param technology representing used technology (angular, react, ...)
     * @param remoteBaseUrl uri for remote base path
     * @param remoteEntry webpack hook
     * @param productName name of associated product
     * @param classifications tags for mfe
     * @param contact contact details (like mail address) for e.g. application support
     * @param iconName identifier of PrimeNG icon lib, z.b. trash, times
     * @param note additional notes
     * @param exposedModule module information
     * @param endpoints endpoints which can be used to address app pages & services
     * @return
     */
    private UpdateMicrofrontendRequestDTO createMicrofrontendUpdateRequest(
            String appId,
            String appVersion,
            String appName,
            String description,
            String technology,
            String remoteBaseUrl,
            String remoteEntry,
            String productName,
            String classifications,
            String contact,
            String iconName,
            String note,
            String exposedModule,
            List<UpdateUIEndpointDTO> endpoints) {

        UpdateMicrofrontendRequestDTO mfe = new UpdateMicrofrontendRequestDTO();
        mfe.setAppId(appId);
        mfe.setAppVersion(appVersion);
        mfe.setAppName(appName);
        mfe.setDescription(description);
        mfe.setTechnology(technology);
        mfe.setRemoteBaseUrl(remoteBaseUrl);
        mfe.setRemoteEntry(remoteEntry);
        mfe.setProductName(productName);
        mfe.setClassifications(classifications);
        mfe.setContact(contact);
        mfe.setIconName(iconName);
        mfe.setNote(note);
        mfe.setExposedModule(exposedModule);
        mfe.setEndpoints(endpoints);

        return mfe;
    }

}
