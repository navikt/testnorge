package no.nav.dolly.web.domain;

import lombok.Value;

@Value
class LogMetadata {
    private final String userAgent;
    private final String host;
    private static final String SEAMONKEY = "Seamonkey";
    private static final String CHROME = "Chrome";
    private static final String CHROMIUM = "Chromium";
    private static final String EDGE = "Edge";
    private static final String SAFARI = "Safari";
    private static final String OPERA = "Opera";

    /**
     * Nettleser navn er ikke 100% sikker basert på user agent. Derfor kan denne koden kun brukes til logging.
     */
    String getNameBrowser() {
        if (userAgent.contains("Edg") || userAgent.contains(EDGE)) {
            return EDGE;
        } else if (userAgent.contains("Firefox") && !userAgent.contains(SEAMONKEY)) {
            return "Firefox";
        } else if (userAgent.contains(SEAMONKEY)) {
            return SEAMONKEY;
        } else if (userAgent.contains(CHROME) && !userAgent.contains(CHROMIUM)) {
            return CHROME;
        } else if (userAgent.contains(CHROMIUM)) {
            return CHROMIUM;
        } else if (userAgent.contains(SAFARI) && !userAgent.contains(CHROME) && !userAgent.contains(CHROMIUM)) {
            return SAFARI;
        } else if (userAgent.contains("OPR") || userAgent.contains(OPERA)) {
            return OPERA;
        } else if (userAgent.contains("; MSIE ") || userAgent.contains("Trident/7.0;")) {
            return "Internet Explorer";
        } else {
            return "Ukjent";
        }
    }
}