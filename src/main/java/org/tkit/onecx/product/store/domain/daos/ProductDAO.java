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
            addProductNamesPredicates(criteria, cb, root, predicates);
            addNamesPredicates(criteria, cb, root, predicates);
            addProvidersPredicate(criteria, root, predicates);
            addClassificationsPredicate(criteria, root, predicates);

            if (!predicates.isEmpty()) {
                cq.where(cb.and(predicates.toArray(new Predicate[0])));
            }

            cq.orderBy(cb.desc(root.get(AbstractTraceableEntity_.CREATION_DATE)));
            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_PRODUCTS_BY_CRITERIA, ex);
        }
    }

    private void addProductNamesPredicates(ProductSearchCriteria criteria, jakarta.persistence.criteria.CriteriaBuilder cb,
            jakarta.persistence.criteria.Root<Product> root, List<Predicate> predicates) {
        if (criteria.getProductNames() != null && !criteria.getProductNames().isEmpty()) {
            if (criteria.getProductNames().size() == 1) {
                predicates.add(cb.equal(root.get(Product_.NAME), criteria.getProductNames().get(0)));
            } else {
                predicates.add(root.get(Product_.NAME).in(criteria.getProductNames()));
            }
        }
    }

    private void addNamesPredicates(ProductSearchCriteria criteria, jakarta.persistence.criteria.CriteriaBuilder cb,
            jakarta.persistence.criteria.Root<Product> root, List<Predicate> predicates) {
        if (criteria.getNames() == null || criteria.getNames().isEmpty()) {
            return;
        }

        if (criteria.getNames().size() == 1) {
            var predicate1 = QueryCriteriaUtil.createSearchStringPredicate(cb, root.get(Product_.NAME),
                    criteria.getNames().get(0));
            var predicate2 = QueryCriteriaUtil.createSearchStringPredicate(cb, root.get(Product_.DISPLAY_NAME),
                    criteria.getNames().get(0));
            predicates.add(cb.or(predicate1, predicate2));
            return;
        }

        predicates.add(cb.or(root.get(Product_.NAME).in(criteria.getNames()),
                root.get(Product_.DISPLAY_NAME).in(criteria.getNames())));
    }

    private void addProvidersPredicate(ProductSearchCriteria criteria, jakarta.persistence.criteria.Root<Product> root,
            List<Predicate> predicates) {
        if (criteria.getProviders() != null && !criteria.getProviders().isEmpty()) {
            predicates.add(root.get(Product_.provider).in(criteria.getProviders()));
        }
    }

    private void addClassificationsPredicate(ProductSearchCriteria criteria, jakarta.persistence.criteria.Root<Product> root,
            List<Predicate> predicates) {
        if (criteria.getClassifications() != null && !criteria.getClassifications().isEmpty()) {
            predicates.add(root.get(Product_.CLASSIFICATIONS).get(ProductClassification_.VALUE)
                    .in(criteria.getClassifications()));
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
            cq.select(root.get(Product_.PROVIDER)).where(cb.isNotNull(root.get(Product_.PROVIDER)));
            return this.getEntityManager().createQuery(cq).getResultList().stream().distinct();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_ALL_PROVIDERS, ex);
        }
    }

    public enum ErrorKeys {

        ERROR_FIND_PRODUCT_BY_PRODUCT_NAMES,

        ERROR_FIND_PRODUCTS_BY_CRITERIA,

        ERROR_FIND_ALL_PROVIDERS,

        ERROR_FIND_PRODUCT_BY_NAME;
    }
}
