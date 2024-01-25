package io.github.onecx.product.store.domain.models;

import static jakarta.persistence.CascadeType.*;

import java.util.HashSet;
import java.util.Set;

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
@NamedEntityGraph(name = Product.PRODUCT_LOAD, includeAllAttributes = true) // TODO CSO: only dedicated attributes
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

    @ElementCollection(fetch = FetchType.EAGER)
    //@ElementCollection(fetch = FetchType.LAZY)
    // @CollectionTable(name = "PRODUCT_CLASSIFICATIONS")
    @CollectionTable(name = "PRODUCT_CLASSIFICATIONS", joinColumns = @JoinColumn(name = "PRODUCT_GUID", referencedColumnName = "GUID"))
    @Column(name = "CLASSIFICATIONS")
    private Set<String> classifications = new HashSet<>();;

}
