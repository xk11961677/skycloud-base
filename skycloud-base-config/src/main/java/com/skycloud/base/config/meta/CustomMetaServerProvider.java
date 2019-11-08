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
import com.skycloud.base.config.util.ResourceUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * For custom meta server configuration use, i.e. application.yml.properties
 *
 * @author sky
 */
@Slf4j
public class CustomMetaServerProvider implements MetaServerProvider {

    public static final int ORDER = MetaServerProvider.LOWEST_PRECEDENCE - 1;

    private static final Map<String, String> DOMAINS = new HashMap<>();

    public CustomMetaServerProvider() {
        initialize();
    }

    /**
     * 初始化加载配置文件,支持 yml yaml properties 三种格式,优先级从高到低
     */
    private void initialize() {
        Map map = ResourceUtils.readConfigFile("application");
        parser(map);
    }

    @Override
    public String getMetaServerAddress(Env targetEnv) {
        String LOG_INFO_TEMPLATE = "skycloud base config load meta server address env:{} meta:{}";
        String env = targetEnv.name().toLowerCase();
        env = Env.UNKNOWN.name().toLowerCase().equals(env) ? "dev" : env;
        String metaServerAddress = DOMAINS.get(env);
        if (metaServerAddress != null) {
            log.info(LOG_INFO_TEMPLATE, env, metaServerAddress);
        }
        if (Env.LOCAL.name().toLowerCase().equals(env)) {
            log.info(LOG_INFO_TEMPLATE, env, "Local development mode has no remote address");
        }
        return metaServerAddress == null ? null : metaServerAddress.trim();
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    /**
     * 解析不同格式返回的map
     *
     * @param map
     */
    private void parser(Map<String, Object> map) {
        if (map.get(ResourceUtils.FORMAT_KEY).equals(ResourceUtils.FORMAT_YML)) {
            Map apollo = (Map) map.get("apollo");
            Map<String, Object> custom = null;
            if (apollo != null) {
                custom = (Map<String, Object>) apollo.get("custom");
            }
            if (apollo != null && custom != null) {
                for (Map.Entry<String, Object> entry : custom.entrySet()) {
                    Map value = (Map) entry.getValue();
                    Object meta = value.get("meta");
                    if (meta != null) {
                        DOMAINS.put(entry.getKey(), meta.toString());
                    }
                }
            }
        }

        if (map.get(ResourceUtils.FORMAT_KEY).equals(ResourceUtils.FORMAT_PROPERTIES)) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("apollo.custom.") && key.endsWith(".meta")) {
                    key = key.replaceAll("apollo.custom.", "").replaceAll(".meta", "");
                    DOMAINS.put(key, entry.getValue().toString());
                }
            }
        }
    }
}
