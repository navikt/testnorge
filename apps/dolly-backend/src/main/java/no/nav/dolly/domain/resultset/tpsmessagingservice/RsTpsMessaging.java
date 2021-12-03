package no.nav.dolly.domain.resultset.tpsmessagingservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto.RsNorskBankkonto;
import no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto.RsUtenlandskBankkonto;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsTpsMessaging {

    private String spraakkode;
    private LocalDate egenAnsattDatoFom;
    private LocalDate egenAnsattDatoTom;
    private RsUtenlandskBankkonto utenlandskBankkonto;
    private RsNorskBankkonto norskBankkonto;
}