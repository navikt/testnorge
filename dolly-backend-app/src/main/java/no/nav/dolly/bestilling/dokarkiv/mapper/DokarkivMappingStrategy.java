package no.nav.dolly.bestilling.dokarkiv.mapper;

import static java.util.Objects.isNull;
import static no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest.IdType.FNR;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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

    private static final String KANAL = "SCAN_IM";
    private static final String PDFA = "PDFA";
    private static final String ARKIV = "ARKIV";

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsDokarkiv.class, DokarkivRequest.class)
                .customize(new CustomMapper<RsDokarkiv, DokarkivRequest>() {
                    @Override
                    public void mapAtoB(RsDokarkiv dokarkiv,
                            DokarkivRequest dokarkivRequest, MappingContext context) {
                        boolean dokarkivIsSet = true;

                        if (isBlank(dokarkiv.getKanal())) {
                            dokarkivRequest.setKanal(KANAL);
                        }
                        if (isNull(dokarkiv.getDokumenter())) {
                            dokarkivIsSet = false;
                            List<DokarkivRequest.Dokument> dokumenter = new ArrayList<>();
                            dokarkivRequest.setDokumenter(dokumenter);
                            List<DokarkivRequest.DokumentVariant> dokumentVarianter = new ArrayList<>();
                            dokumentVarianter.add(new DokarkivRequest.DokumentVariant());
                            dokarkivRequest.getDokumenter().get(0).setDokumentvarianter(dokumentVarianter);
                        } else if (isNull(dokarkiv.getDokumenter().get(0).getDokumentvarianter())) {
                            dokarkivIsSet = false;
                            List<DokarkivRequest.DokumentVariant> dokumentVarianter = new ArrayList<>();
                            dokumentVarianter.add(new DokarkivRequest.DokumentVariant());
                            dokarkivRequest.getDokumenter().get(0).setDokumentvarianter(dokumentVarianter);
                        }
                        if (!dokarkivIsSet || isBlank(dokarkiv.getDokumenter().get(0).getDokumentvarianter().get(0).getFysiskDokument())) {
                            Path pdfPath = Paths.get("dolly-backend-app/src/main/resources/dokarkiv/testpdf.pdf");
                            try {
                                byte[] pdfByteArray = Files.readAllBytes(pdfPath);
                                dokarkivRequest.getDokumenter().get(0).getDokumentvarianter().get(0).setFiltype(PDFA);
                                dokarkivRequest.getDokumenter().get(0).getDokumentvarianter().get(0).setFysiskDokument(Base64.getEncoder().encodeToString(pdfByteArray));
                                dokarkivRequest.getDokumenter().get(0).getDokumentvarianter().get(0).setVariantformat(ARKIV);
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