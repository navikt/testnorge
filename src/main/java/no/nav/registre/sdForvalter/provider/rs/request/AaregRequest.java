package no.nav.registre.sdForvalter.provider.rs.request;

import lombok.Getter;
import lombok.Setter;

import no.nav.registre.sdForvalter.database.model.AaregModel;

@Getter
@Setter
public class AaregRequest extends AaregModel {

    private String eier;

}
