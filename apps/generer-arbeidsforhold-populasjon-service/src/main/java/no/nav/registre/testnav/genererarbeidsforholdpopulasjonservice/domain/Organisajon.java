package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain;

import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;

import java.util.List;
import java.util.Random;

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
        return switch (dto.getEnhetType()) {
            case "AS", "NUF", "BRL", "KBO", "SA", "ENK" -> true;
            default -> false;
        };
    }
}