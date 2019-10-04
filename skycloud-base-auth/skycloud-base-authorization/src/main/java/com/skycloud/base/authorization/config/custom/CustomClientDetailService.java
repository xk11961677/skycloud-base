package com.skycloud.base.authorization.config.custom;

import com.sky.framework.common.LogUtils;
import com.sky.framework.model.exception.BusinessException;
import com.skycloud.base.authorization.exception.AuthErrorType;
import com.skycloud.base.authorization.model.bo.ClientDetailsBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;

/**
 * @author
 */
@Component
@Slf4j
public class CustomClientDetailService {

    private static final String HTTP_BASIC = "Basic ";

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 验证客户端
     *
     * @param request
     */
    public ClientDetailsBo verifyClient(HttpServletRequest request, String grantType) {
        ClientDetailsBo clientDetailsBo;
        try {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (header == null || !header.startsWith(HTTP_BASIC)) {
                throw new UnapprovedClientAuthenticationException("请求头中无client信息");
            }
            String[] tokens = extractAndDecodeHeader(header);
            assert tokens.length == 2;
            String clientId = tokens[0];
            String clientSecret = tokens[1];
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
            if (clientDetails == null) {
                throw new UnapprovedClientAuthenticationException("clientId对应的配置信息不存在" + clientId);
            }
            boolean matches = passwordEncoder.matches(clientSecret, clientDetails.getClientSecret());
            if (!matches) {
                throw new UnapprovedClientAuthenticationException("clientSecret不匹配" + clientId);
            }
            clientDetailsBo = new ClientDetailsBo();
            clientDetailsBo.setClientDetails(clientDetails);
            clientDetailsBo.setClientId(clientId);
            clientDetailsBo.setGrantType(grantType);
        } catch (Exception e) {
            LogUtils.error(log, "invalid client:{}", e);
            throw new BusinessException(AuthErrorType.INVALID_CLIENT.getCode(), AuthErrorType.INVALID_CLIENT.getMsg());
        }
        return clientDetailsBo;
    }


    /**
     * 获取头部信息
     *
     * @param header
     * @return
     * @throws IOException
     */
    private String[] extractAndDecodeHeader(String header) throws IOException {
        byte[] base64Token = header.substring(6).getBytes("UTF-8");
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Failed to decode basic authentication token");
        }
        String token = new String(decoded, "UTF-8");
        int delim = token.indexOf(":");
        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }
}
