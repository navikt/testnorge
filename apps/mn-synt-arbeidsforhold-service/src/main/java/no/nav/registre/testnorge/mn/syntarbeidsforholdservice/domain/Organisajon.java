package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain;

import java.util.Random;
import java.util.Set;

import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;

public class Organisajon {
    private static final Random RANDOM = new Random();
    private OrganisasjonDTO dto;

    public Organisajon(OrganisasjonDTO dto) {
        this.dto = dto;
    }

    public String getOrgnummer() {
        return dto.getOrgnummer();
    }

    public boolean isDriverVirksomheter(){
        return !dto.getDriverVirksomheter().isEmpty();
    }

    public String getRandomVirksomhentsnummer(){
        return dto.getDriverVirksomheter().get(RANDOM.nextInt(dto.getDriverVirksomheter().size()));
    }


    public boolean isOpplysningspliktig() {
        switch (dto.getEnhetType()) {
            case "AS":
            case "NUF":
            case "BRL":
            case "KBO":
            case "SA":
            case "ENK":
                return true;
            default:
                return false;
        }
    }
}