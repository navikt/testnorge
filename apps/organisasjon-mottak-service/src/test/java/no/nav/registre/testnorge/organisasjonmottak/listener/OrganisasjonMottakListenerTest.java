package no.nav.registre.testnorge.organisasjonmottak.listener;

import no.nav.registre.testnorge.organisasjonmottak.consumer.EregDollyEnheterConsumer;
import no.nav.registre.testnorge.organisasjonmottak.domain.Flatfil;
import no.nav.testnav.libs.avro.organisasjon.v1.Endringsdokument;
import no.nav.testnav.libs.avro.organisasjon.v1.Metadata;
import no.nav.testnav.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.testnav.libs.avro.organisasjon.v1.Organisasjon;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrganisasjonMottakListenerTest {

    @Test
    void shouldSendOneFlatfilPerMiljoeWithOpprettelserBeforeEndringer() {
        var consumer = mock(EregDollyEnheterConsumer.class);
        when(consumer.send(any(), any())).thenReturn(Mono.empty());

        new OrganisasjonMottakListener(consumer).listen(List.of(
                record(Endringsdokument.newBuilder()
                        .setOrganisasjon(organisasjon("111111111"))
                        .setMetadata(metadata("q1"))
                        .build()),
                record(Opprettelsesdokument.newBuilder()
                        .setOrganisasjon(organisasjon("222222222"))
                        .setMetadata(metadata("q1"))
                        .build()),
                record(Opprettelsesdokument.newBuilder()
                        .setOrganisasjon(organisasjon("333333333"))
                        .setMetadata(metadata("q2"))
                        .build())));

        var q1 = ArgumentCaptor.forClass(Flatfil.class);
        verify(consumer, times(1)).send(q1.capture(), eq("q1"));
        verify(consumer, times(1)).send(any(), eq("q2"));

        var innhold = q1.getValue().build();
        assertThat(innhold.indexOf("ENH 222222222")).isNotNegative()
                .isLessThan(innhold.indexOf("ENH 111111111"));
    }

    private static ConsumerRecord<String, SpecificRecord> record(SpecificRecord value) {
        return new ConsumerRecord<>("topic", 0, 0, "key", value);
    }

    private static Organisasjon organisasjon(String orgnummer) {
        return Organisasjon.newBuilder()
                .setOrgnummer(orgnummer)
                .setEnhetstype("AS")
                .setUnderenheter(List.of())
                .build();
    }

    private static Metadata metadata(String miljo) {
        return Metadata.newBuilder()
                .setMiljo(miljo)
                .build();
    }
}
