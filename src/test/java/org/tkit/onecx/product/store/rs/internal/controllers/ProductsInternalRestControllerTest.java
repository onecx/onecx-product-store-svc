package org.tkit.onecx.product.store.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.tkit.quarkus.security.test.SecurityTestUtils.getKeycloakClientToken;

import java.io.File;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.product.store.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.image.rs.internal.model.ImageInfoDTO;
import gen.org.tkit.onecx.image.rs.internal.model.RefTypeDTO;
import gen.org.tkit.onecx.product.store.rs.internal.model.*;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
//@TestHTTPEndpoint(ProductsInternalRestController.class)
@WithDBData(value = "data/test-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-ps:read", "ocx-ps:write", "ocx-ps:delete", "ocx-ps:all" })
class ProductsInternalRestControllerTest extends AbstractTest {

    private static final String MEDIA_TYPE_IMAGE_PNG = "image/png";

    private static final File FILE = new File(
            Objects.requireNonNull(ProductsInternalRestControllerTest.class.getResource("/images/Testimage.png")).getFile());

    @Test
    void createProductTest() {

        // create product
        var createProductDTO = new CreateProductRequestDTO();
        createProductDTO.setName("test01");
        createProductDTO.setVersion("test01");
        createProductDTO.setBasePath("basePath");

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(createProductDTO)
                .post("/internal/products")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ProductDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isNotNull().isEqualTo(createProductDTO.getName());
        assertThat(dto.getBasePath()).isNotNull().isEqualTo(createProductDTO.getBasePath());

        // create product without body
        var exception = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .post("/internal/products")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getDetail()).isEqualTo("createProduct.createProductRequestDTO: must not be null");

        // create product with existing name
        exception = given()
                .auth().oauth2(getKeycloakClientToken("testClient")).when()
                .contentType(APPLICATION_JSON)
                .body(createProductDTO)
                .post("/internal/products")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("PERSIST_ENTITY_FAILED");
        assertThat(exception.getDetail()).isEqualTo(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'ui_product_base_path'  Detail: Key (base_path)=(basePath) already exists.]");
    }

    @Test
    void deleteProductTest() {
        // delete product
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "p1")
                .delete("/internal/products/{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        // check if product exists
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "p1")
                .get("/internal/products/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        // check if related MFEs got deleted too
        MicrofrontendSearchCriteriaDTO criteriaDTO = new MicrofrontendSearchCriteriaDTO();
        criteriaDTO.setProductName("product1");
        criteriaDTO.setPageNumber(0);
        criteriaDTO.setPageSize(1);

        var mfes = given()
                .auth().oauth2(getKeycloakClientToken("testClient")).contentType(APPLICATION_JSON)
                .body(criteriaDTO)
                .post("/internal/microfrontends/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicrofrontendPageResultDTO.class);
        assertThat(mfes.getStream()).isEmpty();

        MicroserviceSearchCriteriaDTO msSearchCriteria = new MicroserviceSearchCriteriaDTO();
        msSearchCriteria.setProductName("product1");
        criteriaDTO.setPageNumber(0);
        criteriaDTO.setPageSize(1);

        var ms = given()
                .auth().oauth2(getKeycloakClientToken("testClient")).contentType(APPLICATION_JSON)
                .body(msSearchCriteria)
                .post("/internal/microservices/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicroservicePageResultDTO.class);
        assertThat(ms.getStream()).isEmpty();

        // delete product
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "p1")
                .delete("/internal/products/{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void deleteProductTest_shouldDeleteImages() {
        var refId = "productDeleteTest";
        var refType = RefTypeDTO.LOGO;

        // Create Product
        var createProductDTO = new CreateProductRequestDTO();
        createProductDTO.setName(refId);
        createProductDTO.setVersion("test01");
        createProductDTO.setBasePath("basePath");

        var output = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(createProductDTO)
                .post("/internal/products")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ProductDTO.class);

        // Create Image
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post("/internal/images/{refId}/{refType}")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);

        // delete product
        var res = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", output.getId())
                .delete("/internal/products/{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        Assertions.assertNotNull(res);

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .get("/internal/images/{refId}/{refType}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void getProductTest() {
        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "p1")
                .get("/internal/products/{id}")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(ProductDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("product1");
        assertThat(dto.getDescription()).isEqualTo("description");

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "___")
                .get("/internal/products/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void searchProducts_shouldReturnProductListFullAttributeCheck_whenSearchCriteriaMatches() {
        var criteria = new ProductSearchCriteriaDTO();
        criteria.setName("product1");
        criteria.setPageNumber(0);
        criteria.setPageSize(100);

        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/internal/products/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(1);
        assertThat(data.getStream()).isNotNull().hasSize(1);

        var criteria2 = new ProductSearchCriteriaDTO();
        criteria2.setName("product1");
        criteria2.setPageNumber(0);
        criteria2.setPageSize(1);

        var data2 = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteria2)
                .post("/internal/products/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductPageResultDTO.class);

        assertThat(data2).isNotNull();
        assertThat(data2.getTotalElements()).isEqualTo(1);
        assertThat(data2.getStream()).isNotNull().hasSize(1);
        assertThat(data2.getStream().get(0).getClassifications()).isNotBlank().isEqualTo("searching");

    }

    @Test
    void searchProductsTest() {
        var criteria = new ProductSearchCriteriaDTO();
        criteria.setDisplayName("");
        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/internal/products/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(3);
        assertThat(data.getStream()).isNotNull().hasSize(3);
        assertThat(data.getStream().get(0).getApplications().get(0).getAppName()).isNotNull();

        criteria.setDisplayName("p1");
        data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/internal/products/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(1);
    }

    @Test
    void searchProductsNoBodyTest() {
        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .post("/internal/products/search")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProblemDetailResponseDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getDetail()).isEqualTo("searchProducts.productSearchCriteriaDTO: must not be null");
    }

    @Test
    void searchProductsNoProductNameCriteriaTest() {
        ProductSearchCriteriaDTO criteriaDTO = new ProductSearchCriteriaDTO();
        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteriaDTO)
                .post("/internal/products/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getStream()).hasSize(3);

        ProductSearchCriteriaDTO criteriaDTO2 = new ProductSearchCriteriaDTO();
        criteriaDTO2.setName("");
        var data2 = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteriaDTO2)
                .post("/internal/products/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductPageResultDTO.class);

        assertThat(data2).isNotNull();
        assertThat(data2.getStream()).hasSize(3);
    }

    @Test
    void updateProductTest() {
        var updateDto = new UpdateProductRequestDTO();
        updateDto.setName("test01");
        updateDto.setVersion("0.0.0");
        updateDto.setDescription("description-update");
        updateDto.setBasePath("basePath");

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(updateDto)
                .when()
                .pathParam("id", "does-not-exists")
                .put("/internal/products/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        var updateResult = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(updateDto)
                .when()
                .pathParam("id", "p1")
                .put("/internal/products/{id}")
                .then().statusCode(OK.getStatusCode()).extract().as(ProductDTO.class);

        assertThat(updateResult).isNotNull();
        assertThat(updateResult.getDescription()).isEqualTo(updateDto.getDescription());

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient")).contentType(APPLICATION_JSON)
                .body(updateDto)
                .when()
                .pathParam("id", "p1")
                .get("/internal/products/{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(ProductDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getDescription()).isEqualTo(updateDto.getDescription());

        //check if mfes updated
        MicrofrontendSearchCriteriaDTO criteriaDTO = new MicrofrontendSearchCriteriaDTO();
        criteriaDTO.setProductName(updateDto.getName());
        criteriaDTO.setPageSize(1);
        criteriaDTO.setPageNumber(0);

        var mfes = given()
                .auth().oauth2(getKeycloakClientToken("testClient")).contentType(APPLICATION_JSON)
                .body(criteriaDTO)
                .post("/internal/microfrontends/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicrofrontendPageResultDTO.class);
        assertThat(mfes.getStream()).hasSize(1);

        //check if ms updated
        MicrofrontendSearchCriteriaDTO msCriteriaDTO = new MicrofrontendSearchCriteriaDTO();
        msCriteriaDTO.setProductName(updateDto.getName());
        msCriteriaDTO.setPageSize(1);
        msCriteriaDTO.setPageNumber(0);

        var ms = given()
                .auth().oauth2(getKeycloakClientToken("testClient")).contentType(APPLICATION_JSON)
                .body(msCriteriaDTO)
                .post("/internal/microservices/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicroservicePageResultDTO.class);
        assertThat(ms.getStream()).hasSize(1);
    }

    @Test
    void updateProductWithoutBodyTest() {

        var exception = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "update_create_new")
                .put("/internal/products/{id}")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("CONSTRAINT_VIOLATIONS", exception.getErrorCode());
        Assertions.assertEquals("updateProduct.updateProductRequestDTO: must not be null",
                exception.getDetail());
        Assertions.assertNotNull(exception.getInvalidParams());
        Assertions.assertEquals(1, exception.getInvalidParams().size());
    }

    @Test
    void getProductByNameTest() {
        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("name", "product1")
                .get("/internal/products/name/{name}")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(ProductDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("product1");
        assertThat(dto.getDescription()).isEqualTo("description");

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("name", "___")
                .get("/internal/products/name/{name}")
                .then().statusCode(NOT_FOUND.getStatusCode());
    }
}
