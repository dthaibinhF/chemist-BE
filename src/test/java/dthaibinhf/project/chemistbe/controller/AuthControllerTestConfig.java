package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.service.AuthenticationService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthControllerTestConfig {

    @Bean
    public AuthenticationService authenticationService() {
        return Mockito.mock(AuthenticationService.class);
    }
}