package org.tkit.onecx.product.store.domain.daos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.Predicate;

import org.tkit.onecx.product.store.domain.criteria.MicroserviceSearchCriteria;
import org.tkit.onecx.product.store.domain.criteria.ProductSearchCriteria;
import org.tkit.onecx.product.store.domain.models.Microservice;
import org.tkit.onecx.product.store.domain.models.Microservice_;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.models.AbstractTraceableEntity_;
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

    public PageResult<Microservice> loadByCriteria(ProductSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Microservice.class);
            var root = cq.from(Microservice.class);

            if (!criteria.getProductNames().isEmpty()) {
                cq.where(root.get(Microservice_.PRODUCT_NAME).in(criteria.getProductNames()));
            }
            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception exception) {
            throw new DAOException(MicroserviceDAO.ErrorKeys.ERROR_FIND_MS_BY_CRITERIA, exception);
        }
    }

    public void updateByProductName(String productName, String updatedProductName) {
        var cb = getEntityManager().getCriteriaBuilder();
        var uq = this.updateQuery();
        var root = uq.from(Microservice.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(Microservice_.PRODUCT_NAME), productName));

        uq.set(Microservice_.PRODUCT_NAME, updatedProductName)
                .set(AbstractTraceableEntity_.modificationCount,
                        cb.sum(root.get(AbstractTraceableEntity_.modificationCount), 1))
                .set(AbstractTraceableEntity_.modificationDate, cb.currentTimestamp().as(LocalDateTime.class))
                .where(cb.and(predicates.toArray(new Predicate[0])));

        this.getEntityManager().createQuery(uq).executeUpdate();
    }

    public void deleteByProductName(String productName) {
        var cb = getEntityManager().getCriteriaBuilder();
        var dq = this.deleteQuery();
        var root = dq.from(Microservice.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(Microservice_.PRODUCT_NAME), productName));

        dq.where(cb.and(predicates.toArray(new Predicate[0])));

        this.getEntityManager().createQuery(dq).executeUpdate();
    }

    public enum ErrorKeys {

        ERROR_FIND_MS_BY_CRITERIA,
        ERROR_LOAD_MS_BY_PRODUCT_NAME,
        ERROR_FIND_APP_ID;
    }
}
