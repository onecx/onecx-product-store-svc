package org.tkit.onecx.product.store.domain.wrapper;

import java.util.List;

import org.tkit.onecx.product.store.domain.models.Microfrontend;
import org.tkit.onecx.product.store.domain.models.Microservice;
import org.tkit.onecx.product.store.domain.models.Product;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RegisterForReflection
public class ProductLoadResultWrapper {
    private Product product;
    private List<Microservice> microservices;
    private List<Microfrontend> microfrontends;
}
