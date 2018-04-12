package no.nav.jpa;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_BRUKER")
public class Bruker {

    @Id
    private Long NAV_IDENT;

    @ManyToMany(mappedBy = "brukere")
    @Column(name = "TEAM_MEDLEMSKAP")
    private Set<Team> teamMedlemskap = new HashSet<>();
    
    @OneToMany
    @JoinColumn(name = "id")
    @Column(name = "TEAM_EIERSKAP",unique = true)
    private Set<Team> teamEierskap;
}
