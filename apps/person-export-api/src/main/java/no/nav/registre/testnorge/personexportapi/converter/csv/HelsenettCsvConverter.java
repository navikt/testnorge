package no.nav.registre.testnorge.personexportapi.converter.csv;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.csvconverter.CsvConverter;
import no.nav.registre.testnorge.libs.csvconverter.CsvHeader;
import no.nav.registre.testnorge.libs.csvconverter.ObjectConverter;
import no.nav.registre.testnorge.libs.csvconverter.RowConverter;
import no.nav.registre.testnorge.personexportapi.consumer.dto.PersonStatusMapper;
import no.nav.registre.testnorge.personexportapi.consumer.kodeverk.KodeverkConsumer;
import no.nav.registre.testnorge.personexportapi.domain.Person;

@Slf4j
@Service
@RequiredArgsConstructor
public class HelsenettCsvConverter extends CsvConverter<Person> {

    private static final String LANDKODER = "Landkoder";

    private final KodeverkConsumer kodeverkConsumer;

    private enum Headers implements CsvHeader {
        FNR("FNR"),
        KORTNAVN("KORTNAVN"),
        FORNAVN("FORNAVN"),
        MELLOMNAVN("MELLOMNAVN"),
        ETTERNAVN("ETTERNAVN"),
        PIKENAVN("PIKENAVN"),
        KJONN("KJONN"),
        BESKR_KJONN("BESKR_KJONN"),
        FODT_DATO("FODT_DATO"),
        DOD_DATO("DOD_DATO"),
        PERSONSTATUS("PERSONSTATUS"),
        BESKR_PERSONSTATUS("BESKR_PERSONSTATUS"),
        PERSONSTATUS_DATO("PERSONSTATUS_DATO"),
        SIVILSTAND("SIVILSTAND"),
        BESKR_SIVILSTAND("BESKR_SIVILSTAND"),
        SIVILSTAND_DATO("SIVILSTAND_DATO"),
        STATSBORGERLAND("STATSBORGERLAND"),
        STATSBORGER_DATO("STATSBORGER_DATO"),
        GATENAVN("GATENAVN"),
        CO_ADRESSE("CO_ADRESSE"),
        HUSNR("HUSNR"),
        HUSBOKSTAV("HUSBOKSTAV"),
        BOLIGNR("BOLIGNR"),
        GATENR("GATENR"),
        GARDSNR("GARDSNR"),
        BRUKSNR("BRUKSNR"),
        STEDSNAVN("STEDSNAVN"),
        KOMMUNENR("KOMMUNENR"),
        KOMMUNE("KOMMUNE"),
        DATO_ADR_FRA("DATO_ADR_FRA"),
        ADRESSEKODE("ADRESSEKODE"),
        REG_DATO_ADR_KD("REG_DATO_ADR_KD"),
        POSTADR_1("POSTADR_1"),
        POSTADR_2("POSTADR_2"),
        POSTADR_3("POSTADR_3"),
        POSTNR("POSTNR"),
        POSTSTED("POSTSTED"),
        POSTADR_REG_DATO("POSTADR_REG_DATO"),
        POSTADR_LAND("POSTADR_LAND"),
        BYDEL("BYDEL"),
        BYDELS_NAVN("BYDELS_NAVN"),
        INNVANDRET_FRA_LAND("INNVANDRET_FRA_LAND"),
        INNVANDRET_DATO("INNVANDRET_DATO"),
        UTVANDRET_TIL_LAND("UTVANDRET_TIL_LAND"),
        UTVANDRET_DATO("UTVANDRET_DATO"),
        SKOLEKRETS("SKOLEKRETS"),
        VALGKRETS("VALGKRETS"),
        GRUNNKRETS("GRUNNKRETS");

        private final String header;

        Headers(String header) {
            this.header = header;
        }

        @Override
        public String getValue() {
            return header;
        }
    }

    @Override
    protected RowConverter<Person> getRowConverter() {
        throw new UnsupportedOperationException("Import av person ikke stottet");
    }

    @Override
    protected ObjectConverter<Person> getObjectConverter() {
        return person -> {
            Map<String, Object> map = new HashMap<>();
            map.put(Headers.FNR.getValue(), person.getIdent());
            map.put(Headers.FORNAVN.getValue(), person.getFornavn());
            map.put(Headers.MELLOMNAVN.getValue(), person.getMellomnavn());
            map.put(Headers.ETTERNAVN.getValue(), person.getEtternavn());
            map.put(Headers.PERSONSTATUS.getValue(), person.getPersonstatus());
            map.put(Headers.BESKR_PERSONSTATUS.getValue(), PersonStatusMapper.getPersonstatus(person));
            map.put(Headers.ADRESSEKODE.getValue(), person.getAdressetype());
            map.put(Headers.GATENAVN.getValue(), person.getGatenavn());
            map.put(Headers.GATENR.getValue(), person.getGatenr());
            map.put(Headers.HUSNR.getValue(), person.getHusnr());
            map.put(Headers.HUSBOKSTAV.getValue(), person.getHusbokstav());
            map.put(Headers.STEDSNAVN.getValue(), person.getGaardsnavn());
            map.put(Headers.GARDSNR.getValue(), person.getGaardsnr());
            map.put(Headers.BRUKSNR.getValue(), person.getBruksnr());
            map.put(Headers.BOLIGNR.getValue(), person.getBolignr());
            map.put(Headers.CO_ADRESSE.getValue(), person.getTilleggsadresse());
            map.put(Headers.POSTNR.getValue(), person.getPostnummer());
            map.put(Headers.POSTSTED.getValue(), kodeverkConsumer.getKodeverkOppslag("Postnummer", person.getPostnummer()));
            map.put(Headers.KOMMUNENR.getValue(), person.getKommunenr());
            map.put(Headers.KOMMUNE.getValue(), kodeverkConsumer.getKodeverkOppslag("Kommuner", person.getKommunenr()));
            map.put(Headers.DATO_ADR_FRA.getValue(), person.getFlyttedato());
            map.put(Headers.POSTADR_1.getValue(), person.getAdresse1());
            map.put(Headers.POSTADR_2.getValue(), person.getAdresse2());
            map.put(Headers.POSTADR_3.getValue(), person.getAdresse3());
            map.put(Headers.POSTADR_LAND.getValue(), kodeverkConsumer.getKodeverkOppslag(LANDKODER, person.getPostadrLand()));
            map.put(Headers.FODT_DATO.getValue(), person.getFoedselsdato());
            map.put(Headers.KJONN.getValue(), person.getKjoenn());
            map.put(Headers.BESKR_KJONN.getValue(), kodeverkConsumer.getKodeverkOppslag("Kjønnstyper", person.getKjoennBeskrivelse()));
            map.put(Headers.SIVILSTAND.getValue(), person.getSivilstand());
            map.put(Headers.BESKR_SIVILSTAND.getValue(), kodeverkConsumer.getKodeverkOppslag("Sivilstander", person.getSivilstandBeskrivelse()));
            map.put(Headers.SIVILSTAND_DATO.getValue(), person.getSivilstandRegdato());
            map.put(Headers.STATSBORGERLAND.getValue(), kodeverkConsumer.getKodeverkOppslag(LANDKODER, person.getStatsborgerskap()));
            map.put(Headers.STATSBORGER_DATO.getValue(), person.getStatsborgerskapRegdato());
            map.put(Headers.INNVANDRET_FRA_LAND.getValue(), kodeverkConsumer.getKodeverkOppslag(LANDKODER, person.getInnvandretFraLand()));
            map.put(Headers.INNVANDRET_DATO.getValue(), person.getInvandretFraLandFlyttedato());
            map.put(Headers.UTVANDRET_TIL_LAND.getValue(), kodeverkConsumer.getKodeverkOppslag(LANDKODER, person.getUtvandretTilLand()));
            map.put(Headers.UTVANDRET_DATO.getValue(), person.getInvandretFraLandFlyttedato());

            log.info(person.toString());
            return map;
        };
    }

    @Override
    protected CsvHeader[] getHeaders() {
        return Headers.values();
    }
}
