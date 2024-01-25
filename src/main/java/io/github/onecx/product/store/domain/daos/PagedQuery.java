package io.github.onecx.product.store.domain.daos;

import java.util.Iterator;
import java.util.stream.Stream;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

import org.hibernate.query.sqm.tree.SqmCopyContext;
import org.hibernate.query.sqm.tree.select.SqmOrderByClause;
import org.hibernate.query.sqm.tree.select.SqmQueryPart;
import org.hibernate.query.sqm.tree.select.SqmSelectStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;

public class PagedQuery<T> {
    private static final Logger log = LoggerFactory.getLogger(PagedQuery.class);
    protected static final String HINT_LOAD_GRAPH = "javax.persistence.loadgraph";
    private EntityManager em;
    private EntityGraph entityGraph;
    private CriteriaQuery<T> criteria;
    private CriteriaQuery<Long> countCriteria;
    private Page page;

    public PagedQuery(EntityManager em, CriteriaQuery<T> criteria, Page page, String idAttributeName, EntityGraph entityGraph) {
        this.em = em;
        this.criteria = setDefaultSorting(em, criteria, idAttributeName);
        this.page = page;
        this.countCriteria = createCountCriteria(em, criteria);
        this.entityGraph = entityGraph;
    }

    public PageResult<T> getPageResult() {
        try {
            Long count = (Long) this.em.createQuery(this.countCriteria).getSingleResult();
            if (count == 0L) {
                return PageResult.empty();
            } else {
                Stream<T> stream = this.em.createQuery(this.criteria)
                        .setHint(HINT_LOAD_GRAPH, this.entityGraph)
                        .setFirstResult(this.page.number() * this.page.size())
                        .setMaxResults(this.page.size())
                        .getResultStream();

                return new PageResult(count, stream, this.page);
            }
        } catch (NoResultException var3) {
            return PageResult.empty();
        } catch (Exception var4) {
            String entityClass = this.criteria.getResultType() != null ? this.criteria.getResultType().getName() : null;
            throw new DAOException(org.tkit.quarkus.jpa.daos.PagedQuery.Errors.GET_PAGE_RESULT_ERROR, var4,
                    new Object[] { this.page.number(), this.page.size(), entityClass });
        }
    }

    private static <T> CriteriaQuery<T> setDefaultSorting(EntityManager em, CriteriaQuery<T> criteria, String idAttributeName) {
        Root<T> root = null;

        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            if (criteria.getOrderList().isEmpty()) {
                log.warn(
                        "Paged query used without explicit orderBy. Ordering of results between pages not guaranteed. Please add an orderBy clause to your query.");
                root = findRoot(criteria, criteria.getResultType());
                if (root != null) {
                    criteria.orderBy(new Order[] { builder.asc(root.get(idAttributeName)) });
                    log.warn("Default sorting by '{}' attribute is added.", idAttributeName);
                }
            }
        } catch (IllegalArgumentException var5) {
            log.error("There is no id attribute for Root:{}", root);
        }

        return criteria;
    }

    public Page getPage() {
        return this.page;
    }

    public CriteriaQuery<Long> countCriteria() {
        return this.countCriteria;
    }

    public CriteriaQuery<T> criteria() {
        return this.criteria;
    }

    public PagedQuery<T> previous() {
        if (this.page.number() > 0) {
            this.page = Page.of(this.page.number() - 1, this.page.size());
        }

        return this;
    }

    public PagedQuery<T> next() {
        this.page = Page.of(this.page.number() + 1, this.page.size());
        return this;
    }

    public static <T> CriteriaQuery<Long> createCountCriteria(EntityManager em, CriteriaQuery<T> criteria) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> countCriteria = createCountCriteriaQuery(builder, criteria);
        Root<T> root = findRoot(countCriteria, criteria.getResultType());
        Expression countExpression;
        if (criteria.isDistinct()) {
            countExpression = builder.countDistinct(root);
        } else {
            countExpression = builder.count(root);
        }

        return countCriteria.select(countExpression);
    }

    public static <T> CriteriaQuery<Long> createCountCriteriaQuery(CriteriaBuilder builder, CriteriaQuery<T> from) {
        CriteriaQuery<Long> result = builder.createQuery(Long.class);
        SqmSelectStatement<T> copy = ((SqmSelectStatement) from).copy(SqmCopyContext.simpleContext());
        SqmSelectStatement<T> r = (SqmSelectStatement) result;
        SqmQueryPart<T> part = copy.getQueryPart();
        part.setOrderByClause((SqmOrderByClause) null);
        r.setQueryPart(part);
        r.getOrderList().clear();
        return result;
    }

    public static <T> Root<T> findRoot(CriteriaQuery<?> query, Class<T> clazz) {
        Iterator var2 = query.getRoots().iterator();

        Root r;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            r = (Root) var2.next();
        } while (!clazz.equals(r.getJavaType()));

        return r;
    }

    public static void copyJoins(From<?, ?> from, From<?, ?> to, org.tkit.quarkus.jpa.daos.PagedQuery.AliasCounter counter) {
        from.getJoins().forEach((join) -> {
            Join<?, ?> item = to.join(join.getAttribute().getName(), join.getJoinType());
            item.alias(createAlias(join, counter));
            copyJoins(join, item, counter);
        });
    }

    public static void copyFetches(From<?, ?> from, From<?, ?> to) {
        from.getFetches().forEach((fetch) -> {
            Fetch<?, ?> item = to.fetch(fetch.getAttribute().getName(), fetch.getJoinType());
            copyFetches(fetch, item);
        });
    }

    public static void copyFetches(Fetch<?, ?> from, Fetch<?, ?> to) {
        from.getFetches().forEach((fetch) -> {
            Fetch<?, ?> item = to.fetch(fetch.getAttribute().getName(), fetch.getJoinType());
            copyFetches(fetch, item);
        });
    }

    public static <T> String createAlias(Selection<T> selection, org.tkit.quarkus.jpa.daos.PagedQuery.AliasCounter counter) {
        String alias = selection.getAlias();
        if (alias == null) {
            alias = counter.next();
            selection.alias(alias);
        }

        return alias;
    }

    public String toString() {
        return "PagedQuery{page=" + this.page + "}";
    }

    public static enum Errors {
        GET_PAGE_RESULT_ERROR;

        private Errors() {
        }
    }

    public static class AliasCounter {
        private long index = 0L;

        public AliasCounter() {
        }

        public String next() {
            long var10002 = (long) (this.index++);
            return "a_" + var10002;
        }
    }
}
