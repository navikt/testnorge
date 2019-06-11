package no.nav.registre.core.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.udi.mt_1067_nav_data.v1.Avgjorelse;
import no.udi.mt_1067_nav_data.v1.AvgjorelseListe;
import no.udi.mt_1067_nav_data.v1.Avgjorelser;
import no.udi.mt_1067_nav_data.v1.Avgjorelsestype;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTABeslutningOmOppholdsrett;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdsrett;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAOpphold;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAOppholdstillatelse;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAVedtakOmVarigOppholdsrett;
import no.udi.mt_1067_nav_data.v1.GjeldendeOppholdsstatus;
import no.udi.mt_1067_nav_data.v1.GjeldendePerson;
import no.udi.mt_1067_nav_data.v1.HentPersonstatusResultat;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;
import no.udi.mt_1067_nav_data.v1.SoknadOmBeskyttelseUnderBehandling;
import no.udi.mt_1067_nav_data.v1.Tillatelse;
import no.udi.mt_1067_nav_data.v1.Utfall;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.core.database.model.Arbeidsadgang;
import no.nav.registre.core.database.model.Avgjoerelse;
import no.nav.registre.core.database.model.OppholdsStatus;
import no.nav.registre.core.database.model.Periode;
import no.nav.registre.core.database.model.Person;
import no.nav.registre.core.database.model.PersonNavn;

@Slf4j
public final class ModelToUDIResultConverter {

    public static HentPersonstatusResultat resultat(Person person) {

        HentPersonstatusResultat hentPersonstatusResultat = new HentPersonstatusResultat();
        hentPersonstatusResultat.setGjeldendePerson(gjeldendePerson(person));
        hentPersonstatusResultat.setArbeidsadgang(arbeidsadgang(person.getArbeidsadgang()));
        hentPersonstatusResultat.setAvgjorelsehistorikk(avgjorelser(person.getAvgjoerelser()));
        hentPersonstatusResultat.setForesporselsfodselsnummer(person.getFnr());
        hentPersonstatusResultat.setGjeldendeOppholdsstatus(gjeldendeOppholdsstatus(person.getOppholdsStatus()));
        hentPersonstatusResultat.setHarFlyktningstatus(person.getFlyktning());

        hentPersonstatusResultat.setHistorikkHarFlyktningstatus(person.getAvgjoerelser().parallelStream()
                .anyMatch(Avgjoerelse::isFlyktningstatus));

        hentPersonstatusResultat.setUavklartFlyktningstatus(person.isAvgjoerelseUavklart());
        hentPersonstatusResultat.setSoknadOmBeskyttelseUnderBehandling(beskyttelseUnderBehandling(person.getSoeksnadOmBeskyttelseUnderBehandling(), person.getSoknadDato()));

        try {
            hentPersonstatusResultat.setUttrekkstidspunkt(convertToXMLDate(new Date(Instant.now().getEpochSecond())));
        } catch (DatatypeConfigurationException e) {
            log.error(e.getLocalizedMessage(), e);
        }

        return hentPersonstatusResultat;
    }

    private static SoknadOmBeskyttelseUnderBehandling beskyttelseUnderBehandling(JaNeiUavklart uavklart, Date soeknadsDato) {
        SoknadOmBeskyttelseUnderBehandling beskyttelseUnderBehandling = new SoknadOmBeskyttelseUnderBehandling();
        beskyttelseUnderBehandling.setErUnderBehandling(uavklart);
        try {
            beskyttelseUnderBehandling.setSoknadsdato(convertToXMLDate(soeknadsDato));
        } catch (DatatypeConfigurationException e) {
            log.error(e.getLocalizedMessage(), e);
        }

        return beskyttelseUnderBehandling;
    }

    private static Avgjorelser avgjorelser(List<Avgjoerelse> avgjoerelseList) {
        Avgjorelser avgjorelserResultat = new Avgjorelser();

        AvgjorelseListeWrapper wrapper = new AvgjorelseListeWrapper(avgjoerelseList);

        avgjorelserResultat.setAvgjorelseListe(wrapper);

        return avgjorelserResultat;
    }

    private static GjeldendeOppholdsstatus gjeldendeOppholdsstatus(OppholdsStatus oppholdsStatus) {
        GjeldendeOppholdsstatus resultatOppholdsstatus = new GjeldendeOppholdsstatus();

        EOSellerEFTAOpphold eoSellerEFTAOpphold = new EOSellerEFTAOpphold();

        EOSellerEFTAGrunnlagskategoriOppholdsrett omOppholdsrett = oppholdsStatus.getEoSellerEFTABeslutningOmOppholdsrett();
        if (omOppholdsrett != null) {
            EOSellerEFTABeslutningOmOppholdsrett eoSellerEFTABeslutningOmOppholdsrett = new EOSellerEFTABeslutningOmOppholdsrett();
            try {
                eoSellerEFTABeslutningOmOppholdsrett.setEffektueringsdato(convertToXMLDate(oppholdsStatus.getEoSellerEFTABeslutningOmOppholdsrettEffektuering()));
            } catch (DatatypeConfigurationException e) {
                log.error(e.getLocalizedMessage(), e);
            }

            eoSellerEFTABeslutningOmOppholdsrett.setEOSOppholdsgrunnlag(oppholdsStatus.getEoSellerEFTABeslutningOmOppholdsrett());
            eoSellerEFTABeslutningOmOppholdsrett.setOppholdsrettsPeriode(periode(oppholdsStatus.getEoSellerEFTABeslutningOmOppholdsrettPeriode()));
            eoSellerEFTAOpphold.setEOSellerEFTABeslutningOmOppholdsrett(eoSellerEFTABeslutningOmOppholdsrett);
        }

        EOSellerEFTAGrunnlagskategoriOppholdsrett varigOppholdsrett = oppholdsStatus.getEoSellerEFTAVedtakOmVarigOppholdsrett();
        if (varigOppholdsrett != null) {
            EOSellerEFTAVedtakOmVarigOppholdsrett eoSellerEFTAVedtakOmVarigOppholdsrett = new EOSellerEFTAVedtakOmVarigOppholdsrett();
            try {
                eoSellerEFTAVedtakOmVarigOppholdsrett.setEffektueringsdato(convertToXMLDate(oppholdsStatus.getEoSellerEFTAVedtakOmVarigOppholdsrettEffektuering()));
            } catch (DatatypeConfigurationException e) {
                log.error(e.getLocalizedMessage(), e);
            }

            eoSellerEFTAVedtakOmVarigOppholdsrett.setEOSOppholdsgrunnlag(oppholdsStatus.getEoSellerEFTAVedtakOmVarigOppholdsrett());
            eoSellerEFTAVedtakOmVarigOppholdsrett.setOppholdsrettsPeriode(periode(oppholdsStatus.getEoSellerEFTAVedtakOmVarigOppholdsrettPeriode()));
            eoSellerEFTAOpphold.setEOSellerEFTAVedtakOmVarigOppholdsrett(eoSellerEFTAVedtakOmVarigOppholdsrett);
        }

        if (oppholdsStatus.getEoSellerEFTAOppholdstillatelse() != null) {
            EOSellerEFTAOppholdstillatelse oppholdstillatelse = new EOSellerEFTAOppholdstillatelse();

            try {
                oppholdstillatelse.setEffektueringsdato(convertToXMLDate(oppholdsStatus.getEoSellerEFTAOppholdstillatelseEffektuering()));
            } catch (DatatypeConfigurationException e) {
                log.error(e.getLocalizedMessage(), e);
            }

            oppholdstillatelse.setEOSOppholdsgrunnlag(oppholdsStatus.getEoSellerEFTAOppholdstillatelse());
            oppholdstillatelse.setOppholdstillatelsePeriode(periode(oppholdsStatus.getEoSellerEFTAOppholdstillatelsePeriode()));

            eoSellerEFTAOpphold.setEOSellerEFTAOppholdstillatelse(oppholdstillatelse);
        }


        resultatOppholdsstatus.setEOSellerEFTAOpphold(eoSellerEFTAOpphold);

        return resultatOppholdsstatus;
    }

    private static GjeldendePerson gjeldendePerson(Person person) {

        GjeldendePerson gjeldendePerson = new GjeldendePerson();
        gjeldendePerson.setFodselsnummer(person.getFnr());
        gjeldendePerson.setNavn(personNavn(person.getNavn()));

        return gjeldendePerson;
    }

    private static no.udi.mt_1067_nav_data.v1.PersonNavn personNavn(PersonNavn navn) {
        no.udi.mt_1067_nav_data.v1.PersonNavn personNavn = new no.udi.mt_1067_nav_data.v1.PersonNavn();
        personNavn.setFornavn(navn.getFornavn());
        personNavn.setEtternavn(navn.getEtternavn());
        personNavn.setMellomnavn(navn.getMellomnavn());
        return personNavn;
    }

    private static no.udi.mt_1067_nav_data.v1.Arbeidsadgang arbeidsadgang(Arbeidsadgang arbeidsadgang) {
        no.udi.mt_1067_nav_data.v1.Arbeidsadgang resultArbeidsadgang = new no.udi.mt_1067_nav_data.v1.Arbeidsadgang();
        resultArbeidsadgang.setArbeidsadgangsPeriode(periode(arbeidsadgang.getPeriode()));
        resultArbeidsadgang.setArbeidsOmfang(arbeidsadgang.getArbeidsOmfang());
        resultArbeidsadgang.setHarArbeidsadgang(arbeidsadgang.getHarArbeidsAdgang());
        resultArbeidsadgang.setTypeArbeidsadgang(arbeidsadgang.getTypeArbeidsadgang());
        return resultArbeidsadgang;
    }

    private static no.udi.mt_1067_nav_data.v1.Periode periode(Periode periode) {
        no.udi.mt_1067_nav_data.v1.Periode resultatPeriode = new no.udi.mt_1067_nav_data.v1.Periode();
        try {
            resultatPeriode.setFra(convertToXMLDate(periode.getFra()));
            resultatPeriode.setTil(convertToXMLDate(periode.getTil()));

        } catch (DatatypeConfigurationException e) {
            log.error(e.getLocalizedMessage(), e);
        }

        return resultatPeriode;
    }

    private static XMLGregorianCalendar convertToXMLDate(Date date) throws DatatypeConfigurationException {
        if (date == null) {
            return null;
        }
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(date.toString());
    }

    @Getter
    private static class AvgjorelseListeWrapper extends AvgjorelseListe {

        AvgjorelseListeWrapper(List<Avgjoerelse> aList) {
            this.avgjorelse = aList.parallelStream().map(
                    a -> {
                        Avgjorelse av = new Avgjorelse();
                        try {
                            av.setAvgjorelseDato(convertToXMLDate(a.getAvgjoerelsesDato()));

                            av.setAvgjorelseId(a.getId().toString());

                            Avgjorelsestype avgjorelsestype = new Avgjorelsestype();
                            avgjorelsestype.setGrunntypeKode(a.getGrunntypeKode());
                            avgjorelsestype.setTillatelseKode(a.getTillatelseKode());
                            avgjorelsestype.setUtfallstypeKode(a.getUtfallstypeKode());
                            av.setAvgjorelsestype(avgjorelsestype);

                            av.setEffektueringsDato(convertToXMLDate(a.getEffektueringsDato()));
                            av.setErPositiv(a.isErPositiv());
                            av.setEtat(a.getEtat());
                            av.setFlyktingstatus(a.isFlyktningstatus());

                            av.setIverksettelseDato(convertToXMLDate(a.getIverksettelseDato()));

                            av.setOmgjortavAvgjorelseId(a.getOmgjortAvgjoerelsesId());
                            av.setSaksnummer(a.getSaksnummer().toString());

                            Tillatelse tillatelse = new Tillatelse();
                            tillatelse.setGyldighetsperiode(periode(a.getTillatelsePeriode()));
                            tillatelse.setVarighet(a.getTillatelseVarighet());
                            tillatelse.setVarighetKode(a.getTillatelseVarighetKode());
                            av.setTillatelse(tillatelse);

                            av.setUavklartFlyktningstatus(a.isFlyktningstatus());

                            Utfall utfall = new Utfall();
                            utfall.setGjeldendePeriode(periode(a.getTillatelsePeriode()));
                            utfall.setVarighet(a.getTillatelseVarighet());
                            utfall.setVarighetKode(a.getTillatelseVarighetKode());
                            av.setUtfall(utfall);

                            if (a.getUtreisefristDato() != null) {
                                av.setUtreisefristDato(convertToXMLDate(a.getIverksettelseDato()));
                            } else {
                                av.setUtreisefristDato(null);
                            }


                        } catch (DatatypeConfigurationException e) {
                            log.error(e.getLocalizedMessage(), e);
                        }
                        return av;
                    }
            ).collect(Collectors.toList());
        }
    }

}
