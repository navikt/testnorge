package no.nav.dolly.domain.resultset.pdldata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.data.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlPersondata {

    private PdlPerson opprettNyPerson;
    private PersonDTO person;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PdlPerson {

        private Identtype identtype;

        @Field(type=FieldType.Date, format=DateFormat.date_hour_minute_second, pattern="uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime foedtEtter;
        @Field(type=FieldType.Date, format=DateFormat.date_hour_minute_second, pattern="uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime foedtFoer;
        private Integer alder;
        private Boolean syntetisk;
        private Boolean id2032;
    }
}
