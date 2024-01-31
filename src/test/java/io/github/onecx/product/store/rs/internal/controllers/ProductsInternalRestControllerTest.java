package io.github.onecx.product.store.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.product.store.rs.internal.model.*;
import io.github.onecx.product.store.AbstractTest;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
//@TestHTTPEndpoint(ProductsInternalRestController.class)
@WithDBData(value = "data/test-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class ProductsInternalRestControllerTest extends AbstractTest {

    @Test
    void createProductTest() {

        // create product
        var createProductDTO = new CreateProductRequestDTO();
        createProductDTO.setName("test01");
        createProductDTO.setVersion("test01");
        createProductDTO.setBasePath("basePath");

        var dto = given()
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
                .when()
                .contentType(APPLICATION_JSON)
                .post("/internal/products")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getDetail()).isEqualTo("createProduct.createProductRequestDTO: must not be null");

        // create theme with existing name
        exception = given().when()
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
                .contentType(APPLICATION_JSON)
                .pathParam("id", "p1")
                .delete("/internal/products/{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        // check if product exists
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "p1")
                .get("/internal/products/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        // check if related MFEs got deleted too
        MicrofrontendSearchCriteriaDTO criteriaDTO = new MicrofrontendSearchCriteriaDTO();
        criteriaDTO.setProductName("product1");
        criteriaDTO.setPageNumber(1);
        criteriaDTO.setPageSize(1);

        var mfes = given().contentType(APPLICATION_JSON)
                .body(criteriaDTO)
                .post("/internal/microfrontends/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(MicrofrontendPageResultDTO.class);
        assertThat(mfes.getStream()).isEmpty();

        // delete product
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "p1")
                .delete("/internal/products/{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void getProductTest() {
        var dto = given()
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

        var data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/internal/products/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(2);
        assertThat(data.getStream()).isNotNull().hasSize(2);
    }

    @Test
    void searchProductsNoBodyTest() {
        var data = given()
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
                .contentType(APPLICATION_JSON)
                .body(criteriaDTO)
                .post("/internal/products/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getStream()).hasSize(2);

        ProductSearchCriteriaDTO criteriaDTO2 = new ProductSearchCriteriaDTO();
        criteriaDTO2.setName("");
        var data2 = given()
                .contentType(APPLICATION_JSON)
                .body(criteriaDTO2)
                .post("/internal/products/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductPageResultDTO.class);

        assertThat(data2).isNotNull();
        assertThat(data2.getStream()).hasSize(2);
    }

    @Test
    void updateProductTest() {
        var updateDto = new UpdateProductRequestDTO();
        updateDto.setName("test01");
        updateDto.setVersion("0.0.0");
        updateDto.setDescription("description-update");
        updateDto.setBasePath("basePath");

        given()
                .contentType(APPLICATION_JSON)
                .body(updateDto)
                .when()
                .pathParam("id", "does-not-exists")
                .put("/internal/products/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        given()
                .contentType(APPLICATION_JSON)
                .body(updateDto)
                .when()
                .pathParam("id", "p1")
                .put("/internal/products/{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        var dto = given().contentType(APPLICATION_JSON)
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
    }

    @Test
    void updateProductWithoutBodyTest() {

        var exception = given()
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
                .contentType(APPLICATION_JSON)
                .pathParam("name", "___")
                .get("/internal/products/name/{name}")
                .then().statusCode(NOT_FOUND.getStatusCode());
    }
}
