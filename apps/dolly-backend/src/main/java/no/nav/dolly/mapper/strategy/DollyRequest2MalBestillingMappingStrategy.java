package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaAap;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaAap115;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaDagpenger;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.domain.resultset.etterlatte.EtterlatteYtelse;
import no.nav.dolly.domain.resultset.histark.RsHistark;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.inntektstub.RsInntektsinformasjon;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.domain.resultset.kontoregister.BankkontoData;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.sigrunstub.RsLignetInntekt;
import no.nav.dolly.domain.resultset.sigrunstub.RsPensjonsgivendeForFolketrygden;
import no.nav.dolly.domain.resultset.sigrunstub.RsSummertSkattegrunnlag;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.arbeidsplassencv.v1.ArbeidsplassenCVDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DeltBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedfoedtBarnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FalskIdentitetDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedestedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.IdentRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdsadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TelefonnummerDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TilrettelagtKommunikasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskIdentifikasjonsnummerDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import no.nav.testnav.libs.dto.skattekortservice.v1.ArbeidsgiverSkatt;
import no.nav.testnav.libs.dto.yrkesskade.v1.YrkesskadeRequest;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class DollyRequest2MalBestillingMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsDollyUtvidetBestilling.class, RsDollyUtvidetBestilling.class)
                .mapNulls(false)
                .field("arbeidsplassenCV", "arbeidsplassenCV")
                .field("arbeidssoekerregisteret", "arbeidssoekerregisteret")
                .field("arenaforvalter", "arenaforvalter")
                .field("bankkonto", "bankkonto")
                .field("brregstub", "brregstub")
                .field("fullmakt", "fullmakt")
                .field("inntektsmelding", "inntektsmelding")
                .field("inntektstub", "inntektstub")
                .field("krrstub", "krrstub")
                .field("medl", "medl")
                .field("nomdata", "nomdata")
                .field("navSyntetiskIdent", "navSyntetiskIdent")
                .field("pdldata", "pdldata")
                .field("pensjonforvalter", "pensjonforvalter")
                .field("skjerming", "skjerming")
                .field("sykemelding", "sykemelding")
                .field("udistub", "udistub")

                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsDollyUtvidetBestilling request, RsDollyUtvidetBestilling akkumulert, MappingContext context) {

                        akkumulert.getAareg().addAll(mapperFacade.mapAsList(request.getAareg(), RsAareg.class));
                        akkumulert.getEnvironments().addAll(request.getEnvironments());
                        akkumulert.getInstdata().addAll(mapperFacade.mapAsList(request.getInstdata(), RsInstdata.class));
                        akkumulert.getSigrunstub().addAll(mapperFacade.mapAsList(request.getSigrunstub(), RsLignetInntekt.class));
                        akkumulert.getSigrunstubPensjonsgivende().addAll(mapperFacade.mapAsList(request.getSigrunstubPensjonsgivende(), RsPensjonsgivendeForFolketrygden.class));
                        akkumulert.getSigrunstubSummertSkattegrunnlag().addAll(mapperFacade.mapAsList(request.getSigrunstubSummertSkattegrunnlag(), RsSummertSkattegrunnlag.class));
                        akkumulert.getYrkesskader().addAll(mapperFacade.mapAsList(request.getYrkesskader(), YrkesskadeRequest.class));
                        akkumulert.getDokarkiv().addAll(mapperFacade.mapAsList(request.getDokarkiv(), RsDokarkiv.class));
                        akkumulert.getEtterlatteYtelser().addAll(mapperFacade.mapAsList(request.getEtterlatteYtelser(), EtterlatteYtelse.class));
                        if (nonNull(request.getHistark())) {
                            if (isNull(akkumulert.getHistark())) {
                                akkumulert.setHistark(new RsHistark());
                            }
                            akkumulert.getHistark().getDokumenter()
                                    .addAll(mapperFacade.mapAsList(request.getHistark().getDokumenter(), RsHistark.RsHistarkDokument.class));
                        }
                        if (nonNull(request.getSkattekort())) {
                            if (isNull(akkumulert.getSkattekort())) {
                                akkumulert.setSkattekort(new no.nav.dolly.domain.resultset.skattekort.SkattekortRequestDTO());
                            }
                            akkumulert.getSkattekort().getArbeidsgiverSkatt()
                                    .addAll(mapperFacade.mapAsList(request.getSkattekort().getArbeidsgiverSkatt(), ArbeidsgiverSkatt.class));
                        }
                    }
                })
                .register();

        factory.classMap(RsPensjonsgivendeForFolketrygden.class, RsPensjonsgivendeForFolketrygden.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsPensjonsgivendeForFolketrygden request, RsPensjonsgivendeForFolketrygden akkumulert, MappingContext context) {

                        akkumulert.setInntektsaar(request.getInntektsaar());
                        akkumulert.setPensjonsgivendeInntekt(request.getPensjonsgivendeInntekt());
                    }
                })
                .register();

        factory.classMap(ArbeidsplassenCVDTO.Jobboensker.class, ArbeidsplassenCVDTO.Jobboensker.class)
                .mapNulls(false)
                .field("active", "active")
                .field("startOption", "startOption")
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(ArbeidsplassenCVDTO.Jobboensker jobboensker, ArbeidsplassenCVDTO.Jobboensker akkumulert, MappingContext context) {
                        akkumulert.getLocations().addAll(mapperFacade.mapAsList(jobboensker.getLocations(), ArbeidsplassenCVDTO.Location.class));
                        akkumulert.getOccupationDrafts().addAll(mapperFacade.mapAsList(jobboensker.getOccupationDrafts(), ArbeidsplassenCVDTO.OccupationDraft.class));
                        akkumulert.getOccupationTypes().addAll(mapperFacade.mapAsList(jobboensker.getOccupationTypes(), ArbeidsplassenCVDTO.OccupationType.class));
                        akkumulert.getOccupations().addAll(mapperFacade.mapAsList(jobboensker.getOccupations(), ArbeidsplassenCVDTO.Occupation.class));
                        akkumulert.getWorkLoadTypes().addAll(mapperFacade.mapAsList(jobboensker.getWorkLoadTypes(), ArbeidsplassenCVDTO.Omfang.class));
                        akkumulert.getWorkScheduleTypes().addAll(mapperFacade.mapAsList(jobboensker.getWorkScheduleTypes(), ArbeidsplassenCVDTO.Arbeidstid.class));
                    }
                })
                .register();

        factory.classMap(ArbeidsplassenCVDTO.class, ArbeidsplassenCVDTO.class)
                .mapNulls(false)
                .field("harBil", "harBil")
                .field("sammendrag", "sammendrag")
                .field("jobboensker", "jobboensker")
                .field("sistEndretAvNav", "sistEndretAvNav")
                .field("sistEndret", "sistEndret")

                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(ArbeidsplassenCVDTO arbeidsplassenCV, ArbeidsplassenCVDTO akkumulert, MappingContext context) {
                        akkumulert.getAndreGodkjenninger().addAll(mapperFacade.mapAsList(arbeidsplassenCV.getAndreGodkjenninger(), ArbeidsplassenCVDTO.AnnenGodkjenning.class));
                        akkumulert.getAnnenErfaring().addAll(mapperFacade.mapAsList(arbeidsplassenCV.getAnnenErfaring(), ArbeidsplassenCVDTO.AnnenErfaring.class));
                        akkumulert.getArbeidserfaring().addAll(mapperFacade.mapAsList(arbeidsplassenCV.getArbeidserfaring(), ArbeidsplassenCVDTO.Arbeidserfaring.class));
                        akkumulert.getFagbrev().addAll(mapperFacade.mapAsList(arbeidsplassenCV.getFagbrev(), ArbeidsplassenCVDTO.Fagbrev.class));
                        akkumulert.getFoererkort().addAll(mapperFacade.mapAsList(arbeidsplassenCV.getFoererkort(), ArbeidsplassenCVDTO.Foererkort.class));
                        akkumulert.getKompetanser().addAll(mapperFacade.mapAsList(arbeidsplassenCV.getKompetanser(), ArbeidsplassenCVDTO.Kompetanse.class));
                        akkumulert.getKurs().addAll(mapperFacade.mapAsList(arbeidsplassenCV.getKurs(), ArbeidsplassenCVDTO.Kurs.class));
                        akkumulert.getOffentligeGodkjenninger().addAll(mapperFacade.mapAsList(arbeidsplassenCV.getOffentligeGodkjenninger(), ArbeidsplassenCVDTO.OffentligeGodkjenning.class));
                        akkumulert.getSpraak().addAll(mapperFacade.mapAsList(arbeidsplassenCV.getSpraak(), ArbeidsplassenCVDTO.Spraak.class));
                        akkumulert.getUtdanning().addAll(mapperFacade.mapAsList(arbeidsplassenCV.getUtdanning(), ArbeidsplassenCVDTO.Utdanning.class));
                    }
                })
                .register();

        factory.classMap(RsDigitalKontaktdata.class, RsDigitalKontaktdata.class)
                .mapNulls(false)
                .byDefault()
                .register();

        factory.classMap(RsSykemelding.class, RsSykemelding.class)
                .mapNulls(false)
                .byDefault()
                .register();

        factory.classMap(BankkontoData.class, BankkontoData.class)
                .mapNulls(false)
                .byDefault()
                .register();

        factory.classMap(InntektMultiplierWrapper.class, InntektMultiplierWrapper.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(InntektMultiplierWrapper inntekt, InntektMultiplierWrapper akkumlert, MappingContext context) {
                        akkumlert.getInntektsinformasjon().addAll(mapperFacade.mapAsList(inntekt.getInntektsinformasjon(), RsInntektsinformasjon.class));
                    }
                })
                .register();

        factory.classMap(PensjonData.class, PensjonData.class)
                .mapNulls(false)
                .field("afpOffentlig", "afpOffentlig")
                .field("alderspensjon", "alderspensjon")
                .field("alderspensjonNyUtaksgrad", "alderspensjonNyUtaksgrad")
                .field("inntekt", "inntekt")
                .field("uforetrygd", "uforetrygd")
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PensjonData pensjonData, PensjonData akkumulert, MappingContext context) {
                        akkumulert.getTp().addAll(mapperFacade.mapAsList(pensjonData.getTp(), PensjonData.TpOrdning.class));
                        akkumulert.getPensjonsavtale().addAll(mapperFacade.mapAsList(pensjonData.getPensjonsavtale(), PensjonData.Pensjonsavtale.class));
                    }
                })
                .register();

        factory.classMap(Arenadata.class, Arenadata.class)
                .mapNulls(false)
                .field("aktiveringDato", "aktiveringDato")
                .field("arenaBrukertype", "arenaBrukertype")
                .field("automatiskInnsendingAvMeldekort", "automatiskInnsendingAvMeldekort")
                .field("inaktiveringDato", "inaktiveringDato")
                .field("kvalifiseringsgruppe", "kvalifiseringsgruppe")
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Arenadata arenadata, Arenadata akkumulert, MappingContext context) {
                        akkumulert.getAap115().addAll(mapperFacade.mapAsList(arenadata.getAap115(), RsArenaAap115.class));
                        akkumulert.getAap().addAll(mapperFacade.mapAsList(arenadata.getAap(), RsArenaAap.class));
                        akkumulert.getDagpenger().addAll(mapperFacade.mapAsList(arenadata.getDagpenger(), RsArenaDagpenger.class));
                    }
                })
                .register();

        factory.classMap(PdlPersondata.class, PdlPersondata.class)
                .mapNulls(false)
                .byDefault()
                .register();

        factory.classMap(PersonDTO.class, PersonDTO.class)
                .mapNulls(false)
                .field("ident", "ident")
                .field("identtype", "identtype")
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PersonDTO personDTO, PersonDTO akkumulertDTO, MappingContext context) {
                        akkumulertDTO.getAdressebeskyttelse().addAll(mapperFacade.mapAsList(personDTO.getAdressebeskyttelse(), AdressebeskyttelseDTO.class));
                        akkumulertDTO.getBostedsadresse().addAll(mapperFacade.mapAsList(personDTO.getBostedsadresse(), BostedadresseDTO.class));
                        akkumulertDTO.getDeltBosted().addAll(mapperFacade.mapAsList(personDTO.getDeltBosted(), DeltBostedDTO.class));
                        akkumulertDTO.getDoedfoedtBarn().addAll(mapperFacade.mapAsList(personDTO.getDoedfoedtBarn(), DoedfoedtBarnDTO.class));
                        akkumulertDTO.getDoedsfall().addAll(mapperFacade.mapAsList(personDTO.getDoedsfall(), DoedsfallDTO.class));
                        akkumulertDTO.getFalskIdentitet().addAll(mapperFacade.mapAsList(personDTO.getFalskIdentitet(), FalskIdentitetDTO.class));
                        akkumulertDTO.getFoedested().addAll(mapperFacade.mapAsList(personDTO.getFoedested(), FoedestedDTO.class));
                        akkumulertDTO.getFoedsel().addAll(mapperFacade.mapAsList(personDTO.getFoedsel(), FoedselDTO.class));
                        akkumulertDTO.getFoedselsdato().addAll(mapperFacade.mapAsList(personDTO.getFoedselsdato(), FoedselsdatoDTO.class));
                        akkumulertDTO.getFolkeregisterPersonstatus().addAll(mapperFacade.mapAsList(personDTO.getFolkeregisterPersonstatus(), FolkeregisterPersonstatusDTO.class));
                        akkumulertDTO.getForelderBarnRelasjon().addAll(mapperFacade.mapAsList(personDTO.getForelderBarnRelasjon(), ForelderBarnRelasjonDTO.class));
                        akkumulertDTO.getForeldreansvar().addAll(mapperFacade.mapAsList(personDTO.getForeldreansvar(), ForeldreansvarDTO.class));
                        akkumulertDTO.getFullmakt().addAll(mapperFacade.mapAsList(personDTO.getFullmakt(), FullmaktDTO.class));
                        akkumulertDTO.getInnflytting().addAll(mapperFacade.mapAsList(personDTO.getInnflytting(), InnflyttingDTO.class));
                        akkumulertDTO.getKjoenn().addAll(mapperFacade.mapAsList(personDTO.getKjoenn(), KjoennDTO.class));
                        akkumulertDTO.getKontaktadresse().addAll(mapperFacade.mapAsList(personDTO.getKontaktadresse(), KontaktadresseDTO.class));
                        akkumulertDTO.getKontaktinformasjonForDoedsbo().addAll(mapperFacade.mapAsList(personDTO.getKontaktinformasjonForDoedsbo(), KontaktinformasjonForDoedsboDTO.class));
                        akkumulertDTO.getNavn().addAll(mapperFacade.mapAsList(personDTO.getNavn(), NavnDTO.class));
                        akkumulertDTO.getNyident().addAll(mapperFacade.mapAsList(personDTO.getNyident(), IdentRequestDTO.class));
                        akkumulertDTO.getOpphold().addAll(mapperFacade.mapAsList(personDTO.getOpphold(), OppholdDTO.class));
                        akkumulertDTO.getOppholdsadresse().addAll(mapperFacade.mapAsList(personDTO.getOppholdsadresse(), OppholdsadresseDTO.class));
                        akkumulertDTO.getSikkerhetstiltak().addAll(mapperFacade.mapAsList(personDTO.getSikkerhetstiltak(), SikkerhetstiltakDTO.class));
                        akkumulertDTO.getSivilstand().addAll(mapperFacade.mapAsList(personDTO.getSivilstand(), SivilstandDTO.class));
                        akkumulertDTO.getStatsborgerskap().addAll(mapperFacade.mapAsList(personDTO.getStatsborgerskap(), StatsborgerskapDTO.class));
                        akkumulertDTO.getTelefonnummer().addAll(mapperFacade.mapAsList(personDTO.getTelefonnummer(), TelefonnummerDTO.class));
                        akkumulertDTO.getTilrettelagtKommunikasjon().addAll(mapperFacade.mapAsList(personDTO.getTilrettelagtKommunikasjon(), TilrettelagtKommunikasjonDTO.class));
                        akkumulertDTO.getUtenlandskIdentifikasjonsnummer().addAll(mapperFacade.mapAsList(personDTO.getUtenlandskIdentifikasjonsnummer(), UtenlandskIdentifikasjonsnummerDTO.class));
                        akkumulertDTO.getUtflytting().addAll(mapperFacade.mapAsList(personDTO.getUtflytting(), UtflyttingDTO.class));
                        akkumulertDTO.getVergemaal().addAll(mapperFacade.mapAsList(personDTO.getVergemaal(), VergemaalDTO.class));
                    }
                })
                .register();
    }
}
