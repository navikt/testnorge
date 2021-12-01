package no.nav.dolly.domain.resultset.tpsmessagingservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto.RsNorskBankkonto;
import no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto.RsUtenlandskBankkonto;

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

    private List<RsUtenlandskBankkonto> utenlandskBankkonto;
    private List<RsNorskBankkonto> norskBankkonto;

    public List<RsUtenlandskBankkonto> getUtenlandskBankkonto() {
        if (isNull(utenlandskBankkonto)) {
            utenlandskBankkonto = new ArrayList<>();
        }
        return utenlandskBankkonto;
    }

    public List<RsNorskBankkonto> getNorskBankkonto() {
        if (isNull(norskBankkonto)) {
            norskBankkonto = new ArrayList<>();
        }
        return norskBankkonto;
    }
}