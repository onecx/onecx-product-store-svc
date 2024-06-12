package org.tkit.onecx.product.store.domain.daos;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;

import org.tkit.onecx.product.store.domain.criteria.ProductSearchCriteria;
import org.tkit.onecx.product.store.domain.models.*;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.models.AbstractTraceableEntity_;
import org.tkit.quarkus.jpa.utils.QueryCriteriaUtil;

@ApplicationScoped
public class ProductDAO extends AbstractDAO<Product> {

    public PageResult<Product> findProductsByCriteria(ProductSearchCriteria criteria) {
        try {
            var cb = getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Product.class);
            var root = cq.from(Product.class);

            if (criteria.getName() != null && !criteria.getName().isBlank()) {
                cq.where(QueryCriteriaUtil.createSearchStringPredicate(cb, root.get(Product_.NAME),
                        criteria.getName()));
            }

            if (criteria.getProductNames() != null && !criteria.getProductNames().isEmpty()) {
                cq.where(root.get(Product_.NAME).in(criteria.getProductNames()));
            }
            cq.orderBy(cb.desc(root.get(AbstractTraceableEntity_.CREATION_DATE)));
            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_PRODUCTS_BY_CRITERIA, ex);
        }
    }

    public List<Product> findByProductNames(List<String> productNames) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Product.class);
            var root = cq.from(Product.class);
            cq.where(root.get(Product_.NAME).in(productNames));
            return this.getEntityManager().createQuery(cq).getResultList();
        } catch (Exception exception) {
            throw new DAOException(ErrorKeys.ERROR_FIND_PRODUCT_BY_PRODUCT_NAMES, exception);
        }
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

        ERROR_FIND_PRODUCT_BY_PRODUCT_NAMES,

        ERROR_FIND_PRODUCTS_BY_CRITERIA,

        ERROR_FIND_PRODUCT_BY_NAME;
    }
}
