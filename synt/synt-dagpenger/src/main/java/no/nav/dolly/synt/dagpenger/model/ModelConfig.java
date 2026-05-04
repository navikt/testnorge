package no.nav.dolly.synt.dagpenger.model;

import no.nav.dolly.synt.dagpenger.onnx.DagpengerGeneratorBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
class ModelConfig {

    @Value("${app.config.bucket:}")
    private String bucket;

    @Bean
    @Profile("!local")
    ModelService bucketService(DagpengerGeneratorBean dagpengerGeneratorBean) {
        return new GoogleModelService(bucket, dagpengerGeneratorBean);
    }

    @Bean
    @Profile("local")
    ModelService localModelService(DagpengerGeneratorBean dagpengerGeneratorBean) {
        return new LocalModelService(dagpengerGeneratorBean);
    }


}
