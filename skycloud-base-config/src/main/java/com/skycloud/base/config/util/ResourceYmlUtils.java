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
package com.skycloud.base.config.util;

import com.ctrip.framework.apollo.core.utils.ClassLoaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author
 */
public class ResourceYmlUtils {

    private static final Logger logger = LoggerFactory.getLogger(ResourceYmlUtils.class);

    private static final String[] DEFAULT_FILE_SEARCH_LOCATIONS = new String[]{"./config/", "./"};

    @SuppressWarnings("unchecked")
    public static Map<String,Object> readConfigFile(String configPath) {
        Map<String, Object> map = null;


        Yaml yaml = new Yaml();
        InputStream in = loadConfigFileFromDefaultSearchLocations(configPath);

        try {
            if (in != null) {
                map = yaml.load(in);
            }
        } catch (Exception ex) {
            logger.warn("Reading config failed: {}", ex.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    logger.warn("Close config failed: {}", ex.getMessage());
                }
            }
        }
        return map;
    }


    private static InputStream loadConfigFileFromDefaultSearchLocations(String configPath) {
        try {
            // load from default search locations
            for (String searchLocation : DEFAULT_FILE_SEARCH_LOCATIONS) {
                File candidate = Paths.get(searchLocation, configPath).toFile();
                if (candidate.exists() && candidate.isFile() && candidate.canRead()) {
                    logger.debug("Reading config from resource {}", candidate.getAbsolutePath());
                    return new FileInputStream(candidate);
                }
            }

            // load from classpath
            URL url = ClassLoaderUtil.getLoader().getResource(configPath);

            if (url != null) {
                InputStream in = getResourceAsStream(url);

                if (in != null) {
                    logger.debug("Reading config from resource {}", url.getPath());
                    return in;
                }
            }

            // load outside resource under current user path
            File candidate = new File(System.getProperty("user.dir"), configPath);
            if (candidate.exists() && candidate.isFile() && candidate.canRead()) {
                logger.debug("Reading config from resource {}", candidate.getAbsolutePath());
                return new FileInputStream(candidate);
            }
        } catch (FileNotFoundException e) {
            //ignore
        }
        return null;
    }

    private static InputStream getResourceAsStream(URL url) {
        try {
            return url != null ? url.openStream() : null;
        } catch (IOException e) {
            return null;
        }
    }
}
