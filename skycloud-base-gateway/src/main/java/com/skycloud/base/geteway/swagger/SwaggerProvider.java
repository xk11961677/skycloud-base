package com.skycloud.base.geteway.swagger;

import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author
 */
@Component
@Primary
public class SwaggerProvider implements SwaggerResourcesProvider {

    private static final String API_URI = "/v2/api-docs";

    private static final String CONSUL_PREFIX = "CompositeDiscoveryClient_";

    private static final String exclude_instance_prefix = "consul,apollo-configservice,config,apollo-adminservice,apollo-portal,skycloud-base-gateway";

    private final RouteLocator routeLocator;

    private final GatewayProperties gatewayProperties;

    public SwaggerProvider(RouteLocator routeLocator, GatewayProperties gatewayProperties) {
        this.routeLocator = routeLocator;
        this.gatewayProperties = gatewayProperties;
    }

    @Override

    public List<SwaggerResource> get() {

        List<SwaggerResource> resources = new ArrayList<>();

        List<String> routes = new ArrayList<>();

        //取出gateway的route

        routeLocator.getRoutes().subscribe(route -> routes.add(route.getId().replace(CONSUL_PREFIX,"")));

        //结合配置的route-路径(Path)，和route过滤，只获取有效的route节点

//        gatewayProperties.getRoutes().stream().filter(routeDefinition -> routes.contains(routeDefinition.getId()))
//
//                .forEach(routeDefinition -> routeDefinition.getPredicates().stream()
//
//                        .filter(predicateDefinition -> ("Path").equalsIgnoreCase(predicateDefinition.getName()))
//
//                        .forEach(predicateDefinition -> resources.add(swaggerResource(routeDefinition.getId(),
//
//                                predicateDefinition.getArgs().get("\"pattern\"")
//
//                                        .replace("/**", API_URI)))));
//        routeLocator.getRoutes().filter(route -> route.getUri().getHost() != null)
//                .filter(route -> !self.equals(route.getUri().getHost()))
//                .subscribe(route -> routeHosts.add(route.getUri().getHost()));

        Set<String> dealed = new HashSet<>();
        routes.forEach(instance -> {
            String url = "/" + instance + API_URI;
            if (!dealed.contains(url) && exclude_instance_prefix.indexOf(instance) == -1) {
                dealed.add(url);
                SwaggerResource swaggerResource = new SwaggerResource();
                swaggerResource.setUrl(url);
                swaggerResource.setName(instance);
                resources.add(swaggerResource);
            }
        });
        return resources;

    }

    private SwaggerResource swaggerResource(String name, String location) {

        SwaggerResource swaggerResource = new SwaggerResource();

        swaggerResource.setName(name);

        swaggerResource.setLocation(location);

        swaggerResource.setSwaggerVersion("2.0");

        return swaggerResource;

    }

}

