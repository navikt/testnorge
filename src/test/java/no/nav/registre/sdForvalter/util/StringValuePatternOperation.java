package no.nav.registre.sdForvalter.util;

import com.github.tomakehurst.wiremock.matching.StringValuePattern;

@FunctionalInterface
interface StringValuePatternOperation {
    StringValuePattern getPattern(String value);
}
