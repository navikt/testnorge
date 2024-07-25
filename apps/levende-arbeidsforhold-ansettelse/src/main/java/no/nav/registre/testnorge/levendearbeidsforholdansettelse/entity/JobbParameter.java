package no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "jobb_parameter")
public class JobbParameter {
    @Id
    @Size(max = 255)
    @Column(name = "navn", nullable = false)
    private String navn;

    @Size(max = 255)
    @NotNull
    @Column(name = "tekst", nullable = false)
    private String tekst;

    @Size(max = 255)
    @Column(name = "verdi")
    private String verdi;

    @Column(name = "verdier")
    private String verdierList;
    //@Override
    //public void setVerdierList(String verdier){
    //    verdier.split(",");

    //}
    //public void setverd
    @Override
    public String toString(){
        return "Navn: " + navn + ", tekst: " + tekst + " verdi: " +verdi + " verdierListe: " +verdierList;
    }
/*
    private static String stringArrayTOString(String[] input){
        StringBuffer sb = new StringBuffer("");
        int i = 0;
        for(String value:input){
            if(i!=0){
                sb.append(",");
            }
            sb.append(value);
            i++;
        }
        return sb.toString();

        private static String[] stringToStringArray(String input) {
            String[] output = input.split(",");
            return output;
        }
    }

 */

}