package no.nav.registre.core.database.model;


import lombok.*;

import javax.persistence.Embeddable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Embeddable
public class MangelfullDato {

    private Integer dag;
    private Integer maaned;
    private Integer aar;
}
