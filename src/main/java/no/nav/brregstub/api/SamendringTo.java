package no.nav.brregstub.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SamendringTo {

    @ApiModelProperty(dataType = "java.lang.String", example = "2004-01-01", required = true)
    private LocalDate registringsDato;
    private List<PersonOgRolleTo> roller = new LinkedList<>();

}
