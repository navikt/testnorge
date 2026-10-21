package no.nav.dolly.synt.meldekort.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

import static java.util.Objects.isNull;

class CartModel {

    private final DecisionTree tree;
    private final int[] dependsOn;
    private final Map<Integer, WeightedChoice> categoricalLeaves = new HashMap<>();
    private final Map<Integer, KernelDensitySampler> kdeLeaves = new HashMap<>();

    CartModel(BeanModelDocument.CartDocument document, boolean continuous) {

        if (isNull(document) || isNull(document.dependsOn()) || isNull(document.leaves()) || document.leaves().isEmpty()) {
            throw new IllegalArgumentException("CART model is incomplete");
        }
        this.tree = new DecisionTree(document.tree());
        this.dependsOn = document.dependsOn().clone();
        if (dependsOn.length < tree.featureCount()) {
            throw new IllegalArgumentException("CART model uses %d features but depends on %d columns"
                    .formatted(tree.featureCount(), dependsOn.length));
        }
        document.leaves().forEach((leaf, leafDocument) -> {
            if (continuous) {
                kdeLeaves.put(leaf, new KernelDensitySampler(leafDocument.kde()));
            } else {
                categoricalLeaves.put(leaf, new WeightedChoice(leafDocument.categorical()));
            }
        });

    }

    int[] dependsOn() {
        return dependsOn.clone();
    }

    Object sampleCategorical(Object[] row, RandomGenerator random) {

        var leaf = apply(row);
        var choice = categoricalLeaves.get(leaf);
        if (isNull(choice)) {
            throw new IllegalStateException("CART model has no categorical distribution for leaf " + leaf);
        }
        return choice.sample(random);

    }

    KernelDensitySampler kernelDensity(Object[] row) {

        var leaf = apply(row);
        var kernelDensity = kdeLeaves.get(leaf);
        if (isNull(kernelDensity)) {
            throw new IllegalStateException("CART model has no kernel density for leaf " + leaf);
        }
        return kernelDensity;

    }

    int apply(Object[] row) {

        var features = new double[dependsOn.length];
        for (int index = 0; index < dependsOn.length; index++) {
            features[index] = BeanValues.toDouble(row[dependsOn[index]]);
        }
        return tree.apply(features);

    }

}
