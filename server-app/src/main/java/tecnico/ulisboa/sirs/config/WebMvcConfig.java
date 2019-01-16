package tecnico.ulisboa.sirs.config;


import org.ow2.authzforce.core.pdp.impl.BasePdpEngine;
import org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public BasePdpEngine xacmlEngine() throws Exception {
        final PdpEngineConfiguration pdpEngineConfig = PdpEngineConfiguration.getInstance("classpath:access-control/pdp.xml");
        return new BasePdpEngine(pdpEngineConfig);
    }
}
