package com.skycloud.base.monitor;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapConfig;
import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author
 */
@SpringBootApplication
@EnableAdminServer
public class SkyCloudMonitorApplication {

    /**
     * Hazelcast config config.
     *
     * @return the config
     */
//	@Bean
//	public Config hazelcastConfig() {
//		return new Config().setProperty("hazelcast.jmx", "true")
//				.addMapConfig(new MapConfig("spring-boot-admin-application-store").setBackupCount(1)
//						.setEvictionPolicy(EvictionPolicy.NONE))
//				.addListConfig(new ListConfig("spring-boot-admin-event-store").setBackupCount(1)
//						.setMaxSize(1000));
//	}

    @Bean
    public Config hazelcastConfig() {
        MapConfig mapConfig = new MapConfig("spring-boot-admin-event-store").setInMemoryFormat(InMemoryFormat.OBJECT)
                .setBackupCount(1)
                .setEvictionPolicy(EvictionPolicy.NONE);
        return new Config().setProperty("hazelcast.jmx", "true").addMapConfig(mapConfig);
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(SkyCloudMonitorApplication.class, args);
    }
}
