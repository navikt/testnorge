package no.nav.dolly.domain.resultSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsPostadresse {

    private Long id;

    private String postLinje1;

    private String postLinje2;

    private String postLinje3;

    private String postLand;

}
