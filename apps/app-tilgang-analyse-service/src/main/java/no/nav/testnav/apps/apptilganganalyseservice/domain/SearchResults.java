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
    String owner;
    List<ItemResult> items;

    public SearchResults(String repo, String owner) {
        this.repo = repo;
        this.owner = owner;
        this.items = new ArrayList<>();
    }

    public SearchResults(SearchDTO dto, String repo, String owner) {
        this.repo = repo;
        this.owner = owner;
        this.items = dto.getItems()
                .stream()
                .map(ItemResult::new)
                .collect(Collectors.toList());
    }

    public SearchResults concat(SearchResults results) {
        return new SearchResults(
                repo,
                owner,
                Stream.concat(items.stream(), results.getItems().stream()).collect(Collectors.toList())
        );
    }

    public SearchResults concat(SearchDTO dto) {
        return concat(new SearchResults(dto, repo, owner));
    }
}
