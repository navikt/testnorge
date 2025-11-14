package no.nav.testnav.pdllagreservice.utility;

import io.micrometer.core.instrument.Tag;
import lombok.experimental.UtilityClass;

import java.util.List;

import static java.util.Arrays.asList;

@UtilityClass
public class MetricUtils {
    public static final String KAFKA_PRODUCER_TIMED = "kafka_producer_publish_timed";
    public static final String KAFKA_CONSUMER_TIMED = "kafka_consumer_record_timed";
    public static final String KAFKA_CONSUMED_RECORD_TYPE_COUNT = "kafka_consumer_record_type_count";
    public static final String SCHEDULED_TIMED = "scheduled_job_timed";
    public static final String DB_QUERY_TIMED = "db_query_timed";
    public static final String JMS_TIMED = "jms_timed";
    public static final String KEY = "key";
    public static final String QUERY = "query";

    static final String EXCEPTION = "exception";

    public static Tag exception(Exception e) {
        return Tag.of(EXCEPTION, e.getClass().getSimpleName());
    }

    public static Tag exceptionNone() {
        return Tag.of(EXCEPTION, "None");
    }

    public static Tag type(Object val) {
        return type(val.getClass().getSimpleName());
    }

    public static Tag type(String type) {
        return Tag.of("type", type);
    }

    public static Tag operation(String operation) {
        return Tag.of("operation", operation);
    }

    public static Tag topic(String type) {
        return Tag.of("topic", type);
    }

    public static List<Tag> allOf(Tag... tags) {
        return asList(tags);
    }
}
