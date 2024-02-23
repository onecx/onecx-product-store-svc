package org.tkit.onecx.product.store.domain.daos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.Predicate;

import org.tkit.onecx.product.store.domain.criteria.MicrofrontendSearchCriteria;
import org.tkit.onecx.product.store.domain.criteria.ProductSearchCriteria;
import org.tkit.onecx.product.store.domain.models.Microfrontend;
import org.tkit.onecx.product.store.domain.models.Microfrontend_;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.utils.QueryCriteriaUtil;

@ApplicationScoped
public class MicrofrontendDAO extends AbstractDAO<Microfrontend> {

    public PageResult<Microfrontend> findMicrofrontendsByCriteria(MicrofrontendSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Microfrontend.class);
            var root = cq.from(Microfrontend.class);

            if (criteria.getProductName() != null && !criteria.getProductName().isBlank()) {
                cq.where(QueryCriteriaUtil.createSearchStringPredicate(cb, root.get(Microfrontend_.PRODUCT_NAME),
                        criteria.getProductName()));
            }

            if (criteria.getAppName() != null && !criteria.getAppName().isBlank()) {
                cq.where(QueryCriteriaUtil.createSearchStringPredicate(cb, root.get(Microfrontend_.APP_NAME),
                        criteria.getAppName()));
            }

            if (criteria.getAppId() != null && !criteria.getAppId().isBlank()) {
                cq.where(QueryCriteriaUtil.createSearchStringPredicate(cb, root.get(Microfrontend_.APP_ID),
                        criteria.getAppId()));
            }

            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_MFE_BY_CRITERIA, ex);
        }
    }

    public Microfrontend findByAppId(String appId) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Microfrontend.class);
            var root = cq.from(Microfrontend.class);
            cq.where(cb.equal(root.get(Microfrontend_.APP_ID), appId));
            return this.getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_APP_ID, ex, appId);
        }
    }

    public Stream<Microfrontend> loadByProductName(String productName) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Microfrontend.class);
            var root = cq.from(Microfrontend.class);
            cq.where(cb.equal(root.get(Microfrontend_.PRODUCT_NAME), productName));
            return this.getEntityManager()
                    .createQuery(cq)
                    .setHint(HINT_LOAD_GRAPH, this.getEntityManager().getEntityGraph(Microfrontend.MICROFRONTEND_LOAD))
                    .getResultStream();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_LOAD_MFE_BY_PRODUCT_NAME, ex, productName);
        }
    }

    public PageResult<Microfrontend> loadByCriteria(ProductSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Microfrontend.class);
            var root = cq.from(Microfrontend.class);

            if (criteria.getProductNames() != null && !criteria.getProductNames().isEmpty()) {
                cq.where(root.get(Microfrontend_.PRODUCT_NAME).in(criteria.getProductNames()));
            }
            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception exception) {
            throw new DAOException(MicrofrontendDAO.ErrorKeys.ERROR_FIND_MFE_BY_CRITERIA, exception);
        }
    }

    public void updateByProductName(String productName, String updatedProductName) {
        var cb = getEntityManager().getCriteriaBuilder();
        var uq = this.updateQuery();
        var root = uq.from(Microfrontend.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(Microfrontend_.PRODUCT_NAME), productName));

        uq.set(Microfrontend_.PRODUCT_NAME, updatedProductName)
                .set(Microfrontend_.MODIFICATION_COUNT, cb.sum(root.get(Microfrontend_.MODIFICATION_COUNT), 1))
                .set(Microfrontend_.MODIFICATION_DATE, cb.currentTimestamp())
                .where(cb.and(predicates.toArray(new Predicate[0])));

        this.getEntityManager().createQuery(uq).executeUpdate();
    }

    public enum ErrorKeys {

        ERROR_FIND_MFE_BY_CRITERIA,
        ERROR_LOAD_MFE_BY_PRODUCT_NAME,
        ERROR_FIND_APP_ID;
    }
}
