package no.nav.dolly.bestilling.dokarkiv.mapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static java.util.Objects.isNull;
import static no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest.IdType.FNR;
import static no.nav.dolly.bestilling.dokarkiv.mapper.PdfVedlegg.PDF_VEDLEGG;
import static no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv.JournalPostType.INNGAAENDE;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Component
public class DokarkivMappingStrategy implements MappingStrategy {

    private static final String KANAL = "SKAN_IM";
    private static final String PDFA = "PDFA";
    private static final String ARKIV = "ARKIV";
    private static final String BEHANDLINGSTEMA = "ab0001";
    private static final String FAGSAK_ID = "10695768";
    private static final String FAGSAK_SYSTEM = "AO01";
    private static final String FAGSAK_TYPE = "FAGSAK";

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsDokarkiv.class, DokarkivRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsDokarkiv dokarkiv, DokarkivRequest dokarkivRequest, MappingContext context) {

                        dokarkivRequest.setKanal(isBlank(dokarkiv.getKanal()) ? KANAL : dokarkiv.getKanal());
                        dokarkivRequest.setJournalpostType(isNull(dokarkiv.getJournalpostType()) ? INNGAAENDE : dokarkiv.getJournalpostType());
                        dokarkivRequest.setBehandlingstema(isNull(dokarkiv.getBehandlingstema()) ? BEHANDLINGSTEMA : dokarkiv.getBehandlingstema());
                        if (isNull(dokarkiv.getAvsenderMottaker())
                                || isBlank(dokarkiv.getAvsenderMottaker().getId())
                                || Arrays.stream(RsDokarkiv.IdType.values())
                                .noneMatch(type -> type.equals(dokarkiv.getAvsenderMottaker().getIdType()))) {
                            dokarkivRequest.setAvsenderMottaker(DokarkivRequest.AvsenderMottaker.builder()
                                    .idType(FNR)
                                    .build());
                        }
                        dokarkivRequest.setSak(DokarkivRequest.Sak.builder()
                                .fagsakId(FAGSAK_ID)
                                .fagsaksystem(FAGSAK_SYSTEM)
                                .sakstype(FAGSAK_TYPE)
                                .build());
                        dokarkivRequest.setBruker(DokarkivRequest.Bruker.builder()
                                .idType(FNR)
                                .build());
                        fyllDokarkivDokument(dokarkivRequest);
                    }
                })
                .byDefault()
                .register();
    }

    private void fyllDokarkivDokument(DokarkivRequest dokarkivRequest) {

        if (dokarkivRequest.getDokumenter().isEmpty()) {
            dokarkivRequest.getDokumenter().add(new DokarkivRequest.Dokument());
        }
        if (dokarkivRequest.getDokumenter().get(0).getDokumentvarianter().isEmpty()) {
            dokarkivRequest.getDokumenter().get(0).getDokumentvarianter().add(new DokarkivRequest.DokumentVariant());
        }
        if (isBlank(dokarkivRequest.getDokumenter().get(0).getDokumentvarianter().get(0).getFiltype())) {
            dokarkivRequest.getDokumenter().get(0).getDokumentvarianter().get(0).setFiltype(PDFA);
        }
        if (isBlank(dokarkivRequest.getDokumenter().get(0).getDokumentvarianter().get(0).getVariantformat())) {
            dokarkivRequest.getDokumenter().get(0).getDokumentvarianter().get(0).setVariantformat(ARKIV);
        }
        if (isBlank(dokarkivRequest.getDokumenter().get(0).getDokumentvarianter().get(0).getFysiskDokument())) {
            dokarkivRequest.getDokumenter().get(0).getDokumentvarianter().get(0).setFysiskDokument(PDF_VEDLEGG);
        }
    }
}