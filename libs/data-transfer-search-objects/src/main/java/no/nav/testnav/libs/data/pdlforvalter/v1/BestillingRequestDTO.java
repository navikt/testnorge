package no.nav.testnav.libs.data.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BestillingRequestDTO {

    private Identtype identtype;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime foedtEtter;
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime foedtFoer;
    private Integer alder;

    private Boolean syntetisk;

    private String opprettFraIdent;

    private PersonDTO person;

    @JsonIgnore
    public boolean hasAlder() {

        return nonNull(alder) || nonNull(foedtEtter) || nonNull(foedtFoer);
    }
}