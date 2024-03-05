package org.tkit.onecx.product.store.rs.internal.services;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.product.store.domain.daos.ImageDAO;
import org.tkit.onecx.product.store.domain.daos.MicrofrontendDAO;
import org.tkit.onecx.product.store.domain.daos.MicroserviceDAO;
import org.tkit.onecx.product.store.domain.daos.ProductDAO;
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

    @Transactional
    public void updateProductAndRelatedMfeAndMs(String oldProductName, Product updateItem) {
        microfrontendDAO.updateByProductName(oldProductName, updateItem.getName());
        microserviceDAO.updateByProductName(oldProductName, updateItem.getName());
        productDAO.update(updateItem);
    }

    @Transactional
    public void deleteProduct(String id) {
        var product = productDAO.findById(id);
        if (product != null) {
            List<Microfrontend> productRelatedMfes = microfrontendDAO.loadByProductName(product.getName()).toList();
            microfrontendDAO.delete(productRelatedMfes);
            microserviceDAO.deleteByProductName(product.getName());
            productDAO.deleteQueryById(id);

            // workaround for images
            imageDAO.deleteQueryByRefId(product.getName());
        }

    }
}
