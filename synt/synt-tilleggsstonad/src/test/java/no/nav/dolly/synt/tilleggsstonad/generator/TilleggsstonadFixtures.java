package no.nav.dolly.synt.tilleggsstonad.generator;

import lombok.experimental.UtilityClass;
import no.nav.dolly.synt.tilleggsstonad.dto.VedtakRequestDto;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.EnumMap;

import static java.util.Objects.requireNonNull;

@UtilityClass
class TilleggsstonadFixtures {

    static TilleggsstonadGenerator generator(TilleggsstonadType... types) {

        var models = new EnumMap<TilleggsstonadType, TilleggsstonadModel>(TilleggsstonadType.class);
        for (var type : types) {
            models.put(type, TilleggsstonadModel.read(resource("models/" + TilleggsstonadGenerator.modelFileName(type))));
        }
        return new TilleggsstonadGenerator(models);

    }

    static VedtakRequestDto request(String vedtakTypeKode, String utfall, String fraDato, String tilDato) {

        return VedtakRequestDto.builder()
                .vedtakTypeKode(vedtakTypeKode)
                .utfall(utfall)
                .fraDato(fraDato)
                .tilDato(tilDato)
                .vedtakDato(fraDato)
                .build();

    }

    static Path resource(String name) {

        try {
            return Path.of(requireNonNull(TilleggsstonadFixtures.class.getClassLoader().getResource(name), name).toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }

    }

}
