package io.github.onecx.product.store.domain.models;

import static jakarta.persistence.CascadeType.*;

import jakarta.persistence.*;

import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "PRODUCT", uniqueConstraints = {
        @UniqueConstraint(name = "UI_PRODUCT_NAME", columnNames = { "NAME" }),
        @UniqueConstraint(name = "UI_PRODUCT_BASE_PATH", columnNames = { "BASE_PATH" })
})
public class Product extends TraceableEntity {
    public static final String PRODUCT_LOAD = "PRODUCT_LOAD";

    @Column(name = "NAME")
    private String name;

    @Column(name = "VERSION")
    private String version;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "BASE_PATH")
    private String basePath;

    @Column(name = "OPERATOR", nullable = false)
    private boolean operator;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "ICON_NAME")
    private String iconName;

    @Column(name = "CLASSIFICATION")
    private String classifications;

}
