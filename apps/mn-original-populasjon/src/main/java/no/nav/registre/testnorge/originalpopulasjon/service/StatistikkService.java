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
                Statistikk.BARN_MIN_ALDER,
                Statistikk.BARN_MAX_ALDER,
                antallBarn
        ));
        liste.add(new Alderskategori(
                Statistikk.ELDRE_MIN_ALDER,
                Statistikk.ELDRE_MAX_ALDER,
                antallEldre
        ));

        liste.add(new Alderskategori(
                Statistikk.VOKSEN_MIN_ALDER,
                Statistikk.VOKSEN_MAX_ALDER,
                antallVoksne
        ));
        return liste;
    }

    private Integer finnAntallAvKategori(Integer antall, StatistikkType statistikkType) {
        double v = antall * statistikkConsumer.getStatistikk(statistikkType).getValue();
        return (int) Math.round(v);
    }
}
