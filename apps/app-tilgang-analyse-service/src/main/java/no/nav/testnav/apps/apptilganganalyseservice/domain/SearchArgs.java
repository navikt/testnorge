package no.nav.testnav.apps.apptilganganalyseservice.domain;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchArgs {

    private final List<String> strings;
    private final String owner;
    private final String repo;
    private final List<String> language;

    @Override
    public String toString() {
        var searchTexts = String.join("+", strings);
        var languages = language
                .stream()
                .map(value -> "language:" + value)
                .collect(Collectors.joining("+"));

        return searchTexts + "+in:file+" + languages + "+repo:" + owner + "/" + repo;
    }

    public static class Builder {
        private List<String> strings;
        private List<String> language;
        private String repo;
        private String owner;

        private Builder() {
            strings = new ArrayList<>();
            language = new ArrayList<>();
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder repo(String repo) {
            this.repo = repo;
            return this;
        }

        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder addSearchString(String string) {
            this.strings.add(string);
            return this;
        }

        public Builder addLanguage(String language) {
            this.language.add(language);
            return this;
        }

        public SearchArgs build() {
            return new SearchArgs(strings, owner, repo, language);
        }
    }
}
