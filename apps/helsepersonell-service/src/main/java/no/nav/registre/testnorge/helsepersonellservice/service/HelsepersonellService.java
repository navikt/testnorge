package no.nav.registre.testnorge.helsepersonellservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.testnav.libs.dto.helsepersonell.v1.HelsepersonellDTO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Component
@RequiredArgsConstructor
public class HelsepersonellService {

    private static final String fileUrl = "helsepersonell/helsepersonell.csv";

    public List<HelsepersonellDTO> getHelsepersonell() {

        var resource = new ClassPathResource(fileUrl);
        try {
            return new BufferedReader(new InputStreamReader(resource.getInputStream(), UTF_8))
                    .lines()
                    .filter(line -> !line.contains("FNR"))
                    .map(line -> line.split(";"))
                    .map(words -> HelsepersonellDTO.builder()
                            .fornavn(words[0].split(" ")[0])
                            .etternavn(words[0].split(" ")[1])
                            .fnr(words[1].trim())
                            .hprId(words[2].trim())
                            .samhandlerType(words[3].trim())
                            .build())
                    .toList();

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, fileUrl);
        }
    }
}
