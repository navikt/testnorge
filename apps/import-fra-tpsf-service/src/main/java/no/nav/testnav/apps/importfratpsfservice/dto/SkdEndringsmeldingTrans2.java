package no.nav.testnav.apps.importfratpsfservice.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("t2")
public class SkdEndringsmeldingTrans2 extends SkdEndringsmelding {
    
    @Size(max = 11)
    private String fodselsnr;
    
    @Size(max = 6)
    private String barnFodsdato1;
    @Size(max = 5)
    private String barnPersnr1;
    @Size(max = 50)
    private String barnNavn1;
    @Size(max = 1)
    private String barnKjoenn1;

    @Size(max = 6)
    private String barnFodsdato2;
    @Size(max = 5)
    private String barnPersnr2;
    @Size(max = 50)
    private String barnNavn2;
    @Size(max = 1)
    private String barnKjoenn2;

    @Size(max = 6)
    private String barnFodsdato3;
    @Size(max = 5)
    private String barnPersnr3;
    @Size(max = 50)
    private String barnNavn3;
    @Size(max = 1)
    private String barnKjoenn3;

    @Size(max = 6)
    private String barnFodsdato4;
    @Size(max = 5)
    private String barnPersnr4;
    @Size(max = 50)
    private String barnNavn4;
    @Size(max = 1)
    private String barnKjoenn4;

    @Size(max = 6)
    private String barnFodsdato5;
    @Size(max = 5)
    private String barnPersnr5;
    @Size(max = 50)
    private String barnNavn5;
    @Size(max = 1)
    private String barnKjoenn5;

    @Size(max = 6)
    private String barnFodsdato6;
    @Size(max = 5)
    private String barnPersnr6;
    @Size(max = 50)
    private String barnNavn6;
    @Size(max = 1)
    private String barnKjoenn6;

    @Size(max = 6)
    private String barnFodsdato7;
    @Size(max = 5)
    private String barnPersnr7;
    @Size(max = 50)
    private String barnNavn7;
    @Size(max = 1)
    private String barnKjoenn7;

    @Size(max = 6)
    private String barnFodsdato8;
    @Size(max = 5)
    private String barnPersnr8;
    @Size(max = 50)
    private String barnNavn8;
    @Size(max = 1)
    private String barnKjoenn8;

    @Size(max = 6)
    private String barnFodsdato9;
    @Size(max = 5)
    private String barnPersnr9;
    @Size(max = 50)
    private String barnNavn9;
    @Size(max = 1)
    private String barnKjoenn9;

    @Size(max = 6)
    private String barnFodsdato10;
    @Size(max = 5)
    private String barnPersnr10;
    @Size(max = 50)
    private String barnNavn10;
    @Size(max = 1)
    private String barnKjoenn10;

    @Size(max = 6)
    private String barnFodsdato11;
    @Size(max = 5)
    private String barnPersnr11;
    @Size(max = 50)
    private String barnNavn11;
    @Size(max = 1)
    private String barnKjoenn11;

    @Size(max = 6)
    private String barnFodsdato12;
    @Size(max = 5)
    private String barnPersnr12;
    @Size(max = 50)
    private String barnNavn12;
    @Size(max = 1)
    private String barnKjoenn12;

    @Size(max = 6)
    private String barnFodsdato13;
    @Size(max = 5)
    private String barnPersnr13;
    @Size(max = 50)
    private String barnNavn13;
    @Size(max = 1)
    private String barnKjoenn13;
    
}
