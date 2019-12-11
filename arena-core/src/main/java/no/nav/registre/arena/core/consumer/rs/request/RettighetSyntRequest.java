package no.nav.registre.arena.core.consumer.rs.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RettighetSyntRequest {

    @JsonProperty("type_kode")
    private String typeKode;

    @JsonProperty("utfall")
    private String utfall;

    @JsonProperty("start_dato")
    private LocalDate startDato;
}
