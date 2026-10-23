package no.nav.dolly.libs.syntbean;

import java.util.random.RandomGenerator;

class CumulativeWeights {

    private final double[] cumulative;

    CumulativeWeights(double[] weights) {

        if (weights == null || weights.length == 0) {
            throw new IllegalArgumentException("Weights must not be empty");
        }
        this.cumulative = new double[weights.length];
        var sum = 0.0;
        for (int index = 0; index < weights.length; index++) {
            if (!(weights[index] >= 0) || Double.isInfinite(weights[index])) {
                throw new IllegalArgumentException("Weight at index " + index + " is invalid");
            }
            sum += weights[index];
            cumulative[index] = sum;
        }
        if (!(sum > 0)) {
            throw new IllegalArgumentException("Weights must sum to a positive number");
        }

    }

    int size() {
        return cumulative.length;
    }

    int sampleIndex(RandomGenerator random) {

        var target = random.nextDouble() * cumulative[cumulative.length - 1];
        var low = 0;
        var high = cumulative.length - 1;
        while (low < high) {
            var middle = (low + high) >>> 1;
            if (cumulative[middle] > target) {
                high = middle;
            } else {
                low = middle + 1;
            }
        }
        return low;

    }

}
