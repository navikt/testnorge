package no.nav.dolly.bestilling.histark.mapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.histark.domain.HistarkRequest;
import no.nav.dolly.domain.resultset.histark.RsHistark;
import no.nav.dolly.mapper.MappingStrategy;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;

import static io.micrometer.common.util.StringUtils.isBlank;
import static no.nav.dolly.bestilling.dokarkiv.mapper.PdfVedlegg.PDF_VEDLEGG;

@Slf4j
@Component
public class HistarkMappingStrategy implements MappingStrategy {

    private static final String PERSON_IDENT = "personIdent";
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsHistark.class, HistarkRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsHistark histark, HistarkRequest histarkRequest, MappingContext context) {


                        histark.getDokumenter().forEach(dokument -> {


                            String fysiskDokument = isBlank(dokument.getFysiskDokument()) ? PDF_VEDLEGG : dokument.getFysiskDokument();

                            try {
                                histarkRequest.getHistarkDokumenter().add(HistarkRequest.HistarkDokument.builder()
                                        .file(fysiskDokument)
                                        .metadata(HistarkRequest.HistarkDokument.HistarkMetadata.builder()
                                                .antallSider(String.valueOf(dokument.getAntallSider()))
                                                .brukerident(((String) context.getProperty(PERSON_IDENT)))
                                                .enhetsnavn(dokument.getEnhetsnavn())
                                                .enhetsnummer(dokument.getEnhetsnummer())
                                                .filnavn(dokument.getTittel())
                                                .skanner(dokument.getSkanner())
                                                .skannested(dokument.getSkannested())
                                                .klage("")
                                                .sjekksum(calculateBinaryChecksum(fysiskDokument))
                                                .skanningstidspunkt(dokument.getSkanningsTidspunkt().format(dateTimeFormatter))
                                                .startAar(String.valueOf(dokument.getStartAar().getYear()))
                                                .sluttAar(String.valueOf(dokument.getSluttAar().getYear()))
                                                .temakoder(String.join(",", dokument.getTemakoder()))
                                                .build())
                                        .build());
                            } catch (IOException e) {
                                log.error("Klarte ikke å kalkulere sjekksum fra innsendt histark dokument");
                                throw new RuntimeException(e);
                            }
                        });
                    }
                })
                .register();
    }

    private static String calculateBinaryChecksum(String fysiskDokument) throws IOException {
        InputStream byteArrayInput = new ByteArrayInputStream(fysiskDokument.getBytes());
        InputStream base64Input = new Base64InputStream(byteArrayInput);

        return DigestUtils.sha256Hex(base64Input);
    }
}