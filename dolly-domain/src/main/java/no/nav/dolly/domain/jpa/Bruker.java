package no.nav.dolly.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_BRUKER")
public class Bruker {

    @Id
    @Column(name = "NAV_IDENT", length = 10)
    private String navIdent;
    @ManyToMany(mappedBy = "medlemmer")
    private Set<Team> teams;
    @ManyToMany
    @JoinTable(name = "T_BRUKER_FAVORITTER",
            joinColumns = @JoinColumn(name = "bruker_id"),
            inverseJoinColumns = @JoinColumn(name = "gruppe_id"))
    private Set<Testgruppe> favoritter;

    public Bruker(String navIdent) {
        this.navIdent = navIdent;
    }

    public Set<Team> getTeams() {
        if (teams == null) {
            teams = new HashSet<>();
        }
        return teams;
    }

    public Set<Testgruppe> getFavoritter() {
        if (favoritter == null) {
            favoritter = new HashSet<>();
        }
        return favoritter;
    }
}
