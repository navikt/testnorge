package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.util;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Alias metode for å sette opp en alias tabell, for å gjøre utrekk fra en sannsynlighetsfordeling.
 * les <a href ='https://en.wikipedia.org/wiki/Alias_method /> og
 * <a href='https://pbr-book.org/4ed/Sampling_Algorithms/The_Alias_Method' />
 */
@Slf4j
public class AliasMethod {

    private static final Random RANDOM = new SecureRandom();
    private final List<Double> sannsynlighet;
    private final List<Double> q;
    private final List<Integer> j;
    /**
     * Metoden for å sette opp alias tabellen.
     * @param prob Er listen av tall som utgjør en sannsynlighetsfordeling. Denne behøver ikke å være normalisert
     */
    public AliasMethod(List<Double> prob) {

        sannsynlighet = new ArrayList<Double>();
        q = new ArrayList<Double>();
        j = new ArrayList<Integer>();

        //Finne summen av sannsynlighetsfordeling for så normalisere sannsynligheten
        var sum = prob.stream().mapToDouble(i -> i).sum();

        for(Double i: prob) {
            sannsynlighet.add(i / sum);
        }

        int size = prob.size();

        //initialisere q og j
        // # TODO gjøre dette bedre
        for(int i=0; i<size;i++){
            q.add(0.0);
            j.add(0);
        }

        var mindre = new ArrayList<Integer>();
        var storre = new ArrayList<Integer>();

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

            j.set(liten, stor);
            q.set(stor, q.get(stor) - (1.0 - q.get(liten)));

            if (q.get(stor) < 1.0)
                mindre.add(stor);
            else
                storre.add(stor);

        }
    }

    /**
     * Metode for å gjøre selve trekkingen fra alias tabellen.
     * @return Returnerer indeksen fra sannsynlighetsfordelingen som har blitt trukket.
     */
    public int aliasDraw() {
        int k = j.size();
        int nextInt = RANDOM.nextInt(k);

        double sjekk = RANDOM.nextDouble();
        if (sjekk < q.get(nextInt))
            return nextInt;
        else
            return j.get(nextInt);
    }
}

