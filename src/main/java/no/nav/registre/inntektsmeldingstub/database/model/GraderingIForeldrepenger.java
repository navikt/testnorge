package no.nav.registre.inntektsmeldingstub.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "gradering_i_foreldrepenger")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GraderingIForeldrepenger {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "periode_id", referencedColumnName = "id")
    private Periode periode;

    private Integer gradering;
}
