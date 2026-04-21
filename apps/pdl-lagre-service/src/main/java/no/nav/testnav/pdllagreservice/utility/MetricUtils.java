package no.nav.testnav.pdllagreservice.utility;

import io.micrometer.core.instrument.Tag;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MetricUtils {
    public static final String KAFKA_CONSUMER_TIMED = "kafka_consumer_record_timed";
    public static final String KEY = "key";

    public static Tag type(Object val) {
        return type(val.getClass().getSimpleName());
    }

    public static Tag type(String type) {
        return Tag.of("type", type);

    }
}
