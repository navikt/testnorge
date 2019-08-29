package no.nav.registre.sdForvalter.provider.rs.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import no.nav.registre.sdForvalter.database.model.TpsModel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TpsRequest extends TpsModel {

    private String eier;

}
