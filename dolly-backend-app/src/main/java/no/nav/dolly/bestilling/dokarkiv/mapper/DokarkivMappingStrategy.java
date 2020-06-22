package no.nav.dolly.bestilling.dokarkiv.mapper;

import static no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest.IdType.FNR;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class DokarkivMappingStrategy implements MappingStrategy {

    private static final String KANAL = "SCAN_IM";

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsDokarkiv.class, DokarkivRequest.class)
                .customize(new CustomMapper<RsDokarkiv, DokarkivRequest>() {
                    @Override
                    public void mapAtoB(RsDokarkiv dokarkiv,
                                DokarkivRequest dokarkivRequest, MappingContext context) {

                        if (isBlank(dokarkiv.getKanal())) {
                            dokarkivRequest.setKanal(KANAL);
                        }
                        if (isBlank(dokarkiv.getDokumenter().getDokumentVarianter().getFysiskDokument())) {
                            Path pdfPath = Paths.get("dolly-backend-app/src/main/resources/dokarkiv/testpdf.pdf");
                            try {
                                dokarkivRequest.getDokumenter().getDokumentVarianter().setFysiskDokument(Arrays.toString(Files.readAllBytes(pdfPath)));
                            } catch (IOException e) {
                                e.printStackTrace();
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