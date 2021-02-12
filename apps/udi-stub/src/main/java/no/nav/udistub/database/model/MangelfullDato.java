package no.nav.udistub.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class MangelfullDato {

    private Integer dag;
    private Integer maaned;
    private Integer aar;
}
