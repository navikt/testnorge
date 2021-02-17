package no.nav.registre.testnorge.personsearchservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import no.nav.registre.testnorge.personsearchservice.domain.Search;
import no.nav.registre.testnorge.personsearchservice.service.PersonSearchService;

@RestController
@RequestMapping("/api/v1/person")
@RequiredArgsConstructor
public class PersonSearchController {

    private final ObjectMapper objectMapper;
    private final PersonSearchService service;

    @GetMapping
    public ResponseEntity<Search> search(@RequestParam("search") String search) throws UnsupportedEncodingException, JsonProcessingException {
        var decode = URLDecoder.decode(search, StandardCharsets.UTF_8.name());

        var value = objectMapper.readValue(decode, Search.class);
        service.search(value);
        return ResponseEntity.ok(value);
    }

    @PostMapping
    public ResponseEntity<Search> test(Search search) {
        return ResponseEntity.ok(search);
    }

}
