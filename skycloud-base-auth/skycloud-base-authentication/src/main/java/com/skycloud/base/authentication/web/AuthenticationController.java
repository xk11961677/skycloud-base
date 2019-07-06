package com.skycloud.base.authentication.web;

import com.skycloud.base.authentication.service.AuthenticationService;
import com.sky.framework.model.dto.MessageRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author
 */
@RestController
@Slf4j
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    /**
     *
     * @param url
     * @param method
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/auth/permission")
    @ResponseBody
    public MessageRes decide(@RequestParam String url, @RequestParam String method, HttpServletRequest request) {
        log.debug("authentication controller :{}");
        boolean decide = authenticationService.decide(new HttpServletRequestAuthWrapper(request, url, method));
        return MessageRes.success(decide);
    }

}