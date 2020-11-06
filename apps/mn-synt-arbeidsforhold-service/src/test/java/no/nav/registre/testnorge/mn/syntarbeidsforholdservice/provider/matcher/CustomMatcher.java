package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.provider.matcher;


import com.github.tomakehurst.wiremock.matching.MatchResult;

@FunctionalInterface
public interface CustomMatcher<T> {
        MatchResult match(T first, T second);
}
