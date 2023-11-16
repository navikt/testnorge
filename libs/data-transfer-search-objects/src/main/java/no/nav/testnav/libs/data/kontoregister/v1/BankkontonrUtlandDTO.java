package no.nav.testnav.libs.data.kontoregister.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankkontonrUtlandDTO {

    @With
    private String kontonummer;
    private String swift;
    private String iban;
    private String banknavn;
    private String valuta;
    private String bankAdresse1;
    private String bankAdresse2;
    private String bankAdresse3;
    private String landkode;
    private Boolean tilfeldigKontonummer;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime kontoRegdato;
}
