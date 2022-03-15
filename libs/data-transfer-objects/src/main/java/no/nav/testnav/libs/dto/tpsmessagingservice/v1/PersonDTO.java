package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PersonDTO {

    private String ident;

    private String identtype;

    private String kjonn;

    private String fornavn;

    private String mellomnavn;

    private String etternavn;

    private String forkortetNavn;

    private StatsborgerskapDTO statsborgerskap;

    private String spesreg;

    private LocalDateTime spesregDato;

    private LocalDateTime doedsdato;

    private SivilstandDTO sivilstand;

    private LocalDateTime egenAnsattDatoFom;

    private LocalDateTime egenAnsattDatoTom;

    private SikkerhetstiltakDTO sikkerhetstiltak;

    private AdresseDTO boadresse;

    private PostadresseDTO postadresse;

    private LocalDateTime regdato;

    private List<RelasjonDTO> relasjoner;

    private MidlertidigAdresseDTO midlertidigAdresse;

    private LocalDateTime opprettetDato;

    private String opprettetAv;

    private String sprakKode;

    private LocalDateTime datoSprak;

    private String tknr;

    private String tknavn;

    private String gtType;

    private String gtVerdi;

    private String gtRegel;

    private Boolean utenFastBopel;

    private String personStatus;

    private LocalDateTime forsvunnetDato;

    private String importFra;

    private List<TelefonTypeNummerDTO> telefonnumre;

    private BankkontonrUtlandDTO bankkontonrUtland;
    private BankkontonrNorskDTO bankkontonrNorsk;

    public List<RelasjonDTO> getRelasjoner() {
        if (isNull(relasjoner)) {
            relasjoner = new ArrayList<>();
        }
        return relasjoner;
    }

    public List<TelefonTypeNummerDTO> getTelefonnumre() {
        if (isNull(telefonnumre)) {
            telefonnumre = new ArrayList<>();
        }
        return telefonnumre;
    }
}