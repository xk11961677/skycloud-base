package com.skycloud.base.config.health;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * config custom health check
 * @author
 */
@RestController
@RequestMapping("/consulhealth")
public class ConsulhealthController {

    @RequestMapping("/healthCheck")
    public String healthCheck() {
        return "ok";
    }
}
