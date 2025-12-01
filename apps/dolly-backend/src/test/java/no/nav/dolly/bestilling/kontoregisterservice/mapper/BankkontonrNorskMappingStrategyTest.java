package no.nav.dolly.bestilling.kontoregisterservice.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.kontoregister.v1.OppdaterKontoRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;

class BankkontonrNorskMappingStrategyTest {

    private static final String IDENT = "12345678901";
    private static final String KONTONUMMER = "1234567890";

    private MapperFacade mapperFacade;

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(
                new BankkontonrNorskMappingStrategy(),
                new BankkontonrUtlandskMappingStrategy());
    }

    @Test
    void testBankkontoMapping() {
        var bankkonto1 = new BankkontonrUtlandDTO();

        bankkonto1.setTilfeldigKontonummer(true);
        bankkonto1.setLandkode("SE");

        var mapped1 = mapperFacade.map(bankkonto1, OppdaterKontoRequestDTO.class);

        assertThat(mapped1.getKontonummer(), is(not(blankOrNullString())));
        assertThat(mapped1.getKontonummer(), startsWith("SE"));

        var bankkonto2 = new BankkontonrUtlandDTO();

        bankkonto2.setKontonummer("123");
        bankkonto2.setLandkode("SWE");
        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", IDENT);

        var mapped2 = mapperFacade.map(bankkonto2, OppdaterKontoRequestDTO.class, context);

        assertThat(mapped2.getKontonummer(), is(equalTo("123")));
        assertThat(mapped2.getUtenlandskKonto().getBankLandkode(), is(equalTo("SE")));
        assertThat(mapped2.getKontohaver(), is(equalTo(IDENT)));
    }

    @Test
    void oppdaterUtenlandskbankkonto() {

        var dto = BankkontonrUtlandDTO.builder()
                .kontonummer(KONTONUMMER)
                .swift("SHEDSE22")
                .valuta("SEK")
                .landkode("SE")
                .banknavn("banknavn")
                .bankAdresse1("address1")
                .bankAdresse2("address2")
                .bankAdresse3("address3")
                .build();

        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", IDENT);

        var target = mapperFacade.map(dto, OppdaterKontoRequestDTO.class, context);

        assertThat(target.getUtenlandskKonto().getSwiftBicKode(), is(equalTo("SHEDSE22")));
        assertThat(target.getUtenlandskKonto().getValutakode(), is(equalTo("SEK")));
        assertThat(target.getUtenlandskKonto().getBankLandkode(), is(equalTo("SE")));
        assertThat(target.getUtenlandskKonto().getBanknavn(), is(equalTo("banknavn")));
        assertThat(target.getUtenlandskKonto().getBankadresse1(), is(equalTo("address1")));
        assertThat(target.getUtenlandskKonto().getBankadresse2(), is(equalTo("address2")));
        assertThat(target.getUtenlandskKonto().getBankadresse3(), is(equalTo("address3")));
        assertThat(target.getKontohaver(), is(equalTo(IDENT)));
    }
}