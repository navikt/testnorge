package no.nav.registre.inntektsmeldingstub.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "periode")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Periode {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate fom;
    private LocalDate tom;

}
