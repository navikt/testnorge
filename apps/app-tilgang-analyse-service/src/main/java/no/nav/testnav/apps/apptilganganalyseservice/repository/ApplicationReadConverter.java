package no.nav.testnav.apps.apptilganganalyseservice.repository;


import io.r2dbc.spi.Clob;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import reactor.core.publisher.Flux;

import no.nav.testnav.apps.apptilganganalyseservice.repository.entity.ApplicationEntity;
import no.nav.testnav.apps.apptilganganalyseservice.util.TreadUtil;

@ReadingConverter
public class ApplicationReadConverter implements Converter<Row, ApplicationEntity> {
    @Override
    public ApplicationEntity convert(Row row) {

        return ApplicationEntity
                .builder()
                .repo(row.get("REPO", String.class))
                .sha(row.get("SHA", String.class))
                .content(clobToString(row, "CONTENT"))
                .build();
    }

    /**
     * TODO: Bruker en HACK for å hente ut stream av strings fra Flux.
     */
    private static String clobToString(Row row, String name) {
        var publisher = (Flux<CharSequence>) ((Clob) row.get(name)).stream();
        return String.join("", TreadUtil.Instance().resolve(publisher));
    }
}

