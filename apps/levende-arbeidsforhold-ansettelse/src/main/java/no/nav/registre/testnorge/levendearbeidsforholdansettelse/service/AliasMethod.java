package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.Null;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Alias metode for å sette opp en alias tabell, for å gjøre utrekk fra en sannsynlighetsfordeling.
 * les <a href ='https://en.wikipedia.org/wiki/Alias_method /> og
 * <a href='https://pbr-book.org/4ed/Sampling_Algorithms/The_Alias_Method' />
 */
@Slf4j
public class AliasMethod {
    List<Double> sannsynlighet;
    List<Double> q;
    List<Integer> J;
    Random random;

    /**
     * Metoden for å sette opp alias tabellen.
     * @param prob Er listen av tall som utgjør en sannsynlighetsfordeling. Denne behøver ikke å være normalisert
     */
    public AliasMethod(List<Double> prob) {
        //Finne summen av sannsynlighetsfordeling for så normalisere sannsynligheten
        Double sum = prob.stream().mapToDouble(i -> i).sum();
        sannsynlighet = new ArrayList<>();
        for(Double i: prob){
            sannsynlighet.add(i/sum);
        }

        int size = prob.size();

        this.q = new ArrayList<>();
        this.J = new ArrayList<>();

        //initialisere q og j
        // # TODO gjøre dette bedre
        for(int i=0; i<size;i++){
            q.add(0.0);
            J.add(0);
        }

        this.random = new Random();

        List<Integer> mindre = new ArrayList<>();
        List<Integer> storre = new ArrayList<>();

        //Legge til i mindre hvis q[i] er mindre enn 1 og i større hvis ikke
        for (int i = 0; i < sannsynlighet.size(); i++) {
            q.set(i, sannsynlighet.size() * sannsynlighet.get(i));
            if (q.get(i) < 1.0)
                mindre.add(i);
            else
                storre.add(i);
        }
        while (!mindre.isEmpty() && !storre.isEmpty()) {
            Integer liten = mindre.removeLast();
            Integer stor = storre.removeLast();

            J.set(liten, stor);
            q.set(stor, q.get(stor) - (1.0 - q.get(liten)));

            if (q.get(stor) < 1.0)
                mindre.add(stor);
            else
                storre.add(stor);
            log.info("q: {}", q);
            log.info("J: {}", J);
        }
    }

    /**
     * Metode for å gjøre selve trekkingen fra alias tabellen.
     * @return Returnerer indeksen fra sannsynlighetsfordelingen som har blitt trekkt.
     */
    public int aliasDraw() {
        int k = J.size();
        int nextInt = random.nextInt(k);

        double sjekk = random.nextDouble();
        if (sjekk < q.get(nextInt))
            return nextInt;
        else
            return J.get(nextInt);
    }
}

