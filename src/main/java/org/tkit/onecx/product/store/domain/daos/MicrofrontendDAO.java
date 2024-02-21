package org.tkit.onecx.product.store.domain.daos;

import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.tkit.onecx.product.store.domain.criteria.MicrofrontendSearchCriteria;
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

    public PageResult<String> getAllAppIdsByProductName(String productName) {
        try {
            var cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<String> cq = cb.createQuery(String.class);
            Root<Microfrontend> root = cq.from(Microfrontend.class);
            cq.select(root.get(Microfrontend_.APP_ID)).distinct(true);

            if (productName != null && !productName.isEmpty()) {
                cq.where(cb.equal(root.get(Microfrontend_.PRODUCT_NAME), productName));
            }

            var results = getEntityManager().createQuery(cq).getResultList();
            return new PageResult<>(results.size(), results.stream(), Page.of(0, 1));
        } catch (Exception exception) {
            throw new DAOException(ErrorKeys.ERROR_LOAD_MFE_BY_PRODUCT_NAME, exception);
        }
    }

    public enum ErrorKeys {

        ERROR_FIND_MFE_BY_CRITERIA,
        ERROR_LOAD_MFE_BY_PRODUCT_NAME,
        ERROR_FIND_APP_ID;
    }
}
