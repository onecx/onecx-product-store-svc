package org.tkit.onecx.product.store.domain.models;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.FetchType.LAZY;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "PRODUCT", uniqueConstraints = {
        @UniqueConstraint(name = "UI_PRODUCT_NAME", columnNames = { "NAME" }),
        @UniqueConstraint(name = "UI_PRODUCT_BASE_PATH", columnNames = { "BASE_PATH" }),
}, indexes = { @Index(name = "product_provider_idx", columnList = "PROVIDER") })
@NamedEntityGraph(name = Product.PRODUCT_LOAD, includeAllAttributes = true)
@SuppressWarnings("java:S2160")
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
    private String productClassifications;

    @OneToMany(cascade = { REMOVE, REFRESH, PERSIST, MERGE }, fetch = LAZY, orphanRemoval = true)
    @JoinColumn(name = "PRODUCT_ID")
    private Set<ProductClassification> classifications = new HashSet<>();

    @Column(name = "UNDEPLOYED")
    private Boolean undeployed;

    @Column(name = "PROVIDER")
    private String provider;
}
