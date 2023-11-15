package io.github.onecx.product.store.rs.internal.controllers;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import io.github.onecx.product.store.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(MicrofrontendsInternalRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class MicrofrontendsInternalRestControllerTest extends AbstractTest {

    @Test
    void createMicrofrontendTest() {

    }

    @Test
    void deleteMicrofrontendTest() {
    }

    @Test
    void getMicrofrontendTest() {
    }

    @Test
    void searchMicrofrontendsTest() {
        ;
    }

    @Test
    void updateMicrofrontendTest() {

    }
}
