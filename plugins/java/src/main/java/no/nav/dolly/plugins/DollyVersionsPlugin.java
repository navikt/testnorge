package no.nav.dolly.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class DollyVersionsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("versions", DollyVersionCatalog.class);
    }

    @SuppressWarnings("unused")
    public static class DollyVersionCatalog {
        public String navAltinnInntektsmelding = "1.2019.08.16-13.46-35cbdfd492d4";
        public String navFellesregister = "2020.08.27-09.53-183ead3d81eb";

        public String springBoot = "3.5.10";
        public String springSession = "3.5.4";
        public String springCloud = "2025.0.0";

        public String gcpSecretManager = "5.10.0";

        public String apacheAvro = "1.12.1";
        public String apacheKafka = "4.0.0";
        public String apachePoi = "5.4.1";
        public String assertj = "3.27.3";
        public String avroSerializer = "7.9.1";
        public String bouncyCastle = "1.80";
        public String expressly = "5.0.0";
        public String guava = "33.4.8-jre";
        public String grpc = "1.76.0";
        public String jackson = "2.19.0";
        public String jakartaActivation = "2.1.3";
        public String jakartaValidation = "3.1.0";
        public String jakartaXmlBindApi = "4.0.2";
        public String javaxActivation = "1.1.1";
        public String javaxAnnotation = "1.3.2";
        public String jaxb = "4.0.5";
        public String jaxws = "4.0.3";
        public String junit = "5.12.2";
        public String json = "20250517";
        public String jweaver = "1.9.22.1";
        public String jwt = "4.5.0";
        public String logback = "8.1";
        public String mq = "3.4.3";
        public String okhttp = "4.12.0";
        public String opensearch = "1.8.2";
        public String opensearchClient = "2.26.0";
        public String orika = "1.5.4";
        public String reactorTest = "3.7.6";
        public String springdoc = "2.8.14";
        public String swagger = "2.2.40";
    }

}



