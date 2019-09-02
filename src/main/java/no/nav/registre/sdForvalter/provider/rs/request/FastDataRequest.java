package no.nav.registre.sdForvalter.provider.rs.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.EregModel;
import no.nav.registre.sdForvalter.database.model.KrrModel;
import no.nav.registre.sdForvalter.database.model.TpsModel;

@Getter
@Setter
@NoArgsConstructor
public class FastDataRequest {

    Set<AaregModel> aareg = new HashSet<>();
    Set<TpsModel> tps = new HashSet<>();
    Set<KrrModel> krr = new HashSet<>();
    Set<EregModel> ereg = new HashSet<>();
    @NotNull
    String eier;

}
