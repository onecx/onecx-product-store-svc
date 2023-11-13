package io.github.onecx.product.store.domain.models;

import jakarta.persistence.*;

import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "PS_PRODUCT", uniqueConstraints = {
        @UniqueConstraint(name = "UI_PS_PRODUCT_NAME", columnNames = { "NAME" }),
        @UniqueConstraint(name = "UI_PS_PRODUCT_BASE_PATH", columnNames = { "BASE_PATH" })
})
@SuppressWarnings("squid:S2160")
public class Product extends TraceableEntity {

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "BASE_PATH")
    private String basePath;
}
