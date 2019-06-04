package no.nav.identpool.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "WHITELIST")
public class Whitelist {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "white_seq")
    @SequenceGenerator(name = "white_seq", sequenceName = "WHITELIST_SEQ", allocationSize = 1)
    @Id
    private Long id;

    private String fnr;

}
