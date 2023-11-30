package no.nav.testnav.libs.data.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeltBostedDTO extends DbVersjonDTO {

    private String adresseIdentifikatorFraMatrikkelen;
    private String coAdressenavn;
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime sluttdatoForKontrakt;
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime startdatoForKontrakt;
    private VegadresseDTO vegadresse;
    private UkjentBostedDTO ukjentBosted;
    private MatrikkeladresseDTO matrikkeladresse;

    @JsonIgnore
    public int countAdresser() {

        return count(getVegadresse()) + count(getMatrikkeladresse()) + count(getUkjentBosted());
    }
}
