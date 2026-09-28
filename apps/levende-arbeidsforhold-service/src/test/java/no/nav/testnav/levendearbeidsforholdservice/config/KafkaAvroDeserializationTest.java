package no.nav.testnav.levendearbeidsforholdservice.config;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import no.nav.person.pdl.leesah.Endringstype;
import no.nav.person.pdl.leesah.Personhendelse;
import no.nav.person.pdl.leesah.navn.Navn;
import no.nav.person.pdl.leesah.navn.OriginaltNavn;
import org.apache.avro.util.ClassUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KafkaAvroDeserializationTest {

    @Test
    void shouldDeserializeDeathEvent() {
        var event = personhendelse("DOEDSFALL_V1").build();

        assertThat(deserialize(event)).isEqualTo(event);
    }

    @Test
    void shouldDeserializeNestedLeesahTypes() {
        var event = personhendelse("NAVN_V1")
                .setNavn(Navn.newBuilder()
                        .setFornavn("Syntetisk")
                        .setEtternavn("Testperson")
                        .setOriginaltNavn(OriginaltNavn.newBuilder()
                                .setFornavn("Opprinnelig")
                                .build())
                        .build())
                .build();

        assertThat(deserialize(event)).isEqualTo(event);
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

        assertThat(trustedPackages).isEqualTo("no.nav.person.pdl.leesah");
        assertThat(Files.readString(Path.of("Dockerfile")))
                .contains("\"-Dorg.apache.avro.SERIALIZABLE_PACKAGES=" + trustedPackages + "\", \"-jar\"");
    }

    private Personhendelse.Builder personhendelse(String opplysningstype) {
        return Personhendelse.newBuilder()
                .setHendelseId("syntetisk-hendelse")
                .setPersonidenter(List.of())
                .setMaster("PDL")
                .setOpprettet(Instant.parse("2026-09-22T10:00:00Z"))
                .setOpplysningstype(opplysningstype)
                .setEndringstype(Endringstype.OPPRETTET);
    }

    private Object deserialize(Personhendelse event) {
        var schemaRegistry = new MockSchemaRegistryClient();
        var properties = Map.<String, Object>of(
                AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://leesah",
                KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        try (var serializer = new KafkaAvroSerializer(schemaRegistry);
             var deserializer = new KafkaAvroDeserializer(schemaRegistry)) {
            serializer.configure(properties, false);
            deserializer.configure(properties, false);

            var payload = serializer.serialize("pdl.leesah-v1", event);
            return deserializer.deserialize("pdl.leesah-v1", payload);
        }
    }
}
