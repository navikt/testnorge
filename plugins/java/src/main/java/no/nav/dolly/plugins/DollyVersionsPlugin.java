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

        public String springBoot = "4.1.2";
        public String springSessionBom = "4.1.1";
        public String springCloud = "2025.1.4";

        public String gcpSecretManager = "8.3.0";
        public String gcpStorage = "2.75.0";

        public String apacheAvro = "1.12.2";
        public String apacheKafka = "4.4.0";
        public String apachePoi = "6.0.0";
        public String assertj = "3.27.7";
        public String avroSerializer = "8.4.0";
        public String bouncyCastle = "1.86";
        public String expressly = "6.0.0";
        public String guava = "33.7.2-jre";
        public String jakartaActivation = "2.1.4";
        public String jakartaValidation = "3.1.1";
        public String jakartaXmlBindApi = "4.0.5";
        public String jaxb = "4.0.9";
        public String jaxws = "4.0.5";
        public String junit = "6.1.3";
        public String json = "20260814";
        public String jweaver = "1.9.26";
        public String jwt = "4.6.2";
        public String logback = "9.0";
        public String mq = "4.1.2";
        public String okhttp = "5.5.1";
        public String onnxruntime = "1.31.1";
        public String opensearchClient = "3.10.0";
        public String orika = "1.5.4";
        public String reactorTest = "3.8.8";
        public String springdoc = "3.2.0";
        public String swagger = "2.2.55";
        public String testcontainers = "2.0.5";
        public String wiremock = "3.13.2";
    }
}



