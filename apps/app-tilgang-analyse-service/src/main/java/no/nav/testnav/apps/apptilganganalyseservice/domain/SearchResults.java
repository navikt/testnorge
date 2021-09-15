package no.nav.testnav.apps.apptilganganalyseservice.domain;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.testnav.apps.apptilganganalyseservice.consumer.dto.ItemDTO;
import no.nav.testnav.apps.apptilganganalyseservice.consumer.dto.SearchDTO;

@Value
@AllArgsConstructor
public class SearchResults {
    String repo;
    List<String> sha;

    public SearchResults(String repo) {
        this.repo = repo;
        this.sha = new ArrayList<>();
    }

    public SearchResults(SearchDTO dto, String repo) {
        this.repo = repo;
        this.sha = dto.getItems()
                .stream()
                .map(ItemDTO::getSha)
                .collect(Collectors.toList());
    }

    public SearchResults concat(SearchResults results) {
        return new SearchResults(
                repo,
                Stream.concat(sha.stream(), results.getSha().stream()).collect(Collectors.toList())
        );
    }

    public SearchResults concat(SearchDTO dto) {
        return concat(new SearchResults(dto, repo));
    }
}
