package no.nav.testnav.libs.servletcore.config;

import no.nav.testnav.libs.dto.jackson.v1.CaseInsensitiveEnumModule;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

@AutoConfiguration
@ConditionalOnClass(ObjectMapper.class)
public class JacksonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "caseInsensitiveEnumModule")
    public CaseInsensitiveEnumModule caseInsensitiveEnumModule() {
        return new CaseInsensitiveEnumModule();
    }
}

