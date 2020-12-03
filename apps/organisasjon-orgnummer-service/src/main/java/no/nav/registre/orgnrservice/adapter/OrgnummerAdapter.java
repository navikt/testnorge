package no.nav.registre.orgnrservice.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.orgnrservice.repository.OrgnummerRepository;
import no.nav.registre.orgnrservice.repository.model.OrgnummerModel;

@AllArgsConstructor
@Component
public class OrgnummerAdapter {

    private final OrgnummerRepository orgnummerRepository;

    public List<String> saveAll(List<String> orgnummerListe) {
        var modelList = orgnummerListe.stream()
                .map( orgnummer ->
                        OrgnummerModel.builder().orgnummer(orgnummer).ledig(true).build()
                )
                .collect(Collectors.toList());

        orgnummerRepository.saveAll(modelList);
        return orgnummerListe;
    }
}
