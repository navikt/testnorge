package no.nav.dolly.synt.meldekort.bean;

import java.util.random.RandomGenerator;

class WeightedChoice {

    private final Object[] values;
    private final CumulativeWeights weights;

    WeightedChoice(BeanModelDocument.WeightedValuesDocument document) {

        if (document == null || document.values() == null || document.values().isEmpty()) {
            throw new IllegalArgumentException("Categorical distribution has no values");
        }
        this.values = document.values().toArray();
        this.weights = new CumulativeWeights(document.weights());
        if (weights.size() != values.length) {
            throw new IllegalArgumentException("Categorical distribution must have one weight per value");
        }

    }

    Object sample(RandomGenerator random) {
        return values[weights.sampleIndex(random)];
    }

}
