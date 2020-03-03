package no.nav.registre.spion.provider.rs.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.spion.domain.Vedtak;
import no.nav.registre.spion.provider.rs.response.SyntetiserVedtakResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class VedtakTilKafkaScheduler {

    @Value("${application.kafka.topic}")
    private String topic;

    private final VedtakPublisher vedtakPublisher;

    @Transactional
    public void execute(List<SyntetiserVedtakResponse> syntetisertvedtaksliste) throws ExecutionException, InterruptedException {
        log.info("Sender vedtak til Kafka topic {}.", topic);

        try{
            for(SyntetiserVedtakResponse response : syntetisertvedtaksliste){
                for(Vedtak vedtak : response.getVedtak()){
                    vedtakPublisher.publish(vedtak);
                }
            }
            log.info("Sending av vedtak til Kafka Topic {} var vellykket.", topic);
        }catch(Exception e){
            log.error("Sending av vedtak til Kafka Topic {} mislyktes.", topic);
            throw e;
        }
    }
}
