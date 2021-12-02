package no.nav.dolly.domain.resultset.tpsmessagingservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto.RsNorskBankkonto;
import no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto.RsUtenlandskBankkonto;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsTpsMessaging {

    private RsUtenlandskBankkonto utenlandskBankkonto;
    private RsNorskBankkonto norskBankkonto;
}