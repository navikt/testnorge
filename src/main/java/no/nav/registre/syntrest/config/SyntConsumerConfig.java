//package no.nav.registre.syntrest.config;
//
//import lombok.RequiredArgsConstructor;
//import no.nav.registre.syntrest.consumer.SyntConsumer;
//import no.nav.registre.syntrest.kubernetes.ApplicationManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.DependsOn;
//
//@Configuration
//@ComponentScan(basePackageClasses = {ApplicationManagerConfig.class, AppConfig.class})
//@RequiredArgsConstructor
//public class SyntConsumerConfig {
//
//    private final ApplicationManager applicationManager;
//
//    @Bean
//    @DependsOn({"applicationManager", "restTemplate"})
//    SyntConsumer aaregConsumer() {
//        return new SyntConsumer(applicationManager, "synthdata-aareg");
//    }
//
//    @Bean
//    @DependsOn({"applicationManager", "restTemplate"})
//    SyntConsumer aapConsumer() {
//        return new SyntConsumer(applicationManager, "synthdata-arena-aap");
//    }
//
//    @Bean
//    @DependsOn({"applicationManager", "restTemplate"})
//    SyntConsumer bisysConsumer() {
//        return new SyntConsumer(applicationManager, "synthdata-arena-bisys");
//    }
//
//    @Bean
//    @DependsOn({"applicationManager", "restTemplate"})
//    SyntConsumer instConsumer() {
//        return new SyntConsumer(applicationManager, "synthdata-inst");
//    }
//
//    @Bean
//    @DependsOn({"applicationManager", "restTemplate"})
//    SyntConsumer medlConsumer() {
//        return new SyntConsumer(applicationManager, "synthdata-medl");
//    }
//
//    @Bean
//    @DependsOn({"applicationManager", "restTemplate"})
//    SyntConsumer meldekortConsumer() {
//        return new SyntConsumer(applicationManager, "synthdata-arena-meldekort");
//    }
//
//    @Bean
//    @DependsOn({"applicationManager", "restTemplate"})
//    SyntConsumer navConsumer() {
//        return new SyntConsumer(applicationManager, "synthdata-nav");
//    }
//
//    @Bean
//    @DependsOn({"applicationManager", "restTemplate"})
//    SyntConsumer poppConsumer() {
//        return new SyntConsumer(applicationManager, "synthdata-popp");
//    }
//
//    @Bean
//    @DependsOn({"applicationManager", "restTemplate"})
//    SyntConsumer samConsumer() {
//        return new SyntConsumer(applicationManager, "synthdata-sam");
//    }
//
//    @Bean
//    @DependsOn({"applicationManager", "restTemplate"})
//    SyntConsumer inntektConsumer() {
//        return new SyntConsumer(applicationManager, "synthdata-inntekt");
//    }
//
//    @Bean
//    @DependsOn({"applicationManager", "restTemplate"})
//    SyntConsumer tpConsumer() {
//        return new SyntConsumer(applicationManager, "synthdata-tp");
//    }
//
//    @Bean
//    @DependsOn({"applicationManager", "restTemplate"})
//    SyntConsumer tpsConsumer() {
//        return new SyntConsumer(applicationManager, "synthdata-tps");
//    }
//
//    @Bean
//    @DependsOn({"applicationManager", "restTemplate"})
//    SyntConsumer frikortConsumer() {
//        return new SyntConsumer(applicationManager, "synthdata-frikort");
//    }
//
//    @Bean
//    @DependsOn({"applicationManager", "restTemplate"})
//    SyntConsumer eiaConsumer() {
//        return new SyntConsumer(applicationManager, "synthdata-eia");
//    }
//}
