package org.tkit.onecx.product.store.domain.daos;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaRoot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.tkit.onecx.product.store.domain.criteria.SlotSearchCriteria;
import org.tkit.onecx.product.store.domain.models.Slot;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class SlotDAOTest {
    @Inject
    SlotDAO dao;

    @InjectMock
    EntityManager em;

    @BeforeEach
    void beforeAll() {
        Mockito.when(em.getCriteriaBuilder()).thenThrow(new RuntimeException("Test technical error exception"));
    }

    @Test
    void methodExceptionTests() {
        methodExceptionTests(() -> dao.findByProductNameAppId("test", "test", "test"),
                SlotDAO.ErrorKeys.ERROR_FIND_SLOT_PRODUCT_NAME_APP_ID_NAME);
        methodExceptionTests(() -> dao.findByCriteria(null),
                SlotDAO.ErrorKeys.ERROR_FIND_SLOTS_BY_CRITERIA);
        methodExceptionTests(() -> dao.loadByCriteria(null),
                SlotDAO.ErrorKeys.ERROR_FIND_SLOTS_BY_CRITERIA);
        methodExceptionTests(() -> dao.deleteByProductName(null),
                SlotDAO.ErrorKeys.ERROR_DELETE_BY_PRODUCT_NAME);
        methodExceptionTests(() -> dao.updateByProductName(null, null),
                SlotDAO.ErrorKeys.ERROR_UPDATE_BY_PRODUCT_NAME);
    }

    @Test
    void shouldSkipProductDisplayNameSubqueryWhenProductDisplayNameIsEmpty() {
        var criteriaBuilder = Mockito.mock(HibernateCriteriaBuilder.class, Mockito.RETURNS_DEEP_STUBS);
        var criteriaQuery = Mockito.mock(JpaCriteriaQuery.class, Mockito.RETURNS_DEEP_STUBS);
        var slotRoot = Mockito.mock(JpaRoot.class, Mockito.RETURNS_DEEP_STUBS);

        Mockito.doReturn(criteriaBuilder).when(em).getCriteriaBuilder();
        Mockito.when(criteriaBuilder.createQuery(Slot.class)).thenReturn(criteriaQuery);
        Mockito.when(criteriaQuery.from(Slot.class)).thenReturn(slotRoot);

        var criteria = new SlotSearchCriteria();
        criteria.setProductDisplayName("");
        criteria.setPageNumber(0);
        criteria.setPageSize(10);

        Assertions.assertThrows(DAOException.class, () -> dao.findByCriteria(criteria));

        Mockito.verify(criteriaQuery, Mockito.never()).subquery(String.class);
    }

    void methodExceptionTests(Executable fn, Enum<?> key) {
        var exc = Assertions.assertThrows(DAOException.class, fn);
        Assertions.assertEquals(key, exc.key);
    }
}
