package org.tkit.onecx.product.store.domain.wrapper;

import java.util.List;

import org.tkit.onecx.product.store.domain.models.Microfrontend;
import org.tkit.onecx.product.store.domain.models.Microservice;
import org.tkit.onecx.product.store.domain.models.Product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductLoadResultWrapper {
    private List<Product> products;
    private List<Microservice> microservices;
    private List<Microfrontend> microfrontends;
    private Long size;
    private Long totalPages;
    private Long number;
    private Long totalElements;
}
