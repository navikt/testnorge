package no.nav.testnav.libs.data.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TelefonTypeNummerDTO {

    public enum TypeTelefon {ARBT, HJET, MOBI}

    private String telefonnummer;
    private String landkode;
    private TypeTelefon telefontype;
}
