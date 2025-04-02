package org.tkit.onecx.product.store.domain.daos;

import java.util.List;

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

    public enum ErrorKeys {
        ERROR_FIND_BY_PRODUCT_IDS;
    }
}
