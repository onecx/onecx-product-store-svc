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
class MicrofrontendDAOTest {
    @Inject
    MicrofrontendDAO dao;

    @InjectMock
    EntityManager em;

    @BeforeEach
    void beforeAll() {
        Mockito.when(em.getCriteriaBuilder()).thenThrow(new RuntimeException("Test technical error exception"));
    }

    @Test
    void methodExceptionTests() {
        methodExceptionTests(() -> dao.loadByProductName(null),
                MicrofrontendDAO.ErrorKeys.ERROR_LOAD_MFE_BY_PRODUCT_NAME);
        methodExceptionTests(() -> dao.findMicrofrontendsByCriteria(null),
                MicrofrontendDAO.ErrorKeys.ERROR_FIND_MFE_BY_CRITERIA);
        methodExceptionTests(() -> dao.findByProductAppExposedModule(null, null, null),
                MicrofrontendDAO.ErrorKeys.ERROR_FIND_PRODUCT_APP_MODULE);
        methodExceptionTests(() -> dao.loadByCriteria(null),
                MicrofrontendDAO.ErrorKeys.ERROR_FIND_MFE_BY_CRITERIA);
    }

    void methodExceptionTests(Executable fn, Enum<?> key) {
        var exc = Assertions.assertThrows(DAOException.class, fn);
        Assertions.assertEquals(key, exc.key);
    }
}
