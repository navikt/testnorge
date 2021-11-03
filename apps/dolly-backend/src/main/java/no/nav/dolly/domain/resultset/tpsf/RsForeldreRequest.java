package no.nav.dolly.domain.resultset.tpsf;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsForeldreRequest extends RsRelasjon {

    public enum ForeldreType {MOR, FAR}

    private ForeldreType foreldreType;
    private Boolean harFellesAdresse;
}