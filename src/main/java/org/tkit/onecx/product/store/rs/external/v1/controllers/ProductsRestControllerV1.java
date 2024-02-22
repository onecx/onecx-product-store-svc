package org.tkit.onecx.product.store.rs.external.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.product.store.domain.daos.MicrofrontendDAO;
import org.tkit.onecx.product.store.domain.daos.MicroserviceDAO;
import org.tkit.onecx.product.store.domain.daos.ProductDAO;
import org.tkit.onecx.product.store.domain.wrapper.ProductLoadResultWrapper;
import org.tkit.onecx.product.store.rs.external.v1.mappers.ExceptionMapperV1;
import org.tkit.onecx.product.store.rs.external.v1.mappers.ProductMapperV1;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.product.store.rs.external.v1.ProductsApi;
import gen.org.tkit.onecx.product.store.rs.external.v1.model.*;

@LogService
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ProductsRestControllerV1 implements ProductsApi {

    @Inject
    ExceptionMapperV1 exceptionMapper;

    @Inject
    ProductMapperV1 mapper;

    @Inject
    ProductDAO dao;

    @Inject
    MicrofrontendDAO microfrontendDAO;

    @Inject
    MicroserviceDAO microserviceDAO;

    @Override
    public Response loadProductsByCriteria(ProductItemLoadSearchCriteriaDTOv1 productItemLoadSearchCriteriaDTOv1) {
        var criteria = mapper.map(productItemLoadSearchCriteriaDTOv1);

        ProductLoadResultWrapper wrapper = new ProductLoadResultWrapper();

        var products = dao.findProductsByCriteria(criteria);
        var microservices = microserviceDAO.loadByCriteria(criteria).getStream().toList();
        var microfrontends = microfrontendDAO.loadByCriteria(criteria).getStream().toList();
        wrapper.setProducts(products.getStream().toList());
        wrapper.setMicrofrontends(microfrontends);
        wrapper.setMicroservices(microservices);
        wrapper.setTotalPages(products.getTotalPages());
        wrapper.setNumber(products.getNumber());
        wrapper.setSize(products.getSize());
        wrapper.setTotalElements(products.getTotalElements());

        ProductsLoadResultDTOv1 resultDTOv1 = mapper.map(wrapper);
        return Response.ok(resultDTOv1).build();
    }

    @Override
    public Response getProductByName(String name) {
        var product = dao.findProductByName(name);
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var microfrontends = microfrontendDAO.loadByProductName(product.getName());
        var microservices = microserviceDAO.loadByProductName(product.getName());

        var dto = mapper.map(product, microfrontends, microservices);

        return Response.ok(dto).build();
    }

    @Override
    public Response searchProductsByCriteria(ProductItemSearchCriteriaDTOv1 productSearchCriteriaDTOV1) {
        var criteria = mapper.map(productSearchCriteriaDTOV1);
        var result = dao.findProductsByCriteria(criteria);
        return Response.ok(mapper.mapPageResult(result)).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTOv1> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
