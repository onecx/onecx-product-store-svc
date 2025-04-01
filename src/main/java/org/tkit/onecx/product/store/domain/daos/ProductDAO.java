package org.tkit.onecx.product.store.domain.daos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.Predicate;

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

            List<Predicate> predicates = new ArrayList<>();

            QueryCriteriaUtil.addSearchStringPredicate(predicates, cb, root.get(Product_.NAME),
                    criteria.getName());

            QueryCriteriaUtil.addSearchStringPredicate(predicates, cb, root.get(Product_.DISPLAY_NAME),
                    criteria.getDisplayName());

            if (criteria.getProductNames() != null && !criteria.getProductNames().isEmpty()) {
                predicates.add(root.get(Product_.name).in(criteria.getProductNames()));
            }
            if (criteria.getProviders() != null && !criteria.getProviders().isEmpty()) {
                predicates.add(root.get(Product_.provider).in(criteria.getProviders()));
            }
            if (criteria.getClassifications() != null && !criteria.getClassifications().isEmpty()) {
                predicates.add(root.get(Product_.CLASSIFICATIONS).get("value").in(criteria.getClassifications()));
            }

            if (!predicates.isEmpty()) {
                cq.where(cb.and(predicates.toArray(new Predicate[0])));
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

    public Stream<String> findAllProviders() {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(String.class);
            var root = cq.from(Product.class);
            cq.select(root.get(Product_.PROVIDER));
            return this.getEntityManager().createQuery(cq).getResultList().stream().distinct();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_ALL_PROVIDERS, ex);
        }
    }

    public Stream<String> findAllClassifications() {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(String.class);
            var root = cq.from(ProductClassification.class);
            cq.select(root.get("value"));
            return this.getEntityManager().createQuery(cq).getResultList().stream().distinct();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_ALL_CLASSIFICATIONS, ex);
        }
    }

    public enum ErrorKeys {

        ERROR_FIND_PRODUCT_BY_PRODUCT_NAMES,

        ERROR_FIND_PRODUCTS_BY_CRITERIA,

        ERROR_FIND_ALL_PROVIDERS,

        ERROR_FIND_ALL_CLASSIFICATIONS,

        ERROR_FIND_PRODUCT_BY_NAME;
    }
}
