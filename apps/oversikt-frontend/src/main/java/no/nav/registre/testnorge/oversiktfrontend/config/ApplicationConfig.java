package no.nav.registre.testnorge.oversiktfrontend.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.autoconfigdependencyanalysis.config.DependencyAnalysisAutoConfiguration;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2FrontendConfiguration;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        SecureOAuth2FrontendConfiguration.class,
        DependencyAnalysisAutoConfiguration.class
})
public class ApplicationConfig {

    @Bean
    public FilterRegistrationBean<SessionTimeoutCookieFilter> loggingFilter(){
        FilterRegistrationBean<SessionTimeoutCookieFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new SessionTimeoutCookieFilter());
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }

}
