package no.nav.testnav.apps.tenorsearchservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class TenorOrganisasjonSelectOptions {

    @Getter
    @AllArgsConstructor
    public enum OrganisasjonForm implements LabelEnum {
        AAFY("Underenhet til ikke-næringsdrivende"),
        ADOS("Administrativ enhet - offentlig sektor"),
        ANNA("Annen juridisk person"),
        ANS("Ansvarlig selskap med solidarisk ansvar"),
        AS("Aksjeselskap"),
        ASA("Allmennaksjeselskap"),
        BA("Selskap med begrenset ansvar"),
        BBL("Boligbyggelag"),
        BEDR("Underenhet til næringsdrivende og offentlig forvaltning"),
        BO("Andre bo"),
        BRL("Borettslag"),
        DA("Ansvarlig selskap med delt ansvar"),
        ENK("Enkeltpersonforetak"),
        EØFG("Europeisk økonomisk foretaksgruppe"),
        ESEK("Eierseksjonssameie"),
        FKF("Fylkeskommunalt foretak"),
        FLI("Forening/lag/innretning"),
        FYLK("Fylkeskommune"),
        GFS("Gjensidig forsikringsselskap"),
        IKJP("Andre ikke-juridiske personer"),
        IKS("Interkommunalt selskap"),
        KBO("Konkursbo"),
        KF("Kommunalt foretak"),
        KIRK("Den norske kirke"),
        KOMM("Kommune"),
        KS("Kommandittselskap"),
        KTRF("Kontorfellesskap"),
        NUF("Norskregistrert utenlandsk foretak"),
        OPMV("Særskilt oppdelt enhet, jf. mval. § 2-2"),
        ORGL("Organisasjonsledd"),
        PERS("Andre enkeltpersoner som registreres i tilknyttet register"),
        PK("Pensjonskasse"),
        PRE("Partrederi"),
        SA("Samvirkeforetak"),
        SAM("Tingsrettslig sameie"),
        SE("Europeisk selskap"),
        SF("Statsforetak"),
        SPA("Sparebank"),
        STAT("Staten"),
        STI("Stiftelse"),
        SÆR("Annet foretak iflg. særskilt lov"),
        TVAM("Tvangsregisrert for MVA"),
        UTLA("Utenlandsk enhet"),
        VPFO("Verdipapirfond");

        private final String label;

        @Override
        public String getName() {
            return name();
        }

        @Override
        public String getLabel() {
            return label;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum EnhetStatus implements LabelEnum {
        OPPL("Oppløst"),
        KONK("Åpnet konkurs");

        private final String label;

        @Override
        public String getName() {
            return name();
        }

        @Override
        public String getLabel() {
            return label;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Grunnlagsdata implements LabelEnum {
        AKSJESPAREKONTO("Aksjesparekonto"),
        BARNEPASS("Betaling for pass og stell av barn"),
        BETALINGER_NAERINGSDRIVENDE("Betalinger til selvstendig næringsdrivende"),
        BOLIGSAMEIE("Boligsameie"),
        BOLIGSELSKAP("Boligselskap"),
        BSU("Boligsparing for ungdom "),
        CRSFATCA("Internasjonal rapportering"),
        DROSJESENTRALER("Drosjetjenester"),
        EIENDOMSUTLEIE("Utleie av fast eiendom fra formidlingsselskap"),
        ENOVA("Enova"),
        FAGFORENINGSKONTINGENT("Fagforeningskontingent"),
        FINANSPRODUKTER("Finansprodukter"),
        FONDSKONTO("Fondskonto"),
        GAVER_TIL_ORG("Gaver til organisasjoner"),
        GODTGJOERELSE_OPPHAVSMANN("Godtgjøring til opphaver til åndsverk"),
        INDIVIDUELLE_PENSJONSORDNINGER("Individuelle pensjonsordninger"),
        KOP_EGG("Kjøp fra primærnæring - egg"),
        KOP_FISK("Kjøp fra primærnæring - fisk"),
        KOP_JORD_HAGEBRUK("Kjøp fra primærnæring - jord- og hagebruk"),
        KOP_KORN("Kjøp fra primærnæring - korn"),
        KOP_MELK("Kjøp fra primærnæring - melk"),
        KOP_PELSDYRSKINN("Kjøp fra primærnæring - pelsdyr"),
        KOP_SLAKT("Kjøp fra primærnæring - slakt"),
        KOP_TILSKUDD("Tilskudd innen primærnæringene"),
        KOP_TOEMMER("Kjøp fra primærnæring - tømmer"),
        LIVSFORSIKRING("Livsforsikring"),
        SALDO_RENTE("Innskudd, utlån og renter m.v."),
        SKADEFORSIKRING("Skadeforsikring"),
        SKATTEFRIE_UTBETALINGER("Skattefrie utbetalinger fra offentlig myndighet"),
        SKATTEPLIKTIG_KUNDEUTBYTTE("Skattepliktig kundeutbytte"),
        SKE_A_ORDNING_INNTEKTSMOTTAKER("a-melding"),
        TILDELTE_OPSJONER("Opsjoner i oppstartsselskap"),
        TILSKUDD_FORSK("Tilskudd til forskning eller yrkesopplæring"),
        UNDERHOLDSBIDRAG("Underholdsbidrag"),
        VERDIPAPIRFOND("Verdipapirfond");

        private final String label;

        @Override
        public String getName() {
            return name();
        }

        @Override
        public String getLabel() {
            return label;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum ArbeidsforholdType implements LabelEnum {
        OrdinaertArbeidsforhold("ordinært"),
        MaritimtArbeidsforhold("maritimt"),
        FrilanserOppdragstakerHonorarPersonerMm("frilanser med mer");

        private final String label;

        @Override
        public String getName() {
            return name();
        }

        @Override
        public String getLabel() {
            return label;
        }
    }

}