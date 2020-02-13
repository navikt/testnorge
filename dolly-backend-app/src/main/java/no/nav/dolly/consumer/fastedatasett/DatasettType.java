package no.nav.dolly.consumer.fastedatasett;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum DatasettType {

    AAREG("/aareg"),
    EREG("/ereg"),
    KRR("/krr"),
    TPS("/tps");

    DatasettType(String url) {
        this.url = url;
    }

    private String url;
}