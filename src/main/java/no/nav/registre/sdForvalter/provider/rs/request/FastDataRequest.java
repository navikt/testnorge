package no.nav.registre.sdForvalter.provider.rs.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.KrrModel;
import no.nav.registre.sdForvalter.domain.Ereg;
import no.nav.registre.sdForvalter.domain.TpsIdent;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FastDataRequest {

    private Set<AaregModel> aareg = new HashSet<>();
    private Set<TpsIdent> tps = new HashSet<>();
    private Set<KrrModel> krr = new HashSet<>();
    private List<Ereg> ereg = new ArrayList<>();

}
