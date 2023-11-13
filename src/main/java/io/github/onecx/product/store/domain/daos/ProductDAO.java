package io.github.onecx.product.store.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import io.github.onecx.product.store.domain.models.Product;
import io.github.onecx.product.store.domain.models.Product_;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ProductDAO extends AbstractDAO<Product> {

    public Product findProductByName(String productName) {
        if (productName == null) {
            return null;
        }
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Product.class);
            var root = cq.from(Product.class);
            cq.where(cb.equal(root.get(Product_.name), productName));
            return this.getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception ex) {
            throw new DAOException(ErrorKey.ERROR_FIND_PRODUCT_BY_NAME, ex, productName);
        }
    }

    public enum ErrorKey {

        ERROR_FIND_PRODUCT_BY_NAME;
    }
}
