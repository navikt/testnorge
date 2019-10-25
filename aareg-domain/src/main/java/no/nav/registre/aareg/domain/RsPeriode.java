package no.nav.registre.aareg.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import no.nav.registre.aareg.util.JsonDateSerializer;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsPeriode {

    @JsonSerialize(using = JsonDateSerializer.class)
    private LocalDateTime fom;

    @JsonSerialize(using = JsonDateSerializer.class)
    private LocalDateTime tom;
}
