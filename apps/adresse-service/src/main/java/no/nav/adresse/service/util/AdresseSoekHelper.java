package no.nav.adresse.service.util;

import lombok.experimental.UtilityClass;
import no.nav.adresse.service.dto.MatrikkelAdresseResponse;
import no.nav.adresse.service.dto.MatrikkeladresseRequest;
import no.nav.adresse.service.dto.VegAdresseResponse;
import no.nav.adresse.service.dto.VegadresseRequest;
import no.nav.adresse.service.exception.NotFoundException;

import java.security.SecureRandom;
import java.util.List;

import static java.util.Objects.isNull;

@UtilityClass
public class AdresseSoekHelper {

    private static final String NOT_FOUND = "Ingen adresse funnet, request: ";

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String serialize(MatrikkeladresseRequest request) {

        return String.format("%s-%s-%s-%s-%s-%s-%s",
                request.getMatrikkelId(),
                request.getKommunenummer(),
                request.getGardsnummer(),
                request.getBrukesnummer(),
                request.getPostnummer(),
                request.getPoststed(),
                request.getTilleggsnavn());
    }

    public static String serialize(VegadresseRequest request) {

        return String.format("%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s",
                request.getMatrikkelId(),
                request.getAdressenavn(),
                request.getHusnummer(),
                request.getHusbokstav(),
                request.getPostnummer(),
                request.getKommunenummer(),
                request.getBydelsnummer(),
                request.getPoststed(),
                request.getKommunenavn(),
                request.getBydelsnavn(),
                request.getTilleggsnavn(),
                request.getFritekst());
    }

    public static List<VegAdresseResponse.Hits> getSublist(VegAdresseResponse.Data data, long antall, VegadresseRequest request) {

        if (isNull(data.getSokAdresse()) || data.getSokAdresse().getHits().isEmpty()) {
            throw new NotFoundException(NOT_FOUND + request.toString());

        } else if (data.getSokAdresse().getHits().size() == antall) {
            return data.getSokAdresse().getHits();

        } else {
            var length = Math.min(antall, data.getSokAdresse().getHits().size());
            int startIndex = (int) Math.floor(secureRandom.nextFloat() * (data.getSokAdresse().getHits().size() - length));
            return data.getSokAdresse().getHits().subList(startIndex, startIndex + (int) length);
        }
    }

    public static List<MatrikkelAdresseResponse.Hits> getSublist(MatrikkelAdresseResponse.Data data, long antall, MatrikkeladresseRequest request) {

        if (isNull(data.getSokAdresse()) || data.getSokAdresse().getHits().isEmpty()) {
            throw new NotFoundException(NOT_FOUND + request.toString());

        } else if (data.getSokAdresse().getHits().size() == antall) {
            return data.getSokAdresse().getHits();

        } else {
            var length = Math.min(antall, data.getSokAdresse().getHits().size());
            int startIndex = (int) Math.floor(secureRandom.nextFloat() * (data.getSokAdresse().getHits().size() - length));
            return data.getSokAdresse().getHits().subList(startIndex, startIndex + (int) length);
        }
    }
}
