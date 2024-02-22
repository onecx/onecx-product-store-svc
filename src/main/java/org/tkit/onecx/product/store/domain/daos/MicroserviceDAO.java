package org.tkit.onecx.product.store.domain.daos;

import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;

import org.tkit.onecx.product.store.domain.criteria.MicroserviceSearchCriteria;
import org.tkit.onecx.product.store.domain.criteria.ProductLoadCriteria;
import org.tkit.onecx.product.store.domain.models.Microservice;
import org.tkit.onecx.product.store.domain.models.Microservice_;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.utils.QueryCriteriaUtil;

@ApplicationScoped
public class MicroserviceDAO extends AbstractDAO<Microservice> {
    public PageResult<Microservice> findMicroservicesByCriteria(MicroserviceSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Microservice.class);
            var root = cq.from(Microservice.class);

            if (criteria.getProductName() != null && !criteria.getProductName().isBlank()) {
                cq.where(QueryCriteriaUtil.createSearchStringPredicate(cb, root.get(Microservice_.PRODUCT_NAME),
                        criteria.getProductName()));
            }

            if (criteria.getAppId() != null && !criteria.getAppId().isBlank()) {
                cq.where(QueryCriteriaUtil.createSearchStringPredicate(cb, root.get(Microservice_.APP_ID),
                        criteria.getAppId()));
            }

            if (criteria.getName() != null && !criteria.getName().isBlank()) {
                cq.where(QueryCriteriaUtil.createSearchStringPredicate(cb, root.get(Microservice_.NAME),
                        criteria.getName()));
            }

            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_MS_BY_CRITERIA, ex);
        }
    }

    public Microservice findByAppId(String appId) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Microservice.class);
            var root = cq.from(Microservice.class);
            cq.where(cb.equal(root.get(Microservice_.APP_ID), appId));
            return this.getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_APP_ID, ex, appId);
        }
    }

    public Stream<Microservice> loadByProductName(String productName) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Microservice.class);
            var root = cq.from(Microservice.class);
            cq.where(cb.equal(root.get(Microservice_.PRODUCT_NAME), productName));
            return this.getEntityManager()
                    .createQuery(cq)
                    .getResultStream();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_LOAD_MS_BY_PRODUCT_NAME, ex, productName);
        }
    }

    public PageResult<Microservice> loadByCriteria(ProductLoadCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Microservice.class);
            var root = cq.from(Microservice.class);

            if (criteria.getProductNames() != null && !criteria.getProductNames().isEmpty()) {
                cq.where(root.get(Microservice_.PRODUCT_NAME).in(criteria.getProductNames()));
            }
            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception exception) {
            throw new DAOException(MicroserviceDAO.ErrorKeys.ERROR_FIND_MS_BY_CRITERIA, exception);
        }
    }

    public enum ErrorKeys {

        ERROR_FIND_MS_BY_CRITERIA,
        ERROR_LOAD_MS_BY_PRODUCT_NAME,
        ERROR_FIND_APP_ID;
    }
}
