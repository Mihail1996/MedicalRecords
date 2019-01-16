package tecnico.ulisboa.kms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@ServletComponentScan
@ImportAutoConfiguration
public class KmsApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(KmsApplication.class, args);

    }
}
