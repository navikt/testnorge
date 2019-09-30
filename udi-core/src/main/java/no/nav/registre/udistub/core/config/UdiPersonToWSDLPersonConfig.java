package no.nav.registre.udistub.core.config;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import no.udi.common.v2.Kodeverk;
import no.udi.mt_1067_nav_data.v1.Alias;
import no.udi.mt_1067_nav_data.v1.AliasListe;
import no.udi.mt_1067_nav_data.v1.Arbeidsadgang;
import no.udi.mt_1067_nav_data.v1.Avgjorelse;
import no.udi.mt_1067_nav_data.v1.AvgjorelseListe;
import no.udi.mt_1067_nav_data.v1.Avgjorelser;
import no.udi.mt_1067_nav_data.v1.Avgjorelsestype;
import no.udi.mt_1067_nav_data.v1.GjeldendePerson;
import no.udi.mt_1067_nav_data.v1.HentPersonstatusResultat;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;
import no.udi.mt_1067_nav_data.v1.MangelfullDato;
import no.udi.mt_1067_nav_data.v1.Periode;
import no.udi.mt_1067_nav_data.v1.PersonNavn;
import no.udi.mt_1067_nav_data.v1.SoknadOmBeskyttelseUnderBehandling;
import no.udi.mt_1067_nav_data.v1.Tillatelse;
import no.udi.mt_1067_nav_data.v1.Utfall;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

import no.nav.registre.udistub.core.service.to.UdiAlias;
import no.nav.registre.udistub.core.service.to.UdiArbeidsadgang;
import no.nav.registre.udistub.core.service.to.UdiAvgjorelse;
import no.nav.registre.udistub.core.service.to.UdiPeriode;
import no.nav.registre.udistub.core.service.to.UdiPerson;
import no.nav.registre.udistub.core.service.to.UdiPersonNavn;

@Slf4j
@Configuration
public class UdiPersonToWSDLPersonConfig {

    private final String AVGJ_LIST = "avgjorelsehistorikk.avgjorelseListe";

    @Bean
    MapperFacade udiPersonToWSDLPersonMapper() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(UdiPerson.class, HentPersonstatusResultat.class)
                .field("arbeidsadgang.arbeidsadgangsPeriode", "arbeidsadgang.periode")
                .field("avgjorelseListe", "avgjoerelser")
                .field("uavklartFlyktningstatus", "flyktning")
                .field("harFlyktningstatus", "flyktning")
                .field("soknadOmBeskyttelseUnderBehandling", "soeknadOmBeskyttelseUnderBehandling")
                .field("foresporselsfodselsnummer", "ident")
                .field("gjeldendePerson.fodselsnummer", "ident")
                .field("gjeldendePerson.navn", "navn")
                .field("gjeldendePerson.fodselsdato", "foedselsDato")
                .field("gjeldendePerson.alias", "aliaser")
                .field("gjeldendeOppholdsstatus.eoSellerEFTAOpphold.eoSellerEFTABeslutningOmOppholdsrett",
                        "oppholdStatus.eosEllerEFTABeslutningOmOppholdsrett")
                .field("gjeldendeOppholdsstatus.eoSellerEFTAOpphold.eoSellerEFTAVedtakOmVarigOppholdsrett",
                        "oppholdStatus.eosEllerEFTAVedtakOmVarigOppholdsrett")
                .field("gjeldendeOppholdsstatus.eoSellerEFTAOpphold.eoSellerEFTAOppholdstillatelse",
                        "oppholdStatus.eosEllerEFTAOppholdstillatelse")
                .field("gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar.oppholdstillatelsePeriode",
                        "oppholdStatus.eosEllerEFTAOppholdstillatelsePeriode")
                .field("gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar.effektueringsdato",
                        "oppholdStatus.eosEllerEFTAOppholdstillatelseEffektuering")
                .field("gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar.oppholdstillatelse.oppholdstillatelseType",
                        "oppholdStatus.oppholdSammeVilkaar.oppholdstillatelseType")
                .field("gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar.oppholdstillatelse.vedtaksDato",
                        "oppholdStatus.oppholdSammeVilkaar.oppholdstillatelseVedtaksDato")
                .field("gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum.utvistMedInnreiseForbud.innreiseForbud",
                        "oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.utvistMedInnreiseForbud.innreiseForbud")
                .field("gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum.utvistMedInnreiseForbud.vedtaksDato",
                        "oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.utvistMedInnreiseForbud.innreiseForbudVedtaksDato")
                .field("gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum.utvistMedInnreiseForbud.varighet",
                        "oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.utvistMedInnreiseForbud.varighet")
                .field("gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum.avslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak.avgjorelsesDato",
                        "oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.avslagEllerBortfall.avgjorelsesDato")
                .field("gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum.avslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak.bortfallAvPOellerBOS.virkningsDato",
                        "oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.avslagEllerBortfall.bortfallAvPOellerBOSDato")
                .field("gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum.avslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak.tilbakeKall.virkningsDato",
                        "oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.avslagEllerBortfall.tilbakeKallVirkningsDato")
                .field("gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum.avslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak.tilbakeKall.utreiseFrist",
                        "oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.avslagEllerBortfall.tilbakeKallUtreiseFrist")
                .field("gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum.avslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak" +
                                ".avslagPaSoknadOmOppholdstillatelseRealitetsBehandlet.utreiseFrist",
                        "oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.avslagEllerBortfall.avslagOppholdstillatelseUtreiseFrist")
                .field("gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum.avslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak" +
                                ".avslagPaSoknadOmOppholdstillatelseRealitetsBehandlet.avslagsGrunnlagOvrig",
                        "oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.avslagEllerBortfall.avslagGrunnlagOverig")
                .field("gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum.avslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak" +
                                ".avslagPaSoknadOmOppholdstillatelseRealitetsBehandlet.avslagsGrunnlagEOS",
                        "oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.avslagEllerBortfall.avslagGrunnlagTillatelseGrunnlagEOS")
                .field("gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum.avslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak" +
                                ".avslagPaSoknadOmOppholdsrettRealitetsBehandlet.avslagsGrunnlagEOS",
                        "oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.avslagEllerBortfall.avslagOppholdsrettBehandlet")
                .field("gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum.avslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak" +
                                ".formeltVedtak.utreiseFrist",
                        "oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.avslagEllerBortfall.formeltVedtakUtreiseFrist")
                .field("gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum.ovrigIkkeOpphold.arsak",
                        "oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.ovrigIkkeOppholdsKategoriArsak")
                .field("arbeidsadgang.arbeidsadgangsPeriode", "arbeidsadgang.periode")
                .field("arbeidsadgang.arbeidsadgangsPeriode", "arbeidsadgang.periode")
                .field(AVGJ_LIST, "avgjoerelser")
                .register();

        mapperFactory.classMap(XMLGregorianCalendar.class, LocalDate.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapBtoA(LocalDate date, XMLGregorianCalendar xmlGregorianCalendar, MappingContext context) {
                        try {
                            XMLGregorianCalendar xmlGregorianCalendar1 = DatatypeFactory.newInstance().newXMLGregorianCalendar(date.toString());
                            xmlGregorianCalendar.setDay(xmlGregorianCalendar1.getDay());
                            xmlGregorianCalendar.setMonth(xmlGregorianCalendar1.getMonth());
                            xmlGregorianCalendar.setYear(xmlGregorianCalendar1.getYear());
                        } catch (DatatypeConfigurationException e) {
                            e.printStackTrace();
                        }
                    }
                }).register();

        mapperFactory.classMap(Periode.class, UdiPeriode.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapBtoA(UdiPeriode udiPeriode, Periode periode, MappingContext context) {
                        periode.setFra(mapperFactory.getMapperFacade().map(udiPeriode.getFra(), XMLGregorianCalendar.class));
                        periode.setTil(mapperFactory.getMapperFacade().map(udiPeriode.getTil(), XMLGregorianCalendar.class));
                    }
                }).register();

        mapperFactory.classMap(MangelfullDato.class, LocalDate.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapBtoA(LocalDate date, MangelfullDato mangelfullDato, MappingContext context) {
                        mangelfullDato.setAr(date.getYear());
                        mangelfullDato.setManed(date.getMonthValue());
                        mangelfullDato.setDag(date.getDayOfMonth());
                    }
                }).register();

        mapperFactory.classMap(Kodeverk.class, no.nav.registre.udistub.core.database.model.Kodeverk.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapBtoA(no.nav.registre.udistub.core.database.model.Kodeverk kodeverk, Kodeverk kodeverk2, MappingContext context) {
                        kodeverk2.setType(kodeverk.getType());
                        kodeverk2.setVisningsnavn(kodeverk.getVisningsnavn());
                        kodeverk2.setKode(kodeverk.getKode());
                    }
                }).register();

        mapperFactory.classMap(Avgjorelsestype.class, UdiAvgjorelse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapBtoA(UdiAvgjorelse avgjorelse, Avgjorelsestype avgjorelsestype2, MappingContext context) {
                        avgjorelsestype2.setGrunntypeKode(mapperFactory.getMapperFacade().map(avgjorelse.getGrunntypeKode(), Kodeverk.class));
                        avgjorelsestype2.setTillatelseKode(mapperFactory.getMapperFacade().map(avgjorelse.getTillatelseKode(), Kodeverk.class));
                        avgjorelsestype2.setUtfallstypeKode(mapperFactory.getMapperFacade().map(avgjorelse.getUtfallstypeKode(), Kodeverk.class));
                    }
                }).register();

        mapperFactory.classMap(Tillatelse.class, UdiAvgjorelse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapBtoA(UdiAvgjorelse avgjorelse, Tillatelse tillatelse, MappingContext context) {
                        tillatelse.setVarighetKode(mapperFactory.getMapperFacade().map(avgjorelse.getTillatelseVarighetKode(), Kodeverk.class));
                        tillatelse.setVarighet(avgjorelse.getTillatelseVarighet());
                        tillatelse.setGyldighetsperiode(mapperFactory.getMapperFacade().map(avgjorelse.getTillatelsePeriode(), Periode.class));
                    }
                }).register();

        mapperFactory.classMap(Utfall.class, UdiAvgjorelse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapBtoA(UdiAvgjorelse avgjorelse, Utfall utfall, MappingContext context) {
                        utfall.setVarighetKode(mapperFactory.getMapperFacade().map(avgjorelse.getUtfallVarighetKode(), Kodeverk.class));
                        utfall.setVarighet(avgjorelse.getUtfallVarighet());
                        utfall.setGjeldendePeriode(mapperFactory.getMapperFacade().map(avgjorelse.getUtfallPeriode(), Periode.class));
                    }
                }).register();

        mapperFactory.classMap(Avgjorelse.class, UdiAvgjorelse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapBtoA(UdiAvgjorelse udiAvgjorelse, Avgjorelse avgjorelse, MappingContext context) {
                        avgjorelse.setAvgjorelsestype(mapperFactory.getMapperFacade().map(udiAvgjorelse, Avgjorelsestype.class));
                        avgjorelse.setUtreisefristDato(mapperFactory.getMapperFacade().map(udiAvgjorelse.getUtreisefristDato(), XMLGregorianCalendar.class));
                        avgjorelse.setAvgjorelseDato(mapperFactory.getMapperFacade().map(udiAvgjorelse.getAvgjoerelsesDato(), XMLGregorianCalendar.class));
                        avgjorelse.setEffektueringsDato(mapperFactory.getMapperFacade().map(udiAvgjorelse.getEffektueringsDato(), XMLGregorianCalendar.class));
                        avgjorelse.setErPositiv(udiAvgjorelse.getErPositiv());
                        avgjorelse.setEtat(udiAvgjorelse.getEtat());
                        avgjorelse.setFlyktingstatus(udiAvgjorelse.getHarFlyktningstatus());
                        avgjorelse.setIverksettelseDato(mapperFactory.getMapperFacade().map(udiAvgjorelse.getIverksettelseDato(), XMLGregorianCalendar.class));
                        avgjorelse.setSaksnummer(udiAvgjorelse.getSaksnummer());
                        avgjorelse.setTillatelse(mapperFactory.getMapperFacade().map(udiAvgjorelse, Tillatelse.class));
                        avgjorelse.setUavklartFlyktningstatus(udiAvgjorelse.getUavklartFlyktningstatus());
                        avgjorelse.setUtfall(mapperFactory.getMapperFacade().map(udiAvgjorelse, Utfall.class));
                    }
                }).register();

        mapperFactory.classMap(Arbeidsadgang.class, UdiArbeidsadgang.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapBtoA(UdiArbeidsadgang udiArbeidsadgang, Arbeidsadgang arbeidsadgang, MappingContext context) {
                        arbeidsadgang.setArbeidsadgangsPeriode(mapperFactory.getMapperFacade().map(udiArbeidsadgang.getPeriode(), Periode.class));
                        arbeidsadgang.setArbeidsOmfang(udiArbeidsadgang.getArbeidsOmfang());
                        arbeidsadgang.setHarArbeidsadgang(udiArbeidsadgang.getHarArbeidsadgang());
                        arbeidsadgang.setTypeArbeidsadgang(udiArbeidsadgang.getTypeArbeidsadgang());
                    }
                }).register();

        mapperFactory.classMap(PersonNavn.class, UdiPersonNavn.class)
                .customize(new CustomMapper<PersonNavn, UdiPersonNavn>() {
                    @Override
                    public void mapBtoA(UdiPersonNavn udiPersonNavn, PersonNavn personNavn, MappingContext context) {
                        personNavn.setEtternavn(udiPersonNavn.getEtternavn());
                        personNavn.setMellomnavn(udiPersonNavn.getMellomnavn());
                        personNavn.setFornavn(udiPersonNavn.getFornavn());
                    }
                }).register();

        mapperFactory.classMap(Alias.class, UdiAlias.class)
                .customize(new CustomMapper<Alias, UdiAlias>() {
                    @Override
                    public void mapBtoA(UdiAlias udiAlias, Alias alias, MappingContext context) {
                        alias.setFodselsnummer(udiAlias.getFnr());
                        alias.setNavn(mapperFactory.getMapperFacade().map(udiAlias.getNavn(), PersonNavn.class));
                    }
                }).register();

        mapperFactory.classMap(GjeldendePerson.class, UdiPerson.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapBtoA(UdiPerson udiPerson, GjeldendePerson gjeldendePerson, MappingContext context) {
                        gjeldendePerson.setFodselsnummer(udiPerson.getIdent());
                        gjeldendePerson.setAlias(mapAliasListe(udiPerson));
                        gjeldendePerson.setFodselsdato(mapperFactory.getMapperFacade().map(udiPerson.getFoedselsDato(), MangelfullDato.class));
                        gjeldendePerson.setNavn(mapperFactory.getMapperFacade().map(udiPerson.getNavn(), PersonNavn.class));
                    }

                    private AliasListe mapAliasListe(UdiPerson udiPerson) {
                        AliasListe liste = new AliasListe();
                        udiPerson.getAliaser().forEach(a -> liste.getAlias().add(mapperFactory.getMapperFacade().map(a, Alias.class)));
                        return liste;
                    }
                }).register();

        mapperFactory.classMap(SoknadOmBeskyttelseUnderBehandling.class, JaNeiUavklart.class)
                .customize(new CustomMapper<SoknadOmBeskyttelseUnderBehandling, JaNeiUavklart>() {
                    @Override
                    public void mapBtoA(JaNeiUavklart jaNeiUavklart, SoknadOmBeskyttelseUnderBehandling soknadOmBeskyttelseUnderBehandling, MappingContext context) {
                        soknadOmBeskyttelseUnderBehandling.setErUnderBehandling(jaNeiUavklart);
                    }
                }).register();

        mapperFactory.classMap(HentPersonstatusResultat.class, UdiPerson.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapBtoA(UdiPerson udiPerson, HentPersonstatusResultat resultat, MappingContext context) {
                        resultat.setArbeidsadgang(mapperFactory.getMapperFacade().map(udiPerson.getArbeidsadgang(), Arbeidsadgang.class));
                        resultat.setAvgjorelsehistorikk(mapAvgjorelse(udiPerson));
                        resultat.setForesporselsfodselsnummer(udiPerson.getIdent());
                        resultat.setGjeldendePerson(mapperFactory.getMapperFacade().map(udiPerson, GjeldendePerson.class));
                        resultat.setHarFlyktningstatus(udiPerson.getFlyktning());
                        resultat.setHistorikkHarFlyktningstatus(udiPerson.getAvgjoerelser().stream()
                                .map(UdiAvgjorelse::getHarFlyktningstatus)
                                .reduce(false, (a, b) -> a || b));
                        resultat.setSoknadOmBeskyttelseUnderBehandling(mapperFactory.getMapperFacade().map(udiPerson.getSoeknadOmBeskyttelseUnderBehandling(),
                                SoknadOmBeskyttelseUnderBehandling.class));
                        resultat.setUavklartFlyktningstatus(udiPerson.getAvgjoerelser().stream()
                                .map(UdiAvgjorelse::getUavklartFlyktningstatus)
                                .reduce(false, (a, b) -> a || b));
                    }

                    private Avgjorelser mapAvgjorelse(UdiPerson udiPerson) {
                        Avgjorelser avgjorelser = new Avgjorelser();
                        AvgjorelseListe avgjorelseListe = new AvgjorelseListe();
                        udiPerson.getAvgjoerelser().forEach(a -> avgjorelseListe.getAvgjorelse().add(mapperFactory.getMapperFacade().map(a, Avgjorelse.class)));
                        avgjorelser.setAvgjorelseListe(avgjorelseListe);
                        return avgjorelser;
                    }
                }).register();

        return mapperFactory.getMapperFacade();
    }


}
