package no.nav.dolly.bestilling.kontoregisterservice.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.kontoregister.v1.OppdaterKontoRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BankkontonrUtlandskMappingStrategyTest {

    private static final String IDENT = "12345678901";

    private MapperFacade mapperFacade;

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new BankkontonrUtlandskMappingStrategy());
    }

    @Test
    void shouldMapBankkodeForAustralia() {

        var target = map(BankkontonrUtlandDTO.builder()
                .kontonummer("AU123456789")
                .landkode("AU")
                .swift("BANKAU22")
                .bankkode("123456")
                .build());

        assertThat(target.getUtenlandskKonto().getBankkode()).isEqualTo("123456");
        assertThat(target.getUtenlandskKonto().getSwiftBicKode()).isEqualTo("BANKAU22");
    }

    @Test
    void shouldFallbackToIbanWhenBankkodeMissing() {

        var target = map(BankkontonrUtlandDTO.builder()
                .kontonummer("CA123456789")
                .landkode("CA")
                .iban("123456789")
                .build());

        assertThat(target.getUtenlandskKonto().getBankkode()).isEqualTo("123456789");
    }

    @Test
    void shouldOmitBankkodeForCountryWithoutBankkode() {

        var target = map(BankkontonrUtlandDTO.builder()
                .kontonummer("DE12345678901234567890")
                .landkode("DE")
                .bankkode("123456")
                .iban("654321")
                .build());

        assertThat(target.getUtenlandskKonto().getBankkode()).isNull();
    }

    @Test
    void shouldMapBankkodeForIso3Landkode() {

        var target = map(BankkontonrUtlandDTO.builder()
                .kontonummer("NZ123456789")
                .landkode("NZL")
                .bankkode("123456")
                .build());

        assertThat(target.getUtenlandskKonto().getBankLandkode()).isEqualTo("NZ");
        assertThat(target.getUtenlandskKonto().getBankkode()).isEqualTo("123456");
    }

    private OppdaterKontoRequestDTO map(BankkontonrUtlandDTO bankkonto) {

        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", IDENT);
        return mapperFacade.map(bankkonto, OppdaterKontoRequestDTO.class, context);
    }
}
