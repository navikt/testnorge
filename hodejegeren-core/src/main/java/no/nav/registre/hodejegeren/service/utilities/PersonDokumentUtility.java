package no.nav.registre.hodejegeren.service.utilities;

import no.rtv.namespacetps.BoadresseType;
import no.rtv.namespacetps.DodType;
import no.rtv.namespacetps.ForeldreansvarType;
import no.rtv.namespacetps.GironummerType;
import no.rtv.namespacetps.MigrasjonType;
import no.rtv.namespacetps.NavnType;
import no.rtv.namespacetps.OppholdstillatelseType;
import no.rtv.namespacetps.PersonIdentStatusType;
import no.rtv.namespacetps.PersonIdentType;
import no.rtv.namespacetps.PersonInfoType;
import no.rtv.namespacetps.PersonStatusType;
import no.rtv.namespacetps.PostType;
import no.rtv.namespacetps.PrioritertadresseType;
import no.rtv.namespacetps.RelasjonType;
import no.rtv.namespacetps.SivilstandType;
import no.rtv.namespacetps.StatsborgerType;
import no.rtv.namespacetps.TelefonJobbType;
import no.rtv.namespacetps.TelefonMobilType;
import no.rtv.namespacetps.TelefonPrivatType;
import no.rtv.namespacetps.TilleggType;
import no.rtv.namespacetps.TpsPersonDokumentType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.registre.hodejegeren.provider.rs.requests.skd.PersonDokumentWrapper;
import no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold.Boadresse;
import no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold.Doedshistorikk;
import no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold.Foreldreansvar;
import no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold.Giro;
import no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold.Migrasjon;
import no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold.Navn;
import no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold.Oppholdstillatelse;
import no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold.PersonIdent;
import no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold.PersonInfo;
import no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold.Post;
import no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold.PrioritertAdresse;
import no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold.Relasjon;
import no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold.Sivilstand;
import no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold.Statsborger;
import no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold.TelefonJobb;
import no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold.TelefonMobil;
import no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold.TelefonPrivat;
import no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold.Tillegg;

public class PersonDokumentUtility {

    public static PersonDokumentWrapper convertToPersonDokumentWrapper(TpsPersonDokumentType tpsPersonDokumentType) {
        List<Foreldreansvar> foreldreansvar = new ArrayList<>();
        List<Relasjon> relasjoner = new ArrayList<>();

        PersonIdentType personIdentType = extractPersonIdent(tpsPersonDokumentType);
        PersonIdentStatusType personIdentStatusType = extractPersonIdentStatus(tpsPersonDokumentType);
        PersonInfoType personInfoType = extractPersonInfoType(tpsPersonDokumentType);
        PersonStatusType personStatusType = extractPersonStatusType(tpsPersonDokumentType);
        NavnType navnType = extractNavnType(tpsPersonDokumentType);
        SivilstandType sivilstandType = extractSivilstandType(tpsPersonDokumentType);
        StatsborgerType statsborgerType = extractStatsborgerType(tpsPersonDokumentType);
        DodType dodType = extractDodType(tpsPersonDokumentType);
        TelefonPrivatType telefonPrivatType = extractTelefonPrivatType(tpsPersonDokumentType);
        TelefonJobbType telefonJobbType = extractTelefonJobbType(tpsPersonDokumentType);
        TelefonMobilType telefonMobilType = extractTelefonmobilType(tpsPersonDokumentType);
        BoadresseType boadresseType = extractBoadresseType(tpsPersonDokumentType);
        PrioritertadresseType prioritertadresseType = extractPrioritertadresseType(tpsPersonDokumentType);

        List<ForeldreansvarType> foreldreansvarTypeListe = extractForeldreansvarType(tpsPersonDokumentType);
        for (ForeldreansvarType foreldreansvarType : foreldreansvarTypeListe) {

            Foreldreansvar foreldreansvaret = buildForeldreansvar(foreldreansvarType);
            if (!foreldreansvaret.getIdent().isEmpty()) {
                foreldreansvar.add(foreldreansvaret);
            }
        }

        OppholdstillatelseType oppholdstillatelseType = extractOppholdstillatelseType(tpsPersonDokumentType);
        GironummerType gironummerType = extractGironummerType(tpsPersonDokumentType);
        TilleggType tilleggType = extractTilleggType(tpsPersonDokumentType);
        PostType postType = extractPostType(tpsPersonDokumentType);
        MigrasjonType migrasjonType = extractmigrasjonType(tpsPersonDokumentType);

        List<RelasjonType> relasjonTypeListe = extractRelasjonType(tpsPersonDokumentType);
        for (RelasjonType relasjonType : relasjonTypeListe) {
            Relasjon relasjonen = buildRelasjon(relasjonType);
            if (!relasjonen.getIdent().isEmpty() && !relasjonen.getType().isEmpty() && !relasjonen.getStatus().isEmpty() && !relasjonen.getRolle().isEmpty()) {
                relasjoner.add(relasjonen);
            }
        }

        return PersonDokumentWrapper.builder()
                .personIdent(buildPersonident(personIdentType, personIdentStatusType))
                .personInfo(buildPersonInfo(personInfoType, personStatusType))
                .navn(buildNavn(navnType))
                .sivilstand(buildSivilstand(sivilstandType))
                .statsborger(buildStatsborger(statsborgerType))
                .doedshistorikk(buildDoedshistorikk(dodType))
                .telefonPrivat(buildTelefonPrivat(telefonPrivatType))
                .telefonJobb(buildTelefonJobb(telefonJobbType))
                .telefonMobil(buildTelefonMobil(telefonMobilType))
                .boadresse(buildBoadresse(boadresseType))
                .prioritertAdresse(buildPrioritertAdresse(prioritertadresseType))
                .foreldreansvar(foreldreansvar)
                .oppholdstillatelse(buildOppholdstillatelse(oppholdstillatelseType))
                .giro(buildGiro(gironummerType))
                .tillegg(buildTillegg(tilleggType))
                .post(buildPost(postType))
                .migrasjon(buildMigrasjon(migrasjonType))
                .relasjoner(relasjoner)
                .build();
    }

    private static PersonIdentType extractPersonIdent(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getPerson().getPersonIdent().get(0);
    }

    private static PersonIdentStatusType extractPersonIdentStatus(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getPerson().getPersonIdentstatus().get(0);
    }

    private static PersonInfoType extractPersonInfoType(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getPerson().getPersonInfo().get(0);
    }

    private static PersonStatusType extractPersonStatusType(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getPerson().getPersonStatus().get(0);
    }

    private static NavnType extractNavnType(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getPerson().getNavn().get(0);
    }

    private static SivilstandType extractSivilstandType(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getPerson().getSivilstand().get(0);
    }

    private static StatsborgerType extractStatsborgerType(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getPerson().getStatsborger().get(0);
    }

    private static DodType extractDodType(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getPerson().getDod().get(0);
    }

    private static TelefonPrivatType extractTelefonPrivatType(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getPerson().getTelefon().get(0).getTlfPrivat();
    }

    private static TelefonJobbType extractTelefonJobbType(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getPerson().getTelefon().get(0).getTlfJobb();
    }

    private static TelefonMobilType extractTelefonmobilType(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getPerson().getTelefon().get(0).getTlfMobil();
    }

    private static BoadresseType extractBoadresseType(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getPerson().getBoadresse().get(0);
    }

    private static PrioritertadresseType extractPrioritertadresseType(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getPerson().getPrioritertadresse().get(0);
    }

    private static List<ForeldreansvarType> extractForeldreansvarType(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getPerson().getForeldreansvar();
    }

    private static OppholdstillatelseType extractOppholdstillatelseType(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getPerson().getOppholdstillatelse().get(0);
    }

    private static GironummerType extractGironummerType(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getPerson().getGironummer().get(0);
    }

    private static TilleggType extractTilleggType(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getPerson().getTillegg().get(0);
    }

    private static PostType extractPostType(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getPerson().getPost().get(0);
    }

    private static MigrasjonType extractmigrasjonType(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getPerson().getMigrasjon().get(0);
    }

    private static List<RelasjonType> extractRelasjonType(TpsPersonDokumentType tpsPersonDokumentType) {
        return tpsPersonDokumentType.getRelasjon();
    }

    private static PersonIdent buildPersonident(PersonIdentType personIdentType, PersonIdentStatusType personIdentStatusType) {
        return PersonIdent.builder()
                .id(personIdentType.getPersonIdent().trim())
                .type(personIdentType.getPersonIdenttype().trim())
                .status(personIdentStatusType.getPersonIdentStatus().trim())
                .fraDato(checkIfDateIsEmpty(personIdentType.getPersonIdentFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(personIdentType.getPersonIdentTilDato().trim()))
                .build();
    }

    private static PersonInfo buildPersonInfo(PersonInfoType personInfoType, PersonStatusType personStatusType) {
        return PersonInfo.builder()
                .kjoenn(personInfoType.getPersonKjonn().trim())
                .datoFoedt(checkIfDateIsEmpty(personInfoType.getPersonDatofodt().trim()))
                .foedtLand(personInfoType.getPersonFodtLand().trim())
                .foedtKommune(personInfoType.getPersonFodtKommune().trim())
                .status(personStatusType.getPersonStatus().trim())
                .build();
    }

    private static Navn buildNavn(NavnType navnType) {
        return Navn.builder()
                .forkortet(navnType.getForkortetNavn().trim())
                .slektsnavn(navnType.getSlektsNavn().trim())
                .fornavn(navnType.getForNavn().trim())
                .mellomnavn(navnType.getMellomNavn().trim())
                .slektsnavnUgift(navnType.getSlektsNavnugift().trim())
                .fraDato(checkIfDateIsEmpty(navnType.getNavnFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(navnType.getNavnTilDato().trim()))
                .build();
    }

    private static Sivilstand buildSivilstand(SivilstandType sivilstandType) {
        return Sivilstand.builder()
                .type(sivilstandType.getSivilstand().trim())
                .fraDato(checkIfDateIsEmpty(sivilstandType.getSivilstandFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(sivilstandType.getSivilstandTilDato().trim()))
                .build();
    }

    private static Statsborger buildStatsborger(StatsborgerType statsborgerType) {
        return Statsborger.builder()
                .land(statsborgerType.getStatsborger().trim())
                .fraDato(checkIfDateIsEmpty(statsborgerType.getStatsborgerFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(statsborgerType.getStatsborgerTilDato().trim()))
                .build();
    }

    private static Doedshistorikk buildDoedshistorikk(DodType dodType) {
        return Doedshistorikk.builder()
                .dato(checkIfDateIsEmpty(dodType.getDatoDod().trim()))
                .regDato(checkIfDateIsEmpty(dodType.getDodDatoReg().trim()))
                .build();
    }

    private static TelefonPrivat buildTelefonPrivat(TelefonPrivatType telefonPrivatType) {
        return TelefonPrivat.builder()
                .retningslinje(telefonPrivatType.getTlfPrivatRetningslinje().trim())
                .nummer(telefonPrivatType.getTlfPrivatNummer().trim())
                .fraDato(checkIfDateIsEmpty(telefonPrivatType.getTlfPrivatFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(telefonPrivatType.getTlfPrivatTilDato().trim()))
                .build();
    }

    private static TelefonJobb buildTelefonJobb(TelefonJobbType telefonJobbType) {
        return TelefonJobb.builder()
                .retningslinje(telefonJobbType.getTlfJobbRetningslinje().trim())
                .nummer(telefonJobbType.getTlfJobbNummer().trim())
                .fraDato(checkIfDateIsEmpty(telefonJobbType.getTlfJobbFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(telefonJobbType.getTlfJobbTilDato().trim()))
                .build();
    }

    private static TelefonMobil buildTelefonMobil(TelefonMobilType telefonMobilType) {
        return TelefonMobil.builder()
                .retningslinje(telefonMobilType.getTlfMobilRetningslinje().trim())
                .nummer(telefonMobilType.getTlfMobilNummer().trim())
                .fraDato(checkIfDateIsEmpty(telefonMobilType.getTlfMobilFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(telefonMobilType.getTlfMobilTilDato().trim()))
                .build();
    }

    private static Boadresse buildBoadresse(BoadresseType boadresseType) {
        return Boadresse.builder()
                .adresse(boadresseType.getBoAdresse().trim())
                .land(boadresseType.getBoKodeLand().trim())
                .kommune(boadresseType.getBoKommune().trim())
                .postnr(boadresseType.getBoPostnr().trim())
                .bydel(boadresseType.getBoBydel().trim())
                .offentligGateKode(boadresseType.getBooffaGateKode().trim())
                .offentligHusnr(boadresseType.getBooffaHusnr().trim())
                .offentligBokstav(boadresseType.getBooffaBokstav().trim())
                .offentligBolignr(boadresseType.getBooffaBolignr().trim())
                .matrikkelGardsnr(boadresseType.getBomatrGardsnr().trim())
                .matrikkelBruksnr(boadresseType.getBomatrBruksnr().trim())
                .matrikkelFestenr(boadresseType.getBomatrFestenr().trim())
                .matrikkelUndernr(boadresseType.getBomatrUndernr().trim())
                .fraDato(checkIfDateIsEmpty(boadresseType.getBoAdresseFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(boadresseType.getBoAdresseTilDato().trim()))
                .build();
    }

    private static PrioritertAdresse buildPrioritertAdresse(PrioritertadresseType prioritertadresseType) {
        return PrioritertAdresse.builder()
                .type(prioritertadresseType.getPrioritertAdresseType().trim())
                .fraDato(checkIfDateIsEmpty(prioritertadresseType.getPrioritertAdresseFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(prioritertadresseType.getPrioritertAdresseTilDato().trim()))
                .build();
    }

    private static Foreldreansvar buildForeldreansvar(ForeldreansvarType foreldreansvarType) {
        return Foreldreansvar.builder()
                .ident(foreldreansvarType.getForeldreAnsvar().trim())
                .fraDato(checkIfDateIsEmpty(foreldreansvarType.getForeldreAnsvarFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(foreldreansvarType.getForeldreAnsvarTilDato().trim()))
                .build();
    }

    private static Oppholdstillatelse buildOppholdstillatelse(OppholdstillatelseType oppholdstillatelseType) {
        return Oppholdstillatelse.builder()
                .status(oppholdstillatelseType.getOppholdsTillatelse().trim())
                .fraDato(checkIfDateIsEmpty(oppholdstillatelseType.getOppholdsTillatelseFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(oppholdstillatelseType.getOppholdsTillatelseTilDato().trim()))
                .build();
    }

    private static Giro buildGiro(GironummerType gironummerType) {
        return Giro.builder()
                .nummer(gironummerType.getGironummer().trim())
                .fraDato(checkIfDateIsEmpty(gironummerType.getGironummerFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(gironummerType.getGironummerTilDato().trim()))
                .build();
    }

    private static Tillegg buildTillegg(TilleggType tilleggType) {
        return Tillegg.builder()
                .adresse1(tilleggType.getTilleggAdresse1().trim())
                .adresse2(tilleggType.getTilleggAdresse2().trim())
                .adresse3(tilleggType.getTilleggAdresse3().trim())
                .postnr(tilleggType.getTilleggPostnr().trim())
                .datoTom(checkIfDateIsEmpty(tilleggType.getTilleggAdresseDatoTom().trim()))
                .kommunenr(tilleggType.getTilleggKommunenr().trim())
                .gateKode(tilleggType.getTilleggGateKode().trim())
                .husnummer(tilleggType.getTilleggHusnummer().trim())
                .husbokstav(tilleggType.getTilleggHusbokstav().trim())
                .bolignummer(tilleggType.getTilleggBolignummer().trim())
                .bydel(tilleggType.getTilleggBydel().trim())
                .postboksnr(tilleggType.getTilleggPostboksnr().trim())
                .postboksAnlegg(tilleggType.getTilleggPostboksAnlegg().trim())
                .fraDato(checkIfDateIsEmpty(tilleggType.getTilleggAdresseFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(tilleggType.getTilleggAdresseTilDato().trim()))
                .build();
    }

    private static Post buildPost(PostType postType) {
        return Post.builder()
                .adresse1(postType.getPostAdresse1().trim())
                .adresse2(postType.getPostAdresse2().trim())
                .adresse3(postType.getPostAdresse3().trim())
                .postnr(postType.getPostpostnr().trim())
                .postland(postType.getPostLand().trim())
                .fraDato(checkIfDateIsEmpty(postType.getPostAdresseFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(postType.getPostAdresseTilDato().trim()))
                .build();
    }

    private static Migrasjon buildMigrasjon(MigrasjonType migrasjonType) {
        return Migrasjon.builder()
                .type(migrasjonType.getMigrasjon().trim())
                .land(migrasjonType.getMigrasjonLand().trim())
                .fraDato(checkIfDateIsEmpty(migrasjonType.getMigrasjonFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(migrasjonType.getMigrasjonTilDato().trim()))
                .build();
    }

    private static Relasjon buildRelasjon(RelasjonType relasjonType) {
        return Relasjon.builder()
                .ident(relasjonType.getRelasjonIdent().trim())
                .type(relasjonType.getRelasjonIdentType().trim())
                .status(relasjonType.getRelasjonIdentStatus().trim())
                .rolle(relasjonType.getRelasjonRolle().trim())
                .fraDato(checkIfDateIsEmpty(relasjonType.getRelasjonFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(relasjonType.getRelasjonTilDato().trim()))
                .build();
    }

    private static LocalDate checkIfDateIsEmpty(String date) {
        if (date.isEmpty()) {
            return null;
        } else {
            return LocalDate.parse(date);
        }
    }
}
