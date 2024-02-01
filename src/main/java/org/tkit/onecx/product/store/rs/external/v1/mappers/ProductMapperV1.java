package org.tkit.onecx.product.store.rs.external.v1.mappers;

import java.util.List;
import java.util.stream.Stream;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.onecx.product.store.domain.criteria.ProductSearchCriteria;
import org.tkit.onecx.product.store.domain.models.Microfrontend;
import org.tkit.onecx.product.store.domain.models.Product;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.product.store.rs.external.v1.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ProductMapperV1 {

    ProductSearchCriteria map(ProductItemSearchCriteriaDTOv1 data);

    @Mapping(target = "removeStreamItem", ignore = true)
    ProductItemPageResultDTOv1 mapPageResult(PageResult<Product> page);

    ProductItemDTOv1 maPageItems(Product data);

    @Mapping(target = "removeMicrofrontendsItem", ignore = true)
    @Mapping(target = "microfrontends", ignore = true)
    ProductDTOv1 map(Product data);

    default ProductDTOv1 map(Product product, Stream<Microfrontend> microfrontends) {
        var p = map(product);
        p.setMicrofrontends(items(microfrontends));
        return p;
    }

    List<MicrofrontendDTOv1> items(Stream<Microfrontend> microfrontends);

    @Mapping(target = "removeEndpointsItem", ignore = true)
    MicrofrontendDTOv1 map(Microfrontend data);
}
