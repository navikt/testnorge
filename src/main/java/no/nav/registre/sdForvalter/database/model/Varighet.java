package no.nav.registre.sdForvalter.database.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Collection;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Table(name = "Varighet")
public class Varighet extends AuditModel {

    private static final Period EXPIRATION_TIME = Period.of(2, 0, 0);

    @Id
    @GeneratedValue
    private Long id;

    private boolean hasNotified;

    private Date bestilt;
    @JsonBackReference(value = "team-varigheter")
    @ManyToOne
    @JoinColumn(name = "varighet_id")
    private Team team;

    @JsonManagedReference(value = "tps-varighet")
    @OneToMany(
            mappedBy = "varighet",
            cascade = CascadeType.ALL
    )
    private Collection<TpsModel> tps;
    @JsonManagedReference(value = "aareg-varighet")
    @OneToMany(
            mappedBy = "varighet",
            cascade = CascadeType.ALL
    )
    private Collection<AaregModel> aareg;
    @JsonManagedReference(value = "krr-varighet")
    @OneToMany(
            mappedBy = "varighet",
            cascade = CascadeType.ALL
    )
    private Collection<KrrModel> krr;
    @JsonManagedReference(value = "ereg-varighet")
    @OneToMany(
            mappedBy = "varighet",
            cascade = CascadeType.ALL
    )
    private Collection<EregModel> ereg;

    public Boolean shouldDelete() {
        return LocalDate.now().isAfter(getBestilt().toLocalDate().plus(EXPIRATION_TIME).plusYears(1));
    }

    public Boolean shouldUse() {
        return LocalDate.now().isBefore(getBestilt().toLocalDate().plus(EXPIRATION_TIME));
    }

    public Boolean shouldNotify() {
        if (hasNotified) {
            return false;
        }
        return LocalDate.now().isAfter(getBestilt().toLocalDate().plus(EXPIRATION_TIME).minusMonths(1));
    }

    public Period getAlder() {
        return Period.between(getCreatedAt().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate(), LocalDate.now());
    }

    public Period timeLeft() {
        return Period.between(LocalDate.now(), getBestilt().toLocalDate().plus(EXPIRATION_TIME));
    }
}
