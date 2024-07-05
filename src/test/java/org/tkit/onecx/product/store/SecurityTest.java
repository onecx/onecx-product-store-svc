package org.tkit.onecx.product.store;

import java.util.List;

import org.tkit.quarkus.security.test.AbstractSecurityTest;
import org.tkit.quarkus.security.test.SecurityTestConfig;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SecurityTest extends AbstractSecurityTest {
    @Override
    public SecurityTestConfig getConfig() {
        SecurityTestConfig config = new SecurityTestConfig();
        config.addConfig("read", "/internal/products/id", 404, List.of("ocx-ps:read"), "get");
        config.addConfig("write", "/internal/products", 400, List.of("ocx-ps:write"), "post");
        return config;
    }
}
