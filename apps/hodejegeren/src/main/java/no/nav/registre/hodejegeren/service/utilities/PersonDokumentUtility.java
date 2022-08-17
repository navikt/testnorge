package no.nav.registre.hodejegeren.service.utilities;

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
import no.nav.testnav.libs.domain.dto.namespacetps.BoadresseType;
import no.nav.testnav.libs.domain.dto.namespacetps.DodType;
import no.nav.testnav.libs.domain.dto.namespacetps.ForeldreansvarType;
import no.nav.testnav.libs.domain.dto.namespacetps.GironummerType;
import no.nav.testnav.libs.domain.dto.namespacetps.MigrasjonType;
import no.nav.testnav.libs.domain.dto.namespacetps.NavnType;
import no.nav.testnav.libs.domain.dto.namespacetps.OppholdstillatelseType;
import no.nav.testnav.libs.domain.dto.namespacetps.PersonIdentStatusType;
import no.nav.testnav.libs.domain.dto.namespacetps.PersonIdentType;
import no.nav.testnav.libs.domain.dto.namespacetps.PersonInfoType;
import no.nav.testnav.libs.domain.dto.namespacetps.PersonStatusType;
import no.nav.testnav.libs.domain.dto.namespacetps.PostType;
import no.nav.testnav.libs.domain.dto.namespacetps.PrioritertadresseType;
import no.nav.testnav.libs.domain.dto.namespacetps.RelasjonType;
import no.nav.testnav.libs.domain.dto.namespacetps.SivilstandType;
import no.nav.testnav.libs.domain.dto.namespacetps.StatsborgerType;
import no.nav.testnav.libs.domain.dto.namespacetps.TelefonJobbType;
import no.nav.testnav.libs.domain.dto.namespacetps.TelefonMobilType;
import no.nav.testnav.libs.domain.dto.namespacetps.TelefonPrivatType;
import no.nav.testnav.libs.domain.dto.namespacetps.TelefonType;
import no.nav.testnav.libs.domain.dto.namespacetps.TilleggType;
import no.nav.testnav.libs.domain.dto.namespacetps.TpsPersonDokumentType;

public class PersonDokumentUtility {

    private PersonDokumentUtility() {
    }

    public static PersonDokumentWrapper convertToPersonDokumentWrapper(TpsPersonDokumentType tpsPersonDokumentType) {
        List<Foreldreansvar> foreldreansvar = new ArrayList<>();
        List<Relasjon> relasjoner = new ArrayList<>();

        var personIdentType = extractPersonIdent(tpsPersonDokumentType);
        var personIdentStatusType = extractPersonIdentStatus(tpsPersonDokumentType);
        var personInfoType = extractPersonInfoType(tpsPersonDokumentType);
        var personStatusType = extractPersonStatusType(tpsPersonDokumentType);
        var navnType = extractNavnType(tpsPersonDokumentType);
        var sivilstandType = extractSivilstandType(tpsPersonDokumentType);
        var statsborgerType = extractStatsborgerType(tpsPersonDokumentType);
        var dodType = extractDodType(tpsPersonDokumentType);
        var telefonPrivatType = extractTelefonPrivatType(tpsPersonDokumentType);
        var telefonJobbType = extractTelefonJobbType(tpsPersonDokumentType);
        var telefonMobilType = extractTelefonmobilType(tpsPersonDokumentType);
        var boadresseType = extractBoadresseType(tpsPersonDokumentType);
        var prioritertadresseType = extractPrioritertadresseType(tpsPersonDokumentType);

        var foreldreansvarTypeListe = extractForeldreansvarType(tpsPersonDokumentType);
        for (var foreldreansvarType : foreldreansvarTypeListe) {
            var foreldreansvaret = buildForeldreansvar(foreldreansvarType);
            if (!foreldreansvaret.getIdent().isEmpty()) {
                foreldreansvar.add(foreldreansvaret);
            }
        }

        var oppholdstillatelseType = extractOppholdstillatelseType(tpsPersonDokumentType);
        var gironummerType = extractGironummerType(tpsPersonDokumentType);
        var tilleggType = extractTilleggType(tpsPersonDokumentType);
        var postType = extractPostType(tpsPersonDokumentType);
        var migrasjonType = extractmigrasjonType(tpsPersonDokumentType);

        var relasjonTypeListe = extractRelasjonType(tpsPersonDokumentType);
        for (var relasjonType : relasjonTypeListe) {
            var relasjonen = buildRelasjon(relasjonType);
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
        return (PersonIdentType) extractIfNotEmpty(tpsPersonDokumentType.getPerson().getPersonIdent());
    }

    private static PersonIdentStatusType extractPersonIdentStatus(TpsPersonDokumentType tpsPersonDokumentType) {
        return (PersonIdentStatusType) extractIfNotEmpty(tpsPersonDokumentType.getPerson().getPersonIdentstatus());
    }

    private static PersonInfoType extractPersonInfoType(TpsPersonDokumentType tpsPersonDokumentType) {
        return (PersonInfoType) extractIfNotEmpty(tpsPersonDokumentType.getPerson().getPersonInfo());
    }

    private static PersonStatusType extractPersonStatusType(TpsPersonDokumentType tpsPersonDokumentType) {
        return (PersonStatusType) extractIfNotEmpty(tpsPersonDokumentType.getPerson().getPersonStatus());
    }

    private static NavnType extractNavnType(TpsPersonDokumentType tpsPersonDokumentType) {
        return (NavnType) extractIfNotEmpty(tpsPersonDokumentType.getPerson().getNavn());
    }

    private static SivilstandType extractSivilstandType(TpsPersonDokumentType tpsPersonDokumentType) {
        return (SivilstandType) extractIfNotEmpty(tpsPersonDokumentType.getPerson().getSivilstand());
    }

    private static StatsborgerType extractStatsborgerType(TpsPersonDokumentType tpsPersonDokumentType) {
        return (StatsborgerType) extractIfNotEmpty(tpsPersonDokumentType.getPerson().getStatsborger());
    }

    private static DodType extractDodType(TpsPersonDokumentType tpsPersonDokumentType) {
        return (DodType) extractIfNotEmpty(tpsPersonDokumentType.getPerson().getDod());
    }

    private static TelefonPrivatType extractTelefonPrivatType(TpsPersonDokumentType tpsPersonDokumentType) {
        TelefonType telefonType = (TelefonType) extractIfNotEmpty(tpsPersonDokumentType.getPerson().getTelefon());
        if (telefonType != null) {
            return telefonType.getTlfPrivat();
        } else {
            return null;
        }
    }

    private static TelefonJobbType extractTelefonJobbType(TpsPersonDokumentType tpsPersonDokumentType) {
        TelefonType telefonType = (TelefonType) extractIfNotEmpty(tpsPersonDokumentType.getPerson().getTelefon());
        if (telefonType != null) {
            return telefonType.getTlfJobb();
        } else {
            return null;
        }
    }

    private static TelefonMobilType extractTelefonmobilType(TpsPersonDokumentType tpsPersonDokumentType) {
        TelefonType telefonType = (TelefonType) extractIfNotEmpty(tpsPersonDokumentType.getPerson().getTelefon());
        if (telefonType != null) {
            return telefonType.getTlfMobil();
        } else {
            return null;
        }
    }

    private static BoadresseType extractBoadresseType(TpsPersonDokumentType tpsPersonDokumentType) {
        return (BoadresseType) extractIfNotEmpty(tpsPersonDokumentType.getPerson().getBoadresse());
    }

    private static PrioritertadresseType extractPrioritertadresseType(TpsPersonDokumentType tpsPersonDokumentType) {
        return (PrioritertadresseType) extractIfNotEmpty(tpsPersonDokumentType.getPerson().getPrioritertadresse());
    }

    private static List<ForeldreansvarType> extractForeldreansvarType(TpsPersonDokumentType tpsPersonDokumentType) {
        var foreldreansvar = tpsPersonDokumentType.getPerson().getForeldreansvar();
        if (foreldreansvar == null) {
            return new ArrayList<>();
        }
        return foreldreansvar;
    }

    private static OppholdstillatelseType extractOppholdstillatelseType(TpsPersonDokumentType tpsPersonDokumentType) {
        return (OppholdstillatelseType) extractIfNotEmpty(tpsPersonDokumentType.getPerson().getOppholdstillatelse());
    }

    private static GironummerType extractGironummerType(TpsPersonDokumentType tpsPersonDokumentType) {
        return (GironummerType) extractIfNotEmpty(tpsPersonDokumentType.getPerson().getGironummer());
    }

    private static TilleggType extractTilleggType(TpsPersonDokumentType tpsPersonDokumentType) {
        return (TilleggType) extractIfNotEmpty(tpsPersonDokumentType.getPerson().getTillegg());
    }

    private static PostType extractPostType(TpsPersonDokumentType tpsPersonDokumentType) {
        return (PostType) extractIfNotEmpty(tpsPersonDokumentType.getPerson().getPost());
    }

    private static MigrasjonType extractmigrasjonType(TpsPersonDokumentType tpsPersonDokumentType) {
        return (MigrasjonType) extractIfNotEmpty(tpsPersonDokumentType.getPerson().getMigrasjon());
    }

    private static List<RelasjonType> extractRelasjonType(TpsPersonDokumentType tpsPersonDokumentType) {
        var relasjoner = tpsPersonDokumentType.getRelasjon();
        if (relasjoner == null) {
            return new ArrayList<>();
        }
        return relasjoner;
    }

    private static Object extractIfNotEmpty(List<?> list) {
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    private static PersonIdent buildPersonident(
            PersonIdentType personIdentType,
            PersonIdentStatusType personIdentStatusType
    ) {
        PersonIdent personIdent = PersonIdent.builder().build();
        if (personIdentType != null) {
            personIdent.setId(personIdentType.getPersonIdent().trim());
            personIdent.setType(personIdentType.getPersonIdenttype().trim());
            personIdent.setFraDato(checkIfDateIsEmpty(personIdentType.getPersonIdentFraDato().trim()));
            personIdent.setTilDato(checkIfDateIsEmpty(personIdentType.getPersonIdentTilDato().trim()));
        }
        if (personIdentStatusType != null) {
            personIdent.setStatus(personIdentStatusType.getPersonIdentStatus().trim());
        }
        return personIdent;
    }

    private static PersonInfo buildPersonInfo(
            PersonInfoType personInfoType,
            PersonStatusType personStatusType
    ) {
        PersonInfo personInfo = PersonInfo.builder().build();
        if (personInfoType != null) {
            personInfo.setKjoenn(personInfoType.getPersonKjonn().trim());
            personInfo.setDatoFoedt(checkIfDateIsEmpty(personInfoType.getPersonDatofodt().trim()));
            personInfo.setFoedtLand(personInfoType.getPersonFodtLand().trim());
            personInfo.setFoedtKommune(personInfoType.getPersonFodtKommune().trim());
        }
        if (personStatusType != null) {
            personInfo.setStatus(personStatusType.getPersonStatus().trim());
        }
        return personInfo;
    }

    private static Navn buildNavn(NavnType navnType) {
        if (navnType == null) {
            return Navn.builder().build();
        }
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
        if (sivilstandType == null) {
            return Sivilstand.builder().build();
        }
        return Sivilstand.builder()
                .type(sivilstandType.getSivilstand().trim())
                .fraDato(checkIfDateIsEmpty(sivilstandType.getSivilstandFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(sivilstandType.getSivilstandTilDato().trim()))
                .build();
    }

    private static Statsborger buildStatsborger(StatsborgerType statsborgerType) {
        if (statsborgerType == null) {
            return Statsborger.builder().build();
        }
        return Statsborger.builder()
                .land(statsborgerType.getStatsborger().trim())
                .fraDato(checkIfDateIsEmpty(statsborgerType.getStatsborgerFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(statsborgerType.getStatsborgerTilDato().trim()))
                .build();
    }

    private static Doedshistorikk buildDoedshistorikk(DodType dodType) {
        if (dodType == null) {
            return Doedshistorikk.builder().build();
        }
        return Doedshistorikk.builder()
                .dato(checkIfDateIsEmpty(dodType.getDatoDod().trim()))
                .regDato(checkIfDateIsEmpty(dodType.getDodDatoReg().trim()))
                .build();
    }

    private static TelefonPrivat buildTelefonPrivat(TelefonPrivatType telefonPrivatType) {
        if (telefonPrivatType == null) {
            return TelefonPrivat.builder().build();
        }
        return TelefonPrivat.builder()
                .retningslinje(telefonPrivatType.getTlfPrivatRetningslinje().trim())
                .nummer(telefonPrivatType.getTlfPrivatNummer().trim())
                .fraDato(checkIfDateIsEmpty(telefonPrivatType.getTlfPrivatFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(telefonPrivatType.getTlfPrivatTilDato().trim()))
                .build();
    }

    private static TelefonJobb buildTelefonJobb(TelefonJobbType telefonJobbType) {
        if (telefonJobbType == null) {
            return TelefonJobb.builder().build();
        }
        return TelefonJobb.builder()
                .retningslinje(telefonJobbType.getTlfJobbRetningslinje().trim())
                .nummer(telefonJobbType.getTlfJobbNummer().trim())
                .fraDato(checkIfDateIsEmpty(telefonJobbType.getTlfJobbFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(telefonJobbType.getTlfJobbTilDato().trim()))
                .build();
    }

    private static TelefonMobil buildTelefonMobil(TelefonMobilType telefonMobilType) {
        if (telefonMobilType == null) {
            return TelefonMobil.builder().build();
        }
        return TelefonMobil.builder()
                .retningslinje(telefonMobilType.getTlfMobilRetningslinje().trim())
                .nummer(telefonMobilType.getTlfMobilNummer().trim())
                .fraDato(checkIfDateIsEmpty(telefonMobilType.getTlfMobilFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(telefonMobilType.getTlfMobilTilDato().trim()))
                .build();
    }

    private static Boadresse buildBoadresse(BoadresseType boadresseType) {
        if (boadresseType == null) {
            return Boadresse.builder().build();
        }
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
        if (prioritertadresseType == null) {
            return PrioritertAdresse.builder().build();
        }
        return PrioritertAdresse.builder()
                .type(prioritertadresseType.getPrioritertAdresseType().trim())
                .fraDato(checkIfDateIsEmpty(prioritertadresseType.getPrioritertAdresseFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(prioritertadresseType.getPrioritertAdresseTilDato().trim()))
                .build();
    }

    private static Foreldreansvar buildForeldreansvar(ForeldreansvarType foreldreansvarType) {
        if (foreldreansvarType == null) {
            return Foreldreansvar.builder().build();
        }
        return Foreldreansvar.builder()
                .ident(foreldreansvarType.getForeldreAnsvar().trim())
                .fraDato(checkIfDateIsEmpty(foreldreansvarType.getForeldreAnsvarFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(foreldreansvarType.getForeldreAnsvarTilDato().trim()))
                .build();
    }

    private static Oppholdstillatelse buildOppholdstillatelse(OppholdstillatelseType oppholdstillatelseType) {
        if (oppholdstillatelseType == null) {
            return Oppholdstillatelse.builder().build();
        }
        return Oppholdstillatelse.builder()
                .status(oppholdstillatelseType.getOppholdsTillatelse().trim())
                .fraDato(checkIfDateIsEmpty(oppholdstillatelseType.getOppholdsTillatelseFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(oppholdstillatelseType.getOppholdsTillatelseTilDato().trim()))
                .build();
    }

    private static Giro buildGiro(GironummerType gironummerType) {
        if (gironummerType == null) {
            return Giro.builder().build();
        }
        return Giro.builder()
                .nummer(gironummerType.getGironummer().trim())
                .fraDato(checkIfDateIsEmpty(gironummerType.getGironummerFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(gironummerType.getGironummerTilDato().trim()))
                .build();
    }

    private static Tillegg buildTillegg(TilleggType tilleggType) {
        if (tilleggType == null) {
            return Tillegg.builder().build();
        }
        return Tillegg.builder()
                .adresse1(tilleggType.getTilleggAdresse1().trim())
                .adresse2(tilleggType.getTilleggAdresse2().trim())
                .adresse3(tilleggType.getTilleggAdresse3().trim())
                .postnr(tilleggType.getTilleggPostnr().trim())
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
        if (postType == null) {
            return Post.builder().build();
        }
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
        if (migrasjonType == null) {
            return Migrasjon.builder().build();
        }
        return Migrasjon.builder()
                .type(migrasjonType.getMigrasjon().trim())
                .land(migrasjonType.getMigrasjonLand().trim())
                .fraDato(checkIfDateIsEmpty(migrasjonType.getMigrasjonFraDato().trim()))
                .tilDato(checkIfDateIsEmpty(migrasjonType.getMigrasjonTilDato().trim()))
                .build();
    }

    private static Relasjon buildRelasjon(RelasjonType relasjonType) {
        if (relasjonType == null) {
            return Relasjon.builder().build();
        }
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
