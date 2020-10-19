package no.nav.registre.testnorge.originalpopulasjon.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.testnorge.libs.dto.statistikk.v1.StatistikkType;
import no.nav.registre.testnorge.originalpopulasjon.consumer.StatistikkConsumer;
import no.nav.registre.testnorge.originalpopulasjon.domain.Alderskategori;

@Service
@AllArgsConstructor
public class StatistikkService {

    private final StatistikkConsumer statistikkConsumer;

    public List<Alderskategori> getAlderskategorier (Integer antall) {
        List<Alderskategori> liste = new ArrayList<>();

        Integer antallBarn = finnAntallAvKategori(antall, Statistikk.BARN);
        Integer antallEldre = finnAntallAvKategori(antall, Statistikk.ELDRE);
        Integer antallVoksne = antall - (antallBarn + antallEldre);

        liste.add(new Alderskategori(
                Statistikk.barn_min_alder,
                Statistikk.barn_max_alder,
                antallBarn
        ));
        liste.add(new Alderskategori(
                Statistikk.eldre_min_alder,
                Statistikk.eldre_max_alder,
                antallEldre
        ));

        liste.add(new Alderskategori(
                Statistikk.voksen_min_alder,
                Statistikk.voksen_max_alder,
                antallVoksne
        ));
        return liste;
    }

    private Integer finnAntallAvKategori(Integer antall, StatistikkType statistikkType) {
        double v = antall * statistikkConsumer.getStatistikk(statistikkType).getValue();
        return (int) Math.round(v);
    }
}
