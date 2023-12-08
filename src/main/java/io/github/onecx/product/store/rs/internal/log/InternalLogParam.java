package io.github.onecx.product.store.rs.internal.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.io.github.onecx.product.store.rs.internal.model.*;

@ApplicationScoped
public class InternalLogParam implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                item(10, CreateProductRequestDTO.class,
                        x -> x.getClass().getSimpleName() + ":" + ((CreateProductRequestDTO) x).getName()),
                item(10, UpdateProductRequestDTO.class,
                        x -> x.getClass().getSimpleName() + ":" + ((UpdateProductRequestDTO) x).getName()),
                item(10, ProductSearchCriteriaDTO.class, x -> {
                    ProductSearchCriteriaDTO d = (ProductSearchCriteriaDTO) x;
                    return ProductSearchCriteriaDTO.class.getSimpleName() + "[" + d.getPageNumber() + "," + d.getPageSize()
                            + "]";
                }),
                item(10, CreateMicrofrontendRequestDTO.class,
                        x -> x.getClass().getSimpleName() + ":" + ((CreateMicrofrontendRequestDTO) x).getAppId()),
                item(10, UpdateMicrofrontendRequestDTO.class,
                        x -> x.getClass().getSimpleName() + ":" + ((UpdateMicrofrontendRequestDTO) x).getAppId()),
                item(10, MicrofrontendSearchCriteriaDTO.class, x -> {
                    MicrofrontendSearchCriteriaDTO d = (MicrofrontendSearchCriteriaDTO) x;
                    return MicrofrontendSearchCriteriaDTO.class.getSimpleName() + "[" + d.getPageNumber() + ","
                            + d.getPageSize() + "]";
                }));
    }
}
