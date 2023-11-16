package io.github.onecx.product.store.domain.models;

import static jakarta.persistence.EnumType.STRING;

import jakarta.persistence.*;

import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PS_MICROFRONTEND", uniqueConstraints = {
        @UniqueConstraint(name = "PS_MICROFRONTEND_UNIQUE", columnNames = { "REMOTE_ENTRY", "REMOTE_BASE_URL",
                "EXPOSED_MODULE" }),
        @UniqueConstraint(name = "PS_MICROFRONTEND_MFE_ID", columnNames = { "MFE_ID" })
}, indexes = {
        @Index(name = "PS_MICROFRONTEND_PRODUCT_NAME", columnList = "PRODUCT_NAME")
})
@SuppressWarnings("java:S2160")
public class Microfrontend extends TraceableEntity {

    @Column(name = "BASE_PATH")
    private String basePath;

    @Column(name = "MFE_ID")
    private String mfeId;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "REMOTE_ENTRY")
    private String remoteEntry;

    @Column(name = "REMOTE_NAME")
    private String remoteName;

    @Column(name = "EXPOSED_MODULE")
    private String exposedModule;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "WC_TAG_NAME")
    private String wcTagName;

    @Column(name = "I18N_PATH")
    private String i18nPath;

    @Column(name = "CONTACT", columnDefinition = "TEXT")
    private String contact;

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "APP_VERSION")
    private String appVersion;

    @Column(name = "NOTE", columnDefinition = "TEXT")
    private String note;

    @Column(name = "REMOTE_BASE_URL")
    private String remoteBaseUrl;

    @Column(name = "MODULE_TYPE")
    @Enumerated(STRING)
    private ModuleType moduleType;

    @Column(name = "OPERATOR")
    private boolean operator;

    public enum ModuleType {
        ANGULAR,
        WEBCOMPONENT
    }
}
