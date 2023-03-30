package no.nav.dolly.bestilling.histark.mapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.histark.domain.HistarkRequest;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.histark.RsHistark;
import no.nav.dolly.mapper.MappingStrategy;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

import static io.micrometer.common.util.StringUtils.isBlank;
import static no.nav.dolly.bestilling.dokarkiv.mapper.PdfVedlegg.PDF_VEDLEGG;

@Slf4j
@Component
public class HistarkMappingStrategy implements MappingStrategy {

    private static final String PERSON_BOLK = "personBolk";
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsHistark.class, HistarkRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsHistark histark, HistarkRequest histarkRequest, MappingContext context) {

                        histark.getDokumenter().forEach(dokument -> {

                            String fysiskDokument = isBlank(dokument.getFysiskDokument()) ? PDF_VEDLEGG : dokument.getFysiskDokument();

                            histarkRequest.getHistarkDokumenter().add(HistarkRequest.HistarkDokument.builder()
                                    .file(fysiskDokument)
                                    .metadata(HistarkRequest.HistarkDokument.HistarkMetadata.builder()
                                            .antallSider(String.valueOf(dokument.getAntallSider()))
                                            .brukerident(((PdlPersonBolk.PersonBolk) context.getProperty(PERSON_BOLK)).getIdent())
                                            .enhetsnavn(dokument.getEnhetsnavn())
                                            .enhetsnummer(dokument.getEnhetsnummer())
                                            .filnavn(dokument.getTittel())
                                            .skanner(dokument.getSkanner())
                                            .skannested(dokument.getSkannested())
                                            .klage("")
                                            .sjekksum(DigestUtils.sha256Hex(fysiskDokument))
                                            .skanningstidspunkt(dokument.getScanningsTidspunkt().format(dateTimeFormatter))
                                            .startAar(String.valueOf(dokument.getStartAar().getYear()))
                                            .sluttAar(String.valueOf(dokument.getSluttAar().getYear()))
                                            .temakoder(String.join(",", dokument.getTemakoder()))
                                            .build())
                                    .build());
                        });
                    }
                })
                .register();
    }
}