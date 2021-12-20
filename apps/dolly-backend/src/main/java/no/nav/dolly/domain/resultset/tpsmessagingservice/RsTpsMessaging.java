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
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonnummerDTO;

import java.time.LocalDate;
import java.util.List;

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
    private List<TelefonnummerDTO> telefonnummer;
}