package no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({ "fom", "tom" })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Bruksperiode {

    private LocalDateTime fom;
    private LocalDateTime tom;

    @JsonProperty("fom")
    @ApiModelProperty(
            notes = "Fra-tidsstempel for bruksperiode, format (ISO-8601): yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS]]",
            example = "2015-01-06T21:44:04.748"
    )
    public String getFomAsString() {
        return JavaTimeUtil.toString(this.fom);
    }

    @JsonProperty("tom")
    @ApiModelProperty(
            notes = "Til-tidsstempel for bruksperiode, format (ISO-8601): yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS]]",
            example = "2015-12-06T19:45:04"
    )
    public String getTomAsString() {
        return JavaTimeUtil.toString(this.tom);
    }
}
