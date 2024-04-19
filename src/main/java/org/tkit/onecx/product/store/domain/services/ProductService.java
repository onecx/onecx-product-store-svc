package org.tkit.onecx.product.store.domain.services;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.product.store.domain.daos.*;
import org.tkit.onecx.product.store.domain.models.Microfrontend;
import org.tkit.onecx.product.store.domain.models.Product;

@ApplicationScoped
public class ProductService {
    @Inject
    MicrofrontendDAO microfrontendDAO;

    @Inject
    MicroserviceDAO microserviceDAO;

    @Inject
    ProductDAO productDAO;

    @Inject
    ImageDAO imageDAO;

    @Inject
    SlotDAO slotDAO;

    @Transactional
    public void updateProductAndRelatedMfeAndMs(String oldProductName, Product updateItem) {
        microfrontendDAO.updateByProductName(oldProductName, updateItem.getName());
        microserviceDAO.updateByProductName(oldProductName, updateItem.getName());
        slotDAO.updateByProductName(oldProductName, updateItem.getName());
        productDAO.update(updateItem);
    }

    @Transactional
    public void deleteProduct(String id) {
        var product = productDAO.findById(id);
        if (product != null) {
            List<Microfrontend> productRelatedMfes = microfrontendDAO.loadByProductName(product.getName()).toList();
            microfrontendDAO.delete(productRelatedMfes);
            microserviceDAO.deleteByProductName(product.getName());
            slotDAO.deleteByProductName(product.getName());
            productDAO.deleteQueryById(id);

            // workaround for images
            imageDAO.deleteQueryByRefId(product.getName());
        }

    }
}
