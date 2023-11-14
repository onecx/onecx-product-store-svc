package io.github.onecx.product.store.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import io.github.onecx.product.store.domain.criteria.ProductSearchCriteria;
import io.github.onecx.product.store.domain.models.Product;
import io.github.onecx.product.store.domain.models.Product_;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ProductDAO extends AbstractDAO<Product> {

    public PageResult<Product> findProductsByCriteria(ProductSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Product.class);
            var root = cq.from(Product.class);

            if (criteria.getName() != null && !criteria.getName().isBlank()) {
                cq.where(cb.like(root.get(Product_.name), likeValue(criteria.getName())));
            }

            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_PRODUCTS_BY_CRITERIA, ex);
        }
    }

    private static String likeValue(String value) {
        return (value.toLowerCase() + "%");
    }

    public Product findProductByName(String productName) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Product.class);
            var root = cq.from(Product.class);
            cq.where(cb.equal(root.get(Product_.name), productName));
            return this.getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_PRODUCT_BY_NAME, ex, productName);
        }
    }

    public enum ErrorKeys {

        ERROR_FIND_PRODUCTS_BY_CRITERIA,

        ERROR_FIND_PRODUCT_BY_NAME;
    }
}
