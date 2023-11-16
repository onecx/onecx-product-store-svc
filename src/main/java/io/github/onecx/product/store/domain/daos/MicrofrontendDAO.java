package io.github.onecx.product.store.domain.daos;

import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.utils.QueryCriteriaUtil;

import io.github.onecx.product.store.domain.criteria.MicrofrontendSearchCriteria;
import io.github.onecx.product.store.domain.models.Microfrontend;
import io.github.onecx.product.store.domain.models.Microfrontend_;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class MicrofrontendDAO extends AbstractDAO<Microfrontend> {

    public PageResult<Microfrontend> findMicrofrontendsByCriteria(MicrofrontendSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Microfrontend.class);
            var root = cq.from(Microfrontend.class);

            if (criteria.getProductName() != null && !criteria.getProductName().isBlank()) {
                cq.where(cb.like(root.get(Microfrontend_.PRODUCT_NAME), QueryCriteriaUtil.wildcard(criteria.getProductName())));
            }

            if (criteria.getDisplayName() != null && !criteria.getDisplayName().isBlank()) {
                cq.where(cb.like(root.get(Microfrontend_.DISPLAY_NAME), QueryCriteriaUtil.wildcard(criteria.getDisplayName())));
            }

            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_MFE_BY_CRITERIA, ex);
        }
    }

    public Microfrontend findByMfeId(String mfeId) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Microfrontend.class);
            var root = cq.from(Microfrontend.class);
            cq.where(cb.equal(root.get(Microfrontend_.MFE_ID), mfeId));
            return this.getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_MFE_BY_ID, ex, mfeId);
        }
    }

    public Stream<Microfrontend> findByProductName(String productName) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Microfrontend.class);
            var root = cq.from(Microfrontend.class);
            cq.where(cb.equal(root.get(Microfrontend_.PRODUCT_NAME), productName));
            return this.getEntityManager().createQuery(cq).getResultStream();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_MFES_BY_PRODUCT_NAME, ex, productName);
        }
    }

    public enum ErrorKeys {

        ERROR_FIND_MFE_BY_CRITERIA,
        ERROR_FIND_MFES_BY_PRODUCT_NAME,
        ERROR_FIND_MFE_BY_ID;
    }
}
