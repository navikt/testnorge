package no.nav.registre.sdForvalter.database.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

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
@ToString
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Table(name = "Varighet")
public class Varighet extends AuditModel {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private Period ttl;

    private Date bestilt;
    @JsonBackReference(value = "team-varigheter")
    @ManyToOne
    @JoinColumn(name = "varighet_id")
    private Team team;
    @JsonManagedReference(value = "tps-varighet")
    @OneToMany(mappedBy = "varighet")
    private Collection<TpsModel> tps;
    @JsonManagedReference(value = "aareg-varighet")
    @OneToMany(mappedBy = "varighet")
    private Collection<AaregModel> aareg;
    @JsonManagedReference(value = "krr-varighet")
    @OneToMany(mappedBy = "varighet")
    private Collection<KrrModel> krr;
    @JsonManagedReference(value = "ereg-varighet")
    @OneToMany(mappedBy = "varighet")
    private Collection<EregModel> ereg;

    public Boolean scheduleDeletion() {
        return getBestilt().toLocalDate().plus(ttl).isAfter(LocalDate.now());
    }

    public Boolean shouldNotify() {
        return getBestilt().toLocalDate().plus(ttl).isAfter(LocalDate.now().minusMonths(1));
    }

    public Period getAlder() {
        return Period.between(getCreatedAt().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate(), LocalDate.now());
    }

    public Period timeLeft() {
        return Period.between(getBestilt().toLocalDate(), LocalDate.now());
    }
}
