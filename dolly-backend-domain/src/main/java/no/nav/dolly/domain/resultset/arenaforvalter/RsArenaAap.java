package no.nav.dolly.domain.resultset.arenaforvalter;

import java.time.ZonedDateTime;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.util.JsonZonedDateTimeDeserializer;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsArenaAap {

    @ApiModelProperty(
            position = 1,
            required = true,
            dataType = "ZonedDateTime"
    )
    @JsonDeserialize(using = JsonZonedDateTimeDeserializer.class)
    private ZonedDateTime fraDato;

    @ApiModelProperty(
            position = 2,
            dataType = "ZonedDateTime"
    )
    @JsonDeserialize(using = JsonZonedDateTimeDeserializer.class)
    private ZonedDateTime tilDato;
}