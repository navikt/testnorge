package no.nav.registre.core.database.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "aliaser")
public class Alias {

    @GeneratedValue
    @Id
    private Long id;

    private String fnr;

    @Embedded
    private PersonNavn navn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_fnr")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Person person;

}
