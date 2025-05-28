package org.tkit.onecx.product.store.domain.daos;

import java.util.List;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.onecx.product.store.domain.models.ProductClassification;
import org.tkit.onecx.product.store.domain.models.ProductClassification_;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.exceptions.DAOException;

@ApplicationScoped
public class ProductClassificationDAO extends AbstractDAO<ProductClassification> {

    public List<ProductClassification> findByProductIds(List<String> ids) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(ProductClassification.class);
            var root = cq.from(ProductClassification.class);
            cq.where(root.get(ProductClassification_.PRODUCT_ID).in(ids));
            return this.getEntityManager().createQuery(cq).getResultList();
        } catch (Exception exception) {
            throw new DAOException(ProductClassificationDAO.ErrorKeys.ERROR_FIND_BY_PRODUCT_IDS, exception);
        }
    }

    public Stream<String> findAllClassifications() {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(String.class);
            var root = cq.from(ProductClassification.class);
            cq.select(root.get(ProductClassification_.VALUE)).where(cb.isNotNull(root.get(ProductClassification_.VALUE)));
            return this.getEntityManager().createQuery(cq).getResultList().stream().distinct();
        } catch (Exception ex) {
            throw new DAOException(ProductClassificationDAO.ErrorKeys.ERROR_FIND_ALL_CLASSIFICATIONS, ex);
        }
    }

    public enum ErrorKeys {
        ERROR_FIND_ALL_CLASSIFICATIONS,
        ERROR_FIND_BY_PRODUCT_IDS;
    }
}
