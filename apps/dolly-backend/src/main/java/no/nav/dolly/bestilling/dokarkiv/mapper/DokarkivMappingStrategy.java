package no.nav.dolly.bestilling.dokarkiv.mapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

import static java.util.Objects.isNull;
import static no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest.IdType.FNR;
import static no.nav.dolly.bestilling.dokarkiv.mapper.PdfVedlegg.PDF_VEDLEGG;
import static no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv.JournalPostType.INNGAAENDE;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Component
public class DokarkivMappingStrategy implements MappingStrategy {

    private static final String PERSON_BOLK = "personBolk";
    private static final String KANAL = "SKAN_IM";
    private static final String PDFA = "PDFA";
    private static final String ARKIV = "ARKIV";
    private static final String BEHANDLINGSTEMA = "ab0001";

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsDokarkiv.class, DokarkivRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsDokarkiv rsDokarkiv, DokarkivRequest dokarkivRequest, MappingContext context) {


                        dokarkivRequest.setEksternReferanseId(UUID.randomUUID().toString());
                        dokarkivRequest.setTittel(rsDokarkiv.getTittel());
                        dokarkivRequest.setJournalfoerendeEnhet(isBlank(rsDokarkiv.getJournalfoerendeEnhet()) ? null : rsDokarkiv.getJournalfoerendeEnhet());
                        dokarkivRequest.setTema(rsDokarkiv.getTema());

                        dokarkivRequest.setKanal(isBlank(rsDokarkiv.getKanal()) ? KANAL : rsDokarkiv.getKanal());
                        dokarkivRequest.setJournalpostType(isNull(rsDokarkiv.getJournalpostType()) ? INNGAAENDE : rsDokarkiv.getJournalpostType());
                        dokarkivRequest.setBehandlingstema(isNull(rsDokarkiv.getBehandlingstema()) ? BEHANDLINGSTEMA : rsDokarkiv.getBehandlingstema());

                        dokarkivRequest.setAvsenderMottaker(mapperFacade.map(rsDokarkiv.getAvsenderMottaker(),
                                DokarkivRequest.AvsenderMottaker.class));
                        if (isNull(rsDokarkiv.getAvsenderMottaker())
                                || isBlank(rsDokarkiv.getAvsenderMottaker().getId())
                                || Arrays.stream(RsDokarkiv.IdType.values())
                                .noneMatch(type -> type.equals(rsDokarkiv.getAvsenderMottaker().getIdType()))) {
                            dokarkivRequest.setAvsenderMottaker(DokarkivRequest.AvsenderMottaker.builder()
                                    .idType(FNR)
                                    .id(((PdlPersonBolk.PersonBolk) context.getProperty(PERSON_BOLK)).getIdent())
                                    .navn(getNavn((PdlPersonBolk.PersonBolk) context.getProperty(PERSON_BOLK)))
                                    .build());
                        }
                        dokarkivRequest.setSak(mapperFacade.map(rsDokarkiv.getSak(), DokarkivRequest.Sak.class));

                        if (!isNull(dokarkivRequest.getSak()) && (isBlank(dokarkivRequest.getSak().getFagsakId()))) {
                            dokarkivRequest.getSak().setFagsakId(null);
                        }

                        dokarkivRequest.setBruker(DokarkivRequest.Bruker.builder()
                                .idType(FNR)
                                .id(((PdlPersonBolk.PersonBolk) context.getProperty(PERSON_BOLK)).getIdent())
                                .build());

                        dokarkivRequest.getDokumenter()
                                .addAll(mapperFacade.mapAsList(rsDokarkiv.getDokumenter(), DokarkivRequest.Dokument.class));
                        fyllDokarkivDokument(dokarkivRequest);
                        dokarkivRequest.setFerdigstill(rsDokarkiv.getFerdigstill());
                    }

                    private String getNavn(PdlPersonBolk.PersonBolk personBolk) {

                        var navn = personBolk.getPerson().getNavn().stream().findFirst().orElse(new PdlPerson.Navn());
                        return String.format("%s, %s%s", navn.getFornavn(), navn.getEtternavn(),
                                isNull(navn.getMellomnavn()) ? "" : ", " + navn.getMellomnavn());
                    }
                })
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