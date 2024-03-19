package no.nav.testnav.endringsmeldingservice.mapper;

import lombok.experimental.UtilityClass;
import no.nav.testnav.endringsmeldingservice.consumer.dto.HentIdenterRequest;
import no.nav.testnav.endringsmeldingservice.consumer.dto.HentIdenterRequest.Identtype;
import no.nav.testnav.libs.dto.endringsmelding.v2.FoedselsmeldingDTO;
import no.nav.testnav.libs.dto.endringsmelding.v2.Kjoenn;

import java.security.SecureRandom;
import java.util.Random;

import static java.util.Objects.isNull;
import static no.nav.testnav.endringsmeldingservice.consumer.dto.HentIdenterRequest.Kjoenn.KVINNE;
import static no.nav.testnav.endringsmeldingservice.consumer.dto.HentIdenterRequest.Kjoenn.MANN;

@UtilityClass
public class IdentpoolRequestMapper {

    private static final Random random = new SecureRandom();

    public HentIdenterRequest convert(FoedselsmeldingDTO fodselsmelding) {

        return HentIdenterRequest.builder()
                .identtype(Identtype.valueOf(fodselsmelding.getIdenttype().name()))
                .foedtFoer(fodselsmelding.getFoedselsdato())
                .foedtEtter(fodselsmelding.getFoedselsdato())
                .kjoenn(convertKjoenn(fodselsmelding.getKjoenn()))
                .rekvirertAv("TEMS") // testnav-endringsmelding-service
                .antall(1)
                .build();
    }
    private HentIdenterRequest.Kjoenn convertKjoenn(Kjoenn kjoenn) {

        if (isNull(kjoenn)) {
            return random.nextBoolean() ? MANN : KVINNE;
        }

        return switch (kjoenn) {
            case GUTT -> MANN;
            case JENTE -> KVINNE;
            case UKJENT -> random.nextBoolean() ? MANN : KVINNE;
        };
    }
}
