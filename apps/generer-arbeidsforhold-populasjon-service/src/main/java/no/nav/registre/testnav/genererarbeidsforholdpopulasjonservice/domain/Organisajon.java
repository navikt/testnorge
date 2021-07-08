package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain;

import java.util.List;
import java.util.Random;

import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;

public class Organisajon {
    private static final Random RANDOM = new Random();
    private OrganisasjonDTO dto;

    public Organisajon(OrganisasjonDTO dto) {
        this.dto = dto;
    }

    public String getOrgnummer() {
        return dto.getOrgnummer();
    }

    public boolean isDriverVirksomheter() {
        return !dto.getDriverVirksomheter().isEmpty();
    }

    public String getRandomVirksomhetsnummer() {
        return dto.getDriverVirksomheter().get(RANDOM.nextInt(dto.getDriverVirksomheter().size()));
    }

    public List<String> getDriverVirksomheter() {
        return dto.getDriverVirksomheter();
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