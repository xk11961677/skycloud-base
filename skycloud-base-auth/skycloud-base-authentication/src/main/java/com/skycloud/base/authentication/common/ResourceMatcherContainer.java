package com.skycloud.base.authentication.common;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.HashMap;

/**
 * @author
 */
public class ResourceMatcherContainer extends HashMap<RequestMatcher, ConfigAttribute> {

    private ResourceMatcherContainer() {
    }

    public static ResourceMatcherContainer getInstance() {
        return ResourceMatcherContainerSingleton.INSTANCE.getInstance();
    }

    public enum ResourceMatcherContainerSingleton {
        INSTANCE;
        private final ResourceMatcherContainer instance;

        ResourceMatcherContainerSingleton() {
            instance = new ResourceMatcherContainer();
        }

        private ResourceMatcherContainer getInstance() {
            return instance;
        }
    }
}
