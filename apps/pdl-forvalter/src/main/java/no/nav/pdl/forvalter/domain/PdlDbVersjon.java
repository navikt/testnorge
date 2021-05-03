package no.nav.pdl.forvalter.domain;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonFilter("idFilter")
public abstract class PdlDbVersjon implements Serializable {

    private Integer id;
}
