package com.skycloud.base.authorization.swagger;

import org.springframework.stereotype.Component;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.DocumentationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author
 */
@Component
public class SwaggerAddtion implements ApiListingScannerPlugin {
    @Override
    public List<ApiDescription> apply(DocumentationContext documentationContext) {
        return new ArrayList<>(Arrays.asList(SwaggerMobile.create(),SwaggerLogin.create()));
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return DocumentationType.SWAGGER_2.equals(documentationType);
    }
}
