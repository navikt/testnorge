package no.nav.dolly.mapper.strategy;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsavtale;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.aareg.RsPermisjon;
import no.nav.dolly.domain.resultset.aareg.RsPersonAareg;
import no.nav.dolly.domain.resultset.aareg.RsUtenlandsopphold;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Arbeidsavtale;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Arbeidsforhold;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Arbeidsforholdstyper;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Arbeidstidsordninger;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Avloenningstyper;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Kodeverdi;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Landkoder;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.NorskIdent;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Organisasjon;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Permisjon;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.PermisjonsOgPermitteringsBeskrivelse;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Person;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Personidenter;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Utenlandsopphold;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Yrker;

@Component
public class AaregMappingStrategy implements MappingStrategy {

    @Override public void register(MapperFactory factory) {
        factory.classMap(RsArbeidsforhold.class, Arbeidsforhold.class)
                .customize(new CustomMapper<RsArbeidsforhold, Arbeidsforhold>() {
                    @Override public void mapAtoB(RsArbeidsforhold rsArbeidsforhold, Arbeidsforhold arbeidsforhold, MappingContext context) {

                        arbeidsforhold.setArbeidsforholdstype(mapKodeverdi(new Arbeidsforholdstyper(), rsArbeidsforhold.getArbeidsforholdstype()));
                        arbeidsforhold.setArbeidstaker(mapPerson(rsArbeidsforhold.getArbeidstaker()));

                        arbeidsforhold.setArbeidsgiver(
                                rsArbeidsforhold.getArbeidsgiver() instanceof RsOrganisasjon ?
                                        mapperFacade.map(rsArbeidsforhold.getArbeidsgiver(), Organisasjon.class) :
                                        mapperFacade.map(rsArbeidsforhold.getArbeidsgiver(), Person.class));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsArbeidsavtale.class, Arbeidsavtale.class)
                .customize(new CustomMapper<RsArbeidsavtale, Arbeidsavtale>() {
                    @Override public void mapAtoB(RsArbeidsavtale rsArbeidsavtale, Arbeidsavtale arbeidsavtale, MappingContext context) {

                        arbeidsavtale.setArbeidstidsordning(mapKodeverdi(new Arbeidstidsordninger(), rsArbeidsavtale.getArbeidstidsordning()));
                        arbeidsavtale.setAvloenningstype(mapKodeverdi(new Avloenningstyper(), rsArbeidsavtale.getAvloenningstype()));
                        arbeidsavtale.setYrke(mapKodeverdi(new Yrker(), rsArbeidsavtale.getYrke()));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsPermisjon.class, Permisjon.class)
                .customize(new CustomMapper<RsPermisjon, Permisjon>() {
                    @Override public void mapAtoB(RsPermisjon rsPermisjon, Permisjon permisjon, MappingContext context) {

                        permisjon.setPermisjonOgPermittering(mapKodeverdi(new PermisjonsOgPermitteringsBeskrivelse(), rsPermisjon.getPermisjonOgPermittering()));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsUtenlandsopphold.class, Utenlandsopphold.class)
                .customize(new CustomMapper<RsUtenlandsopphold, Utenlandsopphold>() {
                    @Override public void mapAtoB(RsUtenlandsopphold rsUtenlandsopphold, Utenlandsopphold utenlandsopphold, MappingContext context) {

                        utenlandsopphold.setLand(mapKodeverdi(new Landkoder(), rsUtenlandsopphold.getLand()));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsOrganisasjon.class, Organisasjon.class)
                .customize(new CustomMapper<RsOrganisasjon, Organisasjon>() {
                    @Override public void mapAtoB(RsOrganisasjon rsOrganisasjon, Organisasjon organisasjon, MappingContext context) {

                        organisasjon.setOrgnummer(rsOrganisasjon.getOrgnummer());
                    }
                })
                .register();

        factory.classMap(RsAktoerPerson.class, Person.class)
                .customize(new CustomMapper<RsAktoerPerson, Person>() {
                    @Override public void mapAtoB(RsAktoerPerson rsAktoerPerson, Person person, MappingContext context) {

                        NorskIdent norskIdent = new NorskIdent();
                        norskIdent.setIdent(rsAktoerPerson.getIdent());

                        norskIdent.setType(mapKodeverdi(new Personidenter(), rsAktoerPerson.getIdenttype()));

                        person.setIdent(norskIdent);
                    }
                })
                .register();
    }

    private Person mapPerson(RsPersonAareg rsPerson) {

        NorskIdent norskIdent = new NorskIdent();
        norskIdent.setIdent(rsPerson.getIdent());

        norskIdent.setType(mapKodeverdi(new Personidenter(), rsPerson.getIdenttype()));

        Person person = new Person();
        person.setIdent(norskIdent);

        return person;
    }

    private static <T extends Kodeverdi> T mapKodeverdi(T clazz, String value) {
        clazz.setKodeRef(value);
        return clazz;
    }
}