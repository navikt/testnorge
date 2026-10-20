package no.nav.registre.testnorge.organisasjonmottak.config;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import no.nav.testnav.libs.avro.organisasjon.v1.DetaljertNavn;
import no.nav.testnav.libs.avro.organisasjon.v1.Endringsdokument;
import no.nav.testnav.libs.avro.organisasjon.v1.Metadata;
import no.nav.testnav.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.testnav.libs.avro.organisasjon.v1.Organisasjon;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.util.ClassUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KafkaAvroDeserializationTest {

    @Test
    void shouldDeserializeOpprettelsesdokument() {
        var dokument = Opprettelsesdokument.newBuilder()
                .setOrganisasjon(organisasjon())
                .setMetadata(metadata())
                .build();

        assertThat(deserialize("dolly.testnav-opprett-organisasjon-v1", dokument)).isEqualTo(dokument);
    }

    @Test
    void shouldDeserializeEndringsdokument() {
        var dokument = Endringsdokument.newBuilder()
                .setOrganisasjon(organisasjon())
                .setMetadata(metadata())
                .build();

        assertThat(deserialize("dolly.testnav-endre-organisasjon-v1", dokument)).isEqualTo(dokument);
    }

    @Test
    void shouldRejectClassesOutsideTrustedPackages() {
        assertThatThrownBy(() -> ClassUtils.forName(KafkaConfig.class.getName()))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Forbidden " + KafkaConfig.class.getName());
    }

    @Test
    void shouldUseSameTrustedPackagesInContainer() throws IOException {
        var trustedPackages = System.getProperty("org.apache.avro.SERIALIZABLE_PACKAGES");

        assertThat(trustedPackages).isEqualTo("no.nav.testnav.libs.avro.organisasjon.v1");
        assertThat(Files.readString(Path.of("Dockerfile")))
                .contains("\"-Dorg.apache.avro.SERIALIZABLE_PACKAGES=" + trustedPackages + "\", \"-jar\"");
    }

    private Organisasjon organisasjon() {
        var underenhet = Organisasjon.newBuilder()
                .setOrgnummer("987654321")
                .setEnhetstype("BEDR")
                .setUnderenheter(List.of())
                .build();

        return Organisasjon.newBuilder()
                .setOrgnummer("123456789")
                .setEnhetstype("AS")
                .setNavn(DetaljertNavn.newBuilder()
                        .setNavn1("Syntetisk organisasjon")
                        .build())
                .setUnderenheter(List.of(underenhet))
                .build();
    }

    private Metadata metadata() {
        return Metadata.newBuilder()
                .setMiljo("q1")
                .build();
    }

    private Object deserialize(String topic, SpecificRecord dokument) {
        var schemaRegistry = new MockSchemaRegistryClient();
        var properties = Map.<String, Object>of(
                AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://organisasjon",
                KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        try (var serializer = new KafkaAvroSerializer(schemaRegistry);
             var deserializer = new KafkaAvroDeserializer(schemaRegistry)) {
            serializer.configure(properties, false);
            deserializer.configure(properties, false);

            var payload = serializer.serialize(topic, dokument);
            return deserializer.deserialize(topic, payload);
        }
    }
}
