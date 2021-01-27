package no.nav.dolly.bestilling.organisasjonforvalter.domain;

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
public class OrganisasjonDetaljer {

    private Long id;
    private String organisasjonsnummer;
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

    private List<OrganisasjonAdresse> adresser;
    private List<OrganisasjonDetaljer> underenheter;

    public List<OrganisasjonAdresse> getAdresser() {
        if (isNull(adresser)) {
            adresser = new ArrayList<>();
        }
        return adresser;
    }

    public List<OrganisasjonDetaljer> getUnderenheter() {
        if (isNull(underenheter)) {
            underenheter = new ArrayList<>();
        }
        return underenheter;
    }
}
