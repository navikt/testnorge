package no.nav.dolly.web.domain;

import lombok.Value;

@Value
class LogMetadata {
    private final String userAgent;
    private final String host;

    /**
     * Nettleser navn er ikke 100% sikker basert på user agent. Derfor kan denne koden kun brukes til logging.
     */
    String getNameBrowser() {
        if(userAgent.contains("Edg") || userAgent.contains("Edge")){
            return "Edge";
        } else if (userAgent.contains("Firefox") && !userAgent.contains("Seamonkey")) {
            return "Firefox";
        } else if (userAgent.contains("Seamonkey")) {
            return "Seamonkey";
        } else if (userAgent.contains("Chrome") && !userAgent.contains("Chromium")) {
            return "Chrome";
        } else if (userAgent.contains("Chromium")) {
            return "Chromium";
        } else if (userAgent.contains("Safari") && !userAgent.contains("Chrome") && !userAgent.contains("Chromium")){
            return "Safari";
        } else if(userAgent.contains("OPR") || userAgent.contains("Opera")){
            return "Opera";
        } else if(userAgent.contains("; MSIE ") || userAgent.contains("Trident/7.0;")){
            return "Internet Explorer";
        } else {
            return "Uskjent";
        }
    }
}