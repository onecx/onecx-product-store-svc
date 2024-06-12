package org.tkit.onecx.product.store.domain.daos;

import static org.tkit.quarkus.jpa.utils.QueryCriteriaUtil.addSearchStringPredicate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.Predicate;

import org.tkit.onecx.product.store.domain.criteria.ProductSearchCriteria;
import org.tkit.onecx.product.store.domain.criteria.SlotSearchCriteria;
import org.tkit.onecx.product.store.domain.models.Slot;
import org.tkit.onecx.product.store.domain.models.Slot_;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.models.AbstractTraceableEntity_;

@ApplicationScoped
public class SlotDAO extends AbstractDAO<Slot> {

    public PageResult<Slot> findByCriteria(SlotSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Slot.class);
            var root = cq.from(Slot.class);

            List<Predicate> predicates = new ArrayList<>();
            addSearchStringPredicate(predicates, cb, root.get(Slot_.NAME), criteria.getName());
            addSearchStringPredicate(predicates, cb, root.get(Slot_.PRODUCT_NAME), criteria.getProductName());
            addSearchStringPredicate(predicates, cb, root.get(Slot_.APP_ID), criteria.getAppId());
            if (!predicates.isEmpty()) {
                cq.where(predicates.toArray(new Predicate[] {}));
            }
            cq.orderBy(cb.desc(root.get(AbstractTraceableEntity_.CREATION_DATE)));
            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_SLOTS_BY_CRITERIA, ex);
        }
    }

    public Slot findByProductNameAppId(String productName, String appId, String name) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Slot.class);
            var root = cq.from(Slot.class);
            cq.where(cb.and(
                    cb.equal(root.get(Slot_.APP_ID), appId),
                    cb.equal(root.get(Slot_.NAME), name),
                    cb.equal(root.get(Slot_.PRODUCT_NAME), productName)));
            return this.getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_SLOT_PRODUCT_NAME_APP_ID_NAME, ex, appId);
        }
    }

    public void updateByProductName(String productName, String updatedProductName) {
        try {
            var cb = getEntityManager().getCriteriaBuilder();
            var uq = this.updateQuery();
            var root = uq.from(Slot.class);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get(Slot_.PRODUCT_NAME), productName));

            uq.set(Slot_.PRODUCT_NAME, updatedProductName)
                    .set(AbstractTraceableEntity_.modificationCount,
                            cb.sum(root.get(AbstractTraceableEntity_.modificationCount), 1))
                    .set(AbstractTraceableEntity_.modificationDate, cb.currentTimestamp().as(LocalDateTime.class))
                    .where(cb.and(predicates.toArray(new Predicate[0])));

            this.getEntityManager().createQuery(uq).executeUpdate();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_UPDATE_BY_PRODUCT_NAME, ex, productName);
        }
    }

    public void deleteByProductName(String productName) {
        try {
            var cb = getEntityManager().getCriteriaBuilder();
            var dq = this.deleteQuery();
            var root = dq.from(Slot.class);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get(Slot_.PRODUCT_NAME), productName));

            dq.where(cb.and(predicates.toArray(new Predicate[0])));

            this.getEntityManager().createQuery(dq).executeUpdate();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_DELETE_BY_PRODUCT_NAME, ex, productName);
        }
    }

    public Stream<Slot> loadByCriteria(ProductSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Slot.class);
            var root = cq.from(Slot.class);

            if (!criteria.getProductNames().isEmpty()) {
                cq.where(root.get(Slot_.PRODUCT_NAME).in(criteria.getProductNames()));
            }
            return this.getEntityManager()
                    .createQuery(cq)
                    .getResultStream();
        } catch (Exception exception) {
            throw new DAOException(ErrorKeys.ERROR_FIND_SLOTS_BY_CRITERIA, exception);
        }
    }

    public enum ErrorKeys {
        ERROR_UPDATE_BY_PRODUCT_NAME,
        ERROR_DELETE_BY_PRODUCT_NAME,
        ERROR_FIND_SLOT_PRODUCT_NAME_APP_ID_NAME,
        ERROR_FIND_SLOTS_BY_CRITERIA,

    }

}
