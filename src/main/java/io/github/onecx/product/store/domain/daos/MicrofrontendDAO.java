package io.github.onecx.product.store.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import io.github.onecx.product.store.domain.models.Microfrontend;
import io.github.onecx.product.store.domain.models.Microfrontend_;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class MicrofrontendDAO extends AbstractDAO<Microfrontend> {

    public Microfrontend findByMfeId(String mfeId) {
        if (mfeId == null) {
            return null;
        }
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Microfrontend.class);
            var root = cq.from(Microfrontend.class);
            cq.where(cb.equal(root.get(Microfrontend_.MFE_ID), mfeId));
            return this.getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception ex) {
            throw new DAOException(ErrorKey.ERROR_FIND_MFE_BY_ID, ex, mfeId);
        }
    }

    public enum ErrorKey {

        ERROR_FIND_MFE_BY_ID;
    }
}
