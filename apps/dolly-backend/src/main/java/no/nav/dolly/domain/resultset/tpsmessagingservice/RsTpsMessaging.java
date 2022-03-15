package no.nav.dolly.domain.resultset.tpsmessagingservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonTypeNummerDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsTpsMessaging {

    private String spraakKode;
    private LocalDate egenAnsattDatoFom;
    private LocalDate egenAnsattDatoTom;
    private BankkontonrUtlandDTO utenlandskBankkonto;
    private List<SikkerhetstiltakDTO> sikkerhetstiltak;
    private BankkontonrNorskDTO norskBankkonto;
    private List<TelefonTypeNummerDTO> telefonnummer;

    public List<SikkerhetstiltakDTO> getSikkerhetstiltak() {
        if (isNull(sikkerhetstiltak)) {
            sikkerhetstiltak = new ArrayList<>();
        }
        return sikkerhetstiltak;
    }

    public List<TelefonTypeNummerDTO> getTelefonnummer() {
        if (isNull(telefonnummer)) {
            telefonnummer = new ArrayList<>();
        }
        return telefonnummer;
    }
}