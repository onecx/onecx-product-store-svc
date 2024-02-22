package org.tkit.onecx.product.store.domain.daos;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class MicroserviceDAOTest {
    @Inject
    MicroserviceDAO dao;

    @InjectMock
    EntityManager em;

    @BeforeEach
    void beforeAll() {
        Mockito.when(em.getCriteriaBuilder()).thenThrow(new RuntimeException("Test technical error exception"));
    }

    @Test
    void methodExceptionTests() {
        methodExceptionTests(() -> dao.loadByProductName(null),
                MicroserviceDAO.ErrorKeys.ERROR_LOAD_MS_BY_PRODUCT_NAME);
        methodExceptionTests(() -> dao.findMicroservicesByCriteria(null),
                MicroserviceDAO.ErrorKeys.ERROR_FIND_MS_BY_CRITERIA);
        methodExceptionTests(() -> dao.findByAppId("test"),
                MicroserviceDAO.ErrorKeys.ERROR_FIND_APP_ID);
        methodExceptionTests(() -> dao.loadByCriteria(null),
                MicroserviceDAO.ErrorKeys.ERROR_FIND_MS_BY_CRITERIA);
    }

    void methodExceptionTests(Executable fn, Enum<?> key) {
        var exc = Assertions.assertThrows(DAOException.class, fn);
        Assertions.assertEquals(key, exc.key);
    }
}
