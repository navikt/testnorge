package no.nav.dolly.bestilling.dokarkiv.mapper;

import static java.util.Objects.isNull;
import static no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest.IdType.FNR;
import static no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv.JournalPostType.INNGAAENDE;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.mapper.MappingStrategy;

@Slf4j
@Component
public class DokarkivMappingStrategy implements MappingStrategy {

    private static final String KANAL = "SKAN_IM";
    private static final String PDFA = "PDFA";
    private static final String ARKIV = "ARKIV";

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsDokarkiv.class, DokarkivRequest.class)
                .customize(new CustomMapper<RsDokarkiv, DokarkivRequest>() {
                    @Override
                    public void mapAtoB(RsDokarkiv dokarkiv, DokarkivRequest dokarkivRequest, MappingContext context) {

                        dokarkivRequest.setKanal(isBlank(dokarkiv.getKanal()) ? KANAL : dokarkiv.getKanal());
                        dokarkivRequest.setJournalpostType(isNull(dokarkiv.getJournalpostType()) ? INNGAAENDE : dokarkiv.getJournalpostType());
                        if (isBlank(dokarkiv.getDokumenter().get(0).getDokumentvarianter().get(0).getFiltype())) {
                            dokarkivRequest.getDokumenter().get(0).getDokumentvarianter().get(0).setFiltype(PDFA);
                        }
                        if (isBlank(dokarkiv.getDokumenter().get(0).getDokumentvarianter().get(0).getVariantformat())) {
                            dokarkivRequest.getDokumenter().get(0).getDokumentvarianter().get(0).setVariantformat(ARKIV);
                        }
                        if (isBlank(dokarkiv.getDokumenter().get(0).getDokumentvarianter().get(0).getFysiskDokument())) {
                            Path pdfPath = Paths.get("dolly-backend-app/src/main/resources/dokarkiv/testpdf.pdf");
                            try {
                                byte[] pdfByteArray = Files.readAllBytes(pdfPath);
                                dokarkivRequest.getDokumenter().get(0).getDokumentvarianter().get(0).setFysiskDokument(Base64.getEncoder().encodeToString(pdfByteArray));
                            } catch (IOException e) {
                                log.error("Klarte ikke å hente test PDF: ", e);
                            }
                        }
                        dokarkivRequest.setBruker(DokarkivRequest.Bruker.builder()
                                .idType(FNR)
                                .build());
                    }
                })
                .byDefault()
                .register();
    }
}