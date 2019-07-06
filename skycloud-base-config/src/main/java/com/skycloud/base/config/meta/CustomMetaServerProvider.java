package com.skycloud.base.config.meta;

import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.core.spi.MetaServerProvider;
import com.skycloud.base.config.util.ResourceYmlUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * For custom meta server configuration use, i.e. application.yml.properties
 *
 * @author sky
 */
@Slf4j
public class CustomMetaServerProvider implements MetaServerProvider {

    public static final int ORDER = MetaServerProvider.LOWEST_PRECEDENCE - 20;

    private static final Map<String, Map> domains = new HashMap<>();

    public CustomMetaServerProvider() {
        initialize();
    }

    private void initialize() {
        Map map = ResourceYmlUtil.readConfigFile("application.yml");
        Map apollo = (Map) map.get("apollo");
        Map custom = null;
        if (apollo != null) {
            custom = (Map) apollo.get("custom");
        }
        if (apollo != null && custom != null) {
            domains.putAll(custom);
        }
        if (custom != null) {
            custom.clear();
        }
        if (apollo != null) {
            apollo.clear();
        }

    }

    @Override
    public String getMetaServerAddress(Env targetEnv) {
        String metaServerAddress = null;
        String env = targetEnv.name().toLowerCase();
        env = Env.UNKNOWN.name().toLowerCase().equals(env)?"dev":env;
        Map map = domains.get(env);
        if (map != null) {
            metaServerAddress = Objects.toString(map.get("meta"), null);
            log.info("skycloud base config load meta server address env:{} meta:{}", env, metaServerAddress);
        }
        return metaServerAddress == null ? null : metaServerAddress.trim();
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
