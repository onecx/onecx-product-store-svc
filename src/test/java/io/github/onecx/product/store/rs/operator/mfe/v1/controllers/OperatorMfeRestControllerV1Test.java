package io.github.onecx.product.store.rs.operator.mfe.v1.controllers;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import io.github.onecx.product.store.AbstractTest;
import io.github.onecx.product.store.rs.operator.product.v1.controllers.OperatorProductRestControllerV1;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OperatorProductRestControllerV1.class)
@WithDBData(value = "data/testdata-operator-mfe.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class OperatorMfeRestControllerV1Test extends AbstractTest {

    @Test
    void createOrUpdateMfeTest() {

    }
}
