package no.nav.dolly.bestilling.organisasjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest.SyntetiskOrganisasjon.AdresseRequest;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest.AdresseType.FADR;
import static no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest.AdresseType.PADR;

@Component
public class OrganisasjonerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsOrganisasjonBestilling.SyntetiskOrganisasjon.class, BestillingRequest.SyntetiskOrganisasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsOrganisasjonBestilling.SyntetiskOrganisasjon rsSyntetiskOrganisasjon, BestillingRequest.SyntetiskOrganisasjon requestOrganisasjon, MappingContext context) {
                        List<AdresseRequest> adresser = new ArrayList<>();
                        if (nonNull(rsSyntetiskOrganisasjon.getForretningsadresse())) {
                            AdresseRequest adresse = mapperFacade.map(rsSyntetiskOrganisasjon.getForretningsadresse(), AdresseRequest.class);
                            adresse.setAdressetype(FADR);
                            adresser.add(adresse);
                        }
                        if (nonNull(rsSyntetiskOrganisasjon.getPostadresse())) {
                            AdresseRequest adresse = mapperFacade.map(rsSyntetiskOrganisasjon.getPostadresse(), AdresseRequest.class);
                            adresse.setAdressetype(PADR);
                            adresser.add(adresse);
                        }
                        requestOrganisasjon.setAdresser(adresser);

                        requestOrganisasjon.setUnderenheter(mapperFacade.mapAsList(rsSyntetiskOrganisasjon.getUnderenheter(), BestillingRequest.SyntetiskOrganisasjon.class));
                    }
                })
                .byDefault()
                .register();
    }
}
