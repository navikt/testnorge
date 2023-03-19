package no.nav.udistub.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j2.callback.SimplePasswordValidationCallbackHandler;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;

import java.util.List;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "udistub")
    public Wsdl11Definition defaultWsdl11Definition() {
        SimpleWsdl11Definition wsdl11Definition = new SimpleWsdl11Definition();
        wsdl11Definition.setWsdl(new ClassPathResource("/wsdl/MT_1067_NAV_v1.wsdl"));
        return wsdl11Definition;
    }

//    @Bean(name = "udistub")
//    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchemaCollection udistubSchema) {
//        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
//        wsdl11Definition.setPortTypeName("MT_1067_NAV_v1Port");
//        wsdl11Definition.setLocationUri("/ws");
//        wsdl11Definition.setTargetNamespace("http://udi.no.MT_1067_NAV.v1");
//        wsdl11Definition.setSchemaCollection(udistubSchema);
//        return wsdl11Definition;
//    }
//
//    @Bean
//    public XsdSchemaCollection udistubSchema() {
//        return new CommonsXsdSchemaCollection(
//                new ClassPathResource("/wsdl/schema/MT_1067_NAV_v1.xsd"),
//                new ClassPathResource("/wsdl/schema/Common_v2.xsd"),
//                new ClassPathResource("/wsdl/schema/MT_1067_NAV_Data_v1.xsd"),
//                new ClassPathResource("/wsdl/schema/Common_Fault_v3.xsd"),
//                new ClassPathResource("/wsdl/schema/Common_Headers_v2.xsd"),
//                new ClassPathResource("/wsdl/schema/Common_Tilgangskontroll_v1.xsd")
//        );
//    }

    @Bean
    public Wss4jSecurityInterceptor securityInterceptor() {
        Wss4jSecurityInterceptor securityInterceptor = new Wss4jSecurityInterceptor();
        securityInterceptor.setValidationActions("Timestamp UsernameToken Encrypt Signature NoSecurity");
        securityInterceptor.setValidationCallbackHandler(securityCallbackHandler());
        return securityInterceptor;
    }

    @Override
    public void addInterceptors(List interceptors) {
        interceptors.add(securityInterceptor());
    }

    @Bean
    public SimplePasswordValidationCallbackHandler securityCallbackHandler() {
        return new SimplePasswordValidationCallbackHandler();
    }
}
