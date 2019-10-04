package com.skycloud.base.authorization.model.bo;

import lombok.Data;
import org.springframework.security.oauth2.provider.ClientDetails;

/**
 * @author
 */
@Data
public class ClientDetailsBo {

    private String clientId;

    private ClientDetails clientDetails;

    private String grantType;
}
