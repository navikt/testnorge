package no.nav.registre.inntektsmeldingstub.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "utsettelse_av_foreldrepenger")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UtsettelseAvForeldrepenger {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne
    @JoinColumn(name = "periode_id", referencedColumnName = "id")
    private Periode periode;

    private String aarsakTilUtsettelse;
}
