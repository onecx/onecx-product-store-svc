package org.tkit.onecx.product.store.rs.internal.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.product.store.domain.daos.MicrofrontendDAO;
import org.tkit.onecx.product.store.domain.daos.MicroserviceDAO;
import org.tkit.onecx.product.store.domain.daos.ProductDAO;
import org.tkit.onecx.product.store.domain.models.Product;

@ApplicationScoped
public class ProductService {
    @Inject
    MicrofrontendDAO microfrontendDAO;

    @Inject
    MicroserviceDAO microserviceDAO;

    @Inject
    ProductDAO productDAO;

    @Transactional
    public void updateProductAndRelatedMfeAndMs(String oldProductName, Product updateItem) {
        microfrontendDAO.updateByProductName(oldProductName, updateItem.getName());
        microserviceDAO.updateByProductName(oldProductName, updateItem.getName());
        productDAO.update(updateItem);
    }
}
