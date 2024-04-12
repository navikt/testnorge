package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.kontoregister.BankkontoData;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.data.arbeidsplassencv.v1.ArbeidsplassenCVDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Component;

@Component
public class DollyRequest2MalBestillingMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsDollyUtvidetBestilling.class, RsDollyUtvidetBestilling.class)
                .mapNulls(false)
                .field("arbeidsplassenCV", "arbeidsplassenCV")
                .field("arenaforvalter", "arenaforvalter")
                .field("bankkonto", "bankkonto")
                .field("brregstub", "brregstub")
                .field("dokarkiv", "dokarkiv")
                .field("environments", "environments")
                .field("histark", "histark")
                .field("inntektsmelding", "inntektsmelding")
                .field("inntektstub", "inntektstub")
                .field("krrstub", "krrstub")
                .field("medl", "medl")
                .field("navSyntetiskIdent", "navSyntetiskIdent")
                .field("pdldata", "pdldata")
                .field("pensjonforvalter", "pensjonforvalter")
                .field("sigrunstub", "sigrunstub")
                .field("sigrunstubPensjonsgivende", "sigrunstubPensjonsgivende")
                .field("skjerming", "skjerming")
                .field("sykemelding", "sykemelding")
                .field("tpsMessaging", "tpsMessaging")
                .field("udistub", "udistub")

                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsDollyUtvidetBestilling request, RsDollyUtvidetBestilling akkumulert, MappingContext context) {

                        akkumulert.getAareg().addAll(request.getAareg());
                        akkumulert.getEnvironments().addAll(request.getEnvironments());
                        akkumulert.getInstdata().addAll(request.getInstdata());
                        akkumulert.getSigrunstub().addAll(request.getSigrunstub());
                        akkumulert.getSigrunstubPensjonsgivende().addAll(request.getSigrunstubPensjonsgivende());
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
                        akkumulert.getLocations().addAll(jobboensker.getLocations());
                        akkumulert.getOccupationDrafts().addAll(jobboensker.getOccupationDrafts());
                        akkumulert.getOccupationTypes().addAll(jobboensker.getOccupationTypes());
                        akkumulert.getOccupations().addAll(jobboensker.getOccupations());
                        akkumulert.getWorkLoadTypes().addAll(jobboensker.getWorkLoadTypes());
                        akkumulert.getWorkScheduleTypes().addAll(jobboensker.getWorkScheduleTypes());
                    }
                })
                .register();

        factory.classMap(ArbeidsplassenCVDTO.class, ArbeidsplassenCVDTO.class)
                .mapNulls(false)
                .field("harHjemmel", "harHjemmel")
                .field("harBil", "harBil")
                .field("sammendrag", "sammendrag")
                .field("jobboensker", "jobboensker")
                .field("sistEndretAvNav", "sistEndretAvNav")
                .field("sistEndret", "sistEndret")

                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(ArbeidsplassenCVDTO arbeidsplassenCV, ArbeidsplassenCVDTO akkumulert, MappingContext context) {
                        akkumulert.getAndreGodkjenninger().addAll(arbeidsplassenCV.getAndreGodkjenninger());
                        akkumulert.getAnnenErfaring().addAll(arbeidsplassenCV.getAnnenErfaring());
                        akkumulert.getArbeidserfaring().addAll(arbeidsplassenCV.getArbeidserfaring());
                        akkumulert.getFagbrev().addAll(arbeidsplassenCV.getFagbrev());
                        akkumulert.getFoererkort().addAll(arbeidsplassenCV.getFoererkort());
                        akkumulert.getKompetanser().addAll(arbeidsplassenCV.getKompetanser());
                        akkumulert.getKurs().addAll(arbeidsplassenCV.getKurs());
                        akkumulert.getOffentligeGodkjenninger().addAll(arbeidsplassenCV.getOffentligeGodkjenninger());
                        akkumulert.getSpraak().addAll(arbeidsplassenCV.getSpraak());
                        akkumulert.getUtdanning().addAll(arbeidsplassenCV.getUtdanning());
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
                        akkumlert.getInntektsinformasjon().addAll(inntekt.getInntektsinformasjon());
                    }
                })
                .register();

        factory.classMap(PensjonData.class, PensjonData.class)
                .mapNulls(false)
                .field("inntekt", "inntekt")
                .field("alderspensjon", "alderspensjon")
                .field("uforetrygd", "uforetrygd")
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PensjonData pensjonData, PensjonData akkumulert, MappingContext context) {
                        akkumulert.getTp().addAll(pensjonData.getTp());
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
                        akkumulert.getAap115().addAll(arenadata.getAap115());
                        akkumulert.getAap().addAll(arenadata.getAap());
                        akkumulert.getDagpenger().addAll(arenadata.getDagpenger());
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
                        akkumulertDTO.getAdressebeskyttelse().addAll(personDTO.getAdressebeskyttelse());
                        akkumulertDTO.getBostedsadresse().addAll(personDTO.getBostedsadresse());
                        akkumulertDTO.getBostedsadresse().addAll(personDTO.getBostedsadresse());
                        akkumulertDTO.getDeltBosted().addAll(personDTO.getDeltBosted());
                        akkumulertDTO.getDoedfoedtBarn().addAll(personDTO.getDoedfoedtBarn());
                        akkumulertDTO.getDoedsfall().addAll(personDTO.getDoedsfall());
                        akkumulertDTO.getFalskIdentitet().addAll(personDTO.getFalskIdentitet());
                        akkumulertDTO.getFoedsel().addAll(personDTO.getFoedsel());
                        akkumulertDTO.getFolkeregisterPersonstatus().addAll(personDTO.getFolkeregisterPersonstatus());
                        akkumulertDTO.getForelderBarnRelasjon().addAll(personDTO.getForelderBarnRelasjon());
                        akkumulertDTO.getForeldreansvar().addAll(personDTO.getForeldreansvar());
                        akkumulertDTO.getFullmakt().addAll(personDTO.getFullmakt());
                        akkumulertDTO.getInnflytting().addAll(personDTO.getInnflytting());
                        akkumulertDTO.getKjoenn().addAll(personDTO.getKjoenn());
                        akkumulertDTO.getKontaktadresse().addAll(personDTO.getKontaktadresse());
                        akkumulertDTO.getKontaktinformasjonForDoedsbo().addAll(personDTO.getKontaktinformasjonForDoedsbo());
                        akkumulertDTO.getNavn().addAll(personDTO.getNavn());
                        akkumulertDTO.getNyident().addAll(personDTO.getNyident());
                        akkumulertDTO.getOpphold().addAll(personDTO.getOpphold());
                        akkumulertDTO.getOppholdsadresse().addAll(personDTO.getOppholdsadresse());
                        akkumulertDTO.getSikkerhetstiltak().addAll(personDTO.getSikkerhetstiltak());
                        akkumulertDTO.getSivilstand().addAll(personDTO.getSivilstand());
                        akkumulertDTO.getStatsborgerskap().addAll(personDTO.getStatsborgerskap());
                        akkumulertDTO.getTelefonnummer().addAll(personDTO.getTelefonnummer());
                        akkumulertDTO.getTilrettelagtKommunikasjon().addAll(personDTO.getTilrettelagtKommunikasjon());
                        akkumulertDTO.getUtenlandskIdentifikasjonsnummer().addAll(personDTO.getUtenlandskIdentifikasjonsnummer());
                        akkumulertDTO.getUtflytting().addAll(personDTO.getUtflytting());
                        akkumulertDTO.getVergemaal().addAll(personDTO.getVergemaal());
                    }
                })
                .register();
    }
}
