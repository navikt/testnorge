package no.nav.testnav.apps.apptilganganalyseservice.repository;


import io.r2dbc.spi.Clob;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import reactor.core.publisher.Flux;

import no.nav.testnav.apps.apptilganganalyseservice.domain.DocumentType;
import no.nav.testnav.apps.apptilganganalyseservice.repository.entity.DocumentEntity;
import no.nav.testnav.apps.apptilganganalyseservice.util.TreadUtil;

@ReadingConverter
public class DocumentReadConverter implements Converter<Row, DocumentEntity> {
    @Override
    public DocumentEntity convert(Row row) {

        return DocumentEntity
                .builder()
                .repo(row.get("REPO", String.class))
                .owner(row.get("OWNER", String.class))
                .sha(row.get("SHA", String.class))
                .path(row.get("PATH", String.class))
                .type(DocumentType.valueOf(row.get("TYPE", String.class)))
                .content(clobToString(row, "CONTENT"))
                .build();
    }

    /**
     * TODO: Bruker en HACK for å hente ut stream av strings fra Flux.
     */
    private static String clobToString(Row row, String name) {
        if (row.get(name) instanceof String) {
            return (String) row.get(name);
        }
        var publisher = (Flux<CharSequence>) ((Clob) row.get(name)).stream();
        return String.join("", TreadUtil.Instance().resolve(publisher));
    }
}