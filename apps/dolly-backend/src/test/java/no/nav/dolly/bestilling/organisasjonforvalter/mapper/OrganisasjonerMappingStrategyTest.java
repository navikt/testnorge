package no.nav.dolly.bestilling.organisasjonforvalter.mapper;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling.SyntetiskOrganisasjon;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OrganisasjonerMappingStrategyTest {

    @Autowired
    private MapperFacade mapperFacade;

    @BeforeEach
    public void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new OrganisasjonerMappingStrategy());
    }

    private SyntetiskOrganisasjon prepMinimumRsOrganisasjonBestilling() {

        return SyntetiskOrganisasjon.builder()
                .forretningsadresse(SyntetiskOrganisasjon.Adresse.builder()
                        .landkode("NO")
                        .build())
                .build();
    }

    private SyntetiskOrganisasjon prepRsOrganisasjonBestilling() {

        return SyntetiskOrganisasjon.builder()
                .enhetstype("Enhet")
                .formaal("Testing")
                .epost("Test@nav.no")
                .underenheter(Collections.singletonList(SyntetiskOrganisasjon.builder()
                        .formaal("Underenhet1")
                        .underenheter(Collections.singletonList(SyntetiskOrganisasjon.builder()
                                .formaal("Underenhet2")
                                .build()))
                        .build()))
                .forretningsadresse(SyntetiskOrganisasjon.Adresse.builder()
                        .adresselinjer(Collections.singletonList("Gate 1"))
                        .kommunenr("123")
                        .landkode("NO")
                        .postnr("1234")
                        .build())
                .postadresse(SyntetiskOrganisasjon.Adresse.builder()
                        .adresselinjer(Collections.singletonList("Gate 2"))
                        .kommunenr("123")
                        .landkode("NO")
                        .postnr("1234")
                        .build())
                .build();
    }

    @Test
    void should_return_nonnull_element_after_mapping() {

        BestillingRequest.SyntetiskOrganisasjon result = mapperFacade.map(prepMinimumRsOrganisasjonBestilling(), BestillingRequest.SyntetiskOrganisasjon.class);

        assertThat(result).isNotNull();
    }

    @Test
    void should_return_same_values_after_mapping() {

        SyntetiskOrganisasjon syntetiskOrganisasjon = prepRsOrganisasjonBestilling();

        BestillingRequest.SyntetiskOrganisasjon result = mapperFacade.map(syntetiskOrganisasjon, BestillingRequest.SyntetiskOrganisasjon.class);

        assertThat(result.getEnhetstype()).isEqualTo(syntetiskOrganisasjon.getEnhetstype());
        assertThat(result.getEpost()).isEqualTo(syntetiskOrganisasjon.getEpost());
        assertThat(result.getFormaal()).isEqualTo(syntetiskOrganisasjon.getFormaal());
    }

    @Test
    void should_create_adresseliste_med_riktig_size_og_forskjellig_enum() {

        BestillingRequest.SyntetiskOrganisasjon result = mapperFacade.map(prepRsOrganisasjonBestilling(), BestillingRequest.SyntetiskOrganisasjon.class);

        assertThat(result.getAdresser()).hasSize(2);
        assertThat(result.getAdresser().get(0).getAdressetype()).isEqualTo(BestillingRequest.AdresseType.FADR);
        assertThat(result.getAdresser().get(1).getAdressetype()).isEqualTo(BestillingRequest.AdresseType.PADR);
    }

    @Test
    void should_map_manglende_felter_som_null_og_sette_enum() {

        BestillingRequest.SyntetiskOrganisasjon result = mapperFacade.map(prepMinimumRsOrganisasjonBestilling(), BestillingRequest.SyntetiskOrganisasjon.class);

        assertThat(result.getAdresser()).hasSize(1);
        assertThat(result.getAdresser().get(0).getLandkode()).isEqualTo("NO");
        assertThat(result.getAdresser().get(0).getAdressetype()).isEqualTo(BestillingRequest.AdresseType.FADR);
    }

    @Test
    void should_map_underenheter_rekursivt() {

        BestillingRequest.SyntetiskOrganisasjon result = mapperFacade.map(prepRsOrganisasjonBestilling(), BestillingRequest.SyntetiskOrganisasjon.class);

        assertThat(result.getUnderenheter()).hasSize(1);
        assertThat(result.getUnderenheter().get(0).getFormaal()).isEqualTo("Underenhet1");
        assertThat(result.getUnderenheter().get(0).getUnderenheter()).hasSize(1);
        assertThat(result.getUnderenheter().get(0).getUnderenheter().get(0).getFormaal()).isEqualTo("Underenhet2");
    }
}