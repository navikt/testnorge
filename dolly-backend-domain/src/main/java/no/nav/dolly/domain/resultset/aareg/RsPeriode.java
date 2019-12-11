package no.nav.dolly.domain.resultset.aareg;

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
public class RsPeriode {

    @ApiModelProperty(
            value = "Dato fra-og-med",
            dataType = "ZonedDateTime",
            required = true,
            position = 1
    )
    @JsonDeserialize(using = JsonZonedDateTimeDeserializer.class)
    private ZonedDateTime fom;

    @ApiModelProperty(
            value = "Dato til-og-med",
            dataType = "ZonedDateTime",
            position = 2
    )
    @JsonDeserialize(using = JsonZonedDateTimeDeserializer.class)
    private ZonedDateTime tom;
}
