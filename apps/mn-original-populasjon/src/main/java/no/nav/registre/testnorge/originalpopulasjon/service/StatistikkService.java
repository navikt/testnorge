package no.nav.registre.testnorge.originalpopulasjon.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import no.nav.testnav.libs.dto.statistikkservice.v1.StatistikkType;
import no.nav.registre.testnorge.originalpopulasjon.consumer.StatistikkConsumer;
import no.nav.registre.testnorge.originalpopulasjon.domain.Alderskategori;
import no.nav.registre.testnorge.originalpopulasjon.domain.Aldersspenn;

@Service
@AllArgsConstructor
public class StatistikkService {

    private final StatistikkConsumer statistikkConsumer;

    public List<Alderskategori> getAlderskategorier(Integer antall) {
        List<Alderskategori> liste = new ArrayList<>();

        Integer antallBarn = finnAntallAvKategori(antall, Statistikk.BARN);
        Integer antallEldre = finnAntallAvKategori(antall, Statistikk.ELDRE);
        Integer antallVoksne = antall - (antallBarn + antallEldre);

        liste.add(new Alderskategori(
                antallBarn,
                new Aldersspenn(Statistikk.BARN_MIN_ALDER,
                        Statistikk.BARN_MAX_ALDER)
        ));
        liste.add(new Alderskategori(
                antallEldre,
                new Aldersspenn(Statistikk.ELDRE_MIN_ALDER,
                        Statistikk.ELDRE_MAX_ALDER)
        ));

        liste.add(new Alderskategori(
                antallVoksne,
                new Aldersspenn(Statistikk.VOKSEN_MIN_ALDER,
                        Statistikk.VOKSEN_MAX_ALDER)
        ));
        return liste;
    }

    private Integer finnAntallAvKategori(Integer antall, StatistikkType statistikkType) {
        double v = antall * statistikkConsumer.getStatistikk(statistikkType).getValue();
        return (int) Math.round(v);
    }
}
