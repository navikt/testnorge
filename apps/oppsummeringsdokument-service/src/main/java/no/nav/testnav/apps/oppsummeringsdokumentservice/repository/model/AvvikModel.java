package no.nav.testnav.apps.oppsummeringsdokumentservice.repository.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvvikModel {
    private String id;
    private String navn;
    private String alvorlighetsgrad;
}
