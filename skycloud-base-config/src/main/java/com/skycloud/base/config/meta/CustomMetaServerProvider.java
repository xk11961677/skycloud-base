/*
 * The MIT License (MIT)
 * Copyright © 2019 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.skycloud.base.config.meta;

import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.core.spi.MetaServerProvider;
import com.skycloud.base.config.util.ResourceYmlUtils;
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
        Map map = ResourceYmlUtils.readConfigFile("application.yml");
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
