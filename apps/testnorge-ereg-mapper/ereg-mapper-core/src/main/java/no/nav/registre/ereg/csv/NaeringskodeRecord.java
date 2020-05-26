package no.nav.registre.ereg.csv;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NaeringskodeRecord {

    private String code;
    private String parentCode;
    private String level;
    private String name;
    private String shortName;
    private String notes;
    private String validFrom;
    private String validTo;

}
