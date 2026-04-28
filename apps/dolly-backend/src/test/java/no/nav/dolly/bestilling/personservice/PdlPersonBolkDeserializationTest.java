package no.nav.dolly.bestilling.personservice;

import no.nav.dolly.domain.PdlPersonBolk;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class PdlPersonBolkDeserializationTest {

    private static final String PDL_RESPONSE = """
            {
              "data": {
                "hentIdenterBolk": [
                  {
                    "ident": "26478028902",
                    "identer": [
                      {"ident": "2992470217317", "gruppe": "AKTORID", "historisk": false},
                      {"ident": "26478028902", "gruppe": "FOLKEREGISTERIDENT", "historisk": false}
                    ],
                    "code": "ok"
                  }
                ],
                "hentGeografiskTilknytningBolk": [
                  {
                    "ident": "26478028902",
                    "geografiskTilknytning": {
                      "gtType": "KOMMUNE",
                      "gtLand": null,
                      "gtKommune": "1554",
                      "gtBydel": null,
                      "regel": "2"
                    },
                    "code": "ok"
                  }
                ],
                "hentPersonBolk": [
                  {
                    "ident": "26478028902",
                    "person": {
                      "falskIdentitet": null,
                      "bostedsadresse": [],
                      "oppholdsadresse": [],
                      "deltBosted": [],
                      "forelderBarnRelasjon": [],
                      "kontaktadresse": [],
                      "kontaktinformasjonForDoedsbo": [],
                      "utenlandskIdentifikasjonsnummer": [],
                      "adressebeskyttelse": [],
                      "foedselsdato": [
                        {
                          "foedselsaar": 1980,
                          "foedselsdato": "1980-07-26",
                          "folkeregistermetadata": null,
                          "metadata": {
                            "endringer": [],
                            "historisk": false,
                            "master": "FREG",
                            "opplysningsId": "test-id"
                          }
                        }
                      ],
                      "foedested": [],
                      "doedfoedtBarn": [],
                      "doedsfall": [],
                      "kjoenn": [
                        {
                          "kjoenn": "MANN",
                          "folkeregistermetadata": null,
                          "metadata": null
                        }
                      ],
                      "navn": [
                        {
                          "fornavn": "TEST",
                          "mellomnavn": null,
                          "etternavn": "TESTESEN",
                          "forkortetNavn": null,
                          "originaltNavn": null,
                          "gyldigFraOgMed": "1980-07-26",
                          "folkeregistermetadata": null,
                          "metadata": null
                        }
                      ],
                      "folkeregisterpersonstatus": [
                        {
                          "status": "bosatt",
                          "forenkletStatus": "bosattEtterFolkeregisterloven",
                          "folkeregistermetadata": null,
                          "metadata": null
                        }
                      ],
                      "identitetsgrunnlag": [],
                      "tilrettelagtKommunikasjon": [],
                      "folkeregisteridentifikator": [],
                      "statsborgerskap": [],
                      "sikkerhetstiltak": [],
                      "opphold": [],
                      "sivilstand": [],
                      "telefonnummer": [],
                      "innflyttingTilNorge": [],
                      "utflyttingFraNorge": [],
                      "vergemaalEllerFremtidsfullmakt": [],
                      "foreldreansvar": [],
                      "fullmakt": []
                    },
                    "code": "ok"
                  }
                ]
              },
              "extensions": {
                "warnings": []
              }
            }
            """;

    @Test
    void shouldDeserializePdlPersonBolk() {
        var jsonMapper = JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();

        PdlPersonBolk result = jsonMapper.readValue(PDL_RESPONSE, PdlPersonBolk.class);

        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getHentPersonBolk()).hasSize(1);
        assertThat(result.getData().getHentPersonBolk().get(0).getIdent()).isEqualTo("26478028902");
        assertThat(result.getData().getHentPersonBolk().get(0).getPerson()).isNotNull();
    }

    @Test
    void shouldDeserializePersonStatusEnum() {
        var jsonMapper = JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();

        PdlPersonBolk result = jsonMapper.readValue(PDL_RESPONSE, PdlPersonBolk.class);

        var person = result.getData().getHentPersonBolk().get(0).getPerson();
        assertThat(person.getFolkeregisterpersonstatus()).hasSize(1);
    }
}

