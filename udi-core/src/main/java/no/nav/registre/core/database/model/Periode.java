package no.nav.registre.core.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.sql.Date;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Periode {
    private Date fra;
    private Date til;
}
