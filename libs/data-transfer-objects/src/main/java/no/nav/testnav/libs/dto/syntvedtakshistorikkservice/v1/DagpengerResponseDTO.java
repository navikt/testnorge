package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;


@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DagpengerResponseDTO {

    @JsonAlias({"nyeMottaDagpengesoknadResponse", "nyeMottaDagpengevedtakResponse", "nyeDagpResponse"})
    private List<NyeDagpenger> nyeDagpenger;

    @JsonAlias({"nyeMottaDagpengesoknadFeilList", "nyeMottaDagpengevedtakFeilList", "nyeDagpFeilList"})
    private List<NyeDagpengerFeil> feiledeDagpenger;


    public List<NyeDagpenger> getNyeDagpenger(){
        if(isNull(nyeDagpenger)){
            nyeDagpenger = new ArrayList<>();
        }
        return nyeDagpenger;
    }

    public List<NyeDagpengerFeil> getFeiledeDagpenger(){
        if(isNull(feiledeDagpenger)){
            feiledeDagpenger = new ArrayList<>();
        }
        return feiledeDagpenger;
    }

}
