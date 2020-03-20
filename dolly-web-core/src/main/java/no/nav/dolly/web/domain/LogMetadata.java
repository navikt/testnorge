package no.nav.dolly.web.domain;

import lombok.Value;

@Value
class LogMetadata {
    private final String userAgent;

    @Override
    public String toString() {
        return "[ user-agent: " + userAgent +" ]";
    }
}
