package no.nav.organisasjonforvalter.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsOrganisasjon {

    private Long id;
    private String organisasjonsnummer;
    private String juridiskEnhet;
    private String enhetstype;
    private String naeringskode;
    private String sektorkode;
    private String formaal;
    private String organisasjonsnavn;
    private LocalDate stiftelsesdato;
    private String telefon;
    private String epost;
    private String nettside;
    private String maalform;

    private List<RsAdresse> adresser;
    private List<RsOrganisasjon> underenheter;

    public List<RsAdresse> getAdresser() {
        if (isNull(adresser)) {
            adresser = new ArrayList<>();
        }
        return adresser;
    }

    public List<RsOrganisasjon> getUnderenheter() {
        if (isNull(underenheter)) {
            underenheter = new ArrayList<>();
        }
        return underenheter;
    }
}
