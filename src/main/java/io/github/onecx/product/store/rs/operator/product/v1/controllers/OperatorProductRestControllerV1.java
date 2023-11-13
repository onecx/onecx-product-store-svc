package io.github.onecx.product.store.rs.operator.product.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import gen.io.github.onecx.product.store.rs.operator.product.v1.OperatorProductApi;
import gen.io.github.onecx.product.store.rs.operator.product.v1.model.UpdateProductRequestDTOV1;
import io.github.onecx.product.store.domain.daos.ProductDAO;
import io.github.onecx.product.store.rs.operator.product.v1.mappers.OperatorProductMapperV1;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
@Path("/operator/product/v1/update/{name}")
public class OperatorProductRestControllerV1 implements OperatorProductApi {

    @Inject
    ProductDAO dao;

    @Inject
    OperatorProductMapperV1 mapper;

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public Response createOrUpdateProduct(String name, UpdateProductRequestDTOV1 dto) {

        var product = dao.findProductByName(name);
        if (product == null) {

            product = mapper.create(dto);
            product.setName(name);
            dao.create(product);

            return Response.status(Response.Status.CREATED).build();
        }

        mapper.update(product, dto);
        dao.update(product);
        return Response.status(Response.Status.OK).build();
    }
}
