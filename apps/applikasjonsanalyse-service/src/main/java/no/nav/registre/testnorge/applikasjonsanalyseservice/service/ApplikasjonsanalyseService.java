package no.nav.registre.testnorge.applikasjonsanalyseservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.applikasjonsanalyseservice.adapter.ApplikasjonsanalyseAdapter;
import no.nav.registre.testnorge.applikasjonsanalyseservice.domain.ApplikasjonsanalyseList;
import no.nav.registre.testnorge.applikasjonsanalyseservice.domain.Properties;

@Service
@RequiredArgsConstructor
public class ApplikasjonsanalyseService {
    private final ApplikasjonsanalyseAdapter adapter;

    public ApplikasjonsanalyseList getApplikasjonsanalyseList(Properties properties) {
        return new ApplikasjonsanalyseList(adapter.search(properties));
    }
}
