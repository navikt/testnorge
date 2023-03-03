package no.nav.testnav.identpool.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

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
