package io.github.onecx.product.store.domain.daos;

import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;

import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.utils.QueryCriteriaUtil;

import io.github.onecx.product.store.domain.criteria.ProductSearchCriteria;
import io.github.onecx.product.store.domain.models.Microfrontend;
import io.github.onecx.product.store.domain.models.Microfrontend_;
import io.github.onecx.product.store.domain.models.Product;
import io.github.onecx.product.store.domain.models.Product_;

@ApplicationScoped
public class ProductDAO extends AbstractDAO<Product> {

    //@EntityGraph(value = "Product.PRODUCT_LOAD")
    public PageResult<Product> findProductsByCriteria(ProductSearchCriteria criteria) {
        try {
            var cb = getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Product.class);
            var root = cq.from(Product.class);

            if (criteria.getName() != null && !criteria.getName().isBlank()) {
                cq.where(cb.like(root.get(Product_.NAME), QueryCriteriaUtil.wildcard(criteria.getName())));
            }
            //  return this.getEntityManager()
            //          .createQuery(cq)
            //           .setHint(HINT_LOAD_GRAPH, this.getEntityManager().getEntityGraph(Product.PRODUCT_LOAD)).getResultStream();

            return new PagedQuery(this.getEntityManager(), cq, Page.of(criteria.getPageNumber(), criteria.getPageSize()), "id",
                    this.getEntityManager().getEntityGraph(Product.PRODUCT_LOAD))
                    .getPageResult();

            /*
             * this.getEntityManager().createQuery(cq)
             * .setHint(HINT_LOAD_GRAPH, this.getEntityManager().getEntityGraph(Product.PRODUCT_LOAD)).getResultStream()
             * .getPageResult();
             */

            //return createPageQuery( cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_PRODUCTS_BY_CRITERIA, ex);
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

        ERROR_FIND_PRODUCTS_BY_CRITERIA,

        ERROR_FIND_PRODUCT_BY_NAME;
    }
}
