package no.nav.dolly.libs.syntbean;

import java.util.random.RandomGenerator;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

class SynthesizedColumn {

    private static final String SAMPLE = "sample";
    private static final String CART = "cart";
    private static final int MAX_BOUNDARY_ATTEMPTS = 10_000;

    record Sample(Object value, boolean missing) {
    }

    private final String name;
    private final boolean cart;
    private final boolean continuous;
    private final boolean integerType;
    private final boolean strictBoundaries;
    private final int decimals;
    private final Double min;
    private final Double max;
    private final WeightedChoice categorical;
    private final KernelDensitySampler kernelDensity;
    private final CartModel cartModel;
    private final WeightedChoice missingChoice;
    private final CartModel missingCart;

    SynthesizedColumn(BeanModelDocument.ColumnDocument column, boolean strictBoundaries) {

        var synthesis = column.synthesis();
        this.name = column.name();
        this.continuous = !column.categorical();
        this.integerType = column.integerType();
        this.strictBoundaries = strictBoundaries;
        this.decimals = isNull(synthesis.decimals()) ? 0 : synthesis.decimals();
        this.min = synthesis.min();
        this.max = synthesis.max();
        this.cart = switch (String.valueOf(synthesis.method())) {
            case SAMPLE -> false;
            case CART -> true;
            default -> throw new IllegalArgumentException("Column %s has unknown method %s".formatted(name, synthesis.method()));
        };
        this.categorical = !cart && !continuous ? new WeightedChoice(synthesis.categorical()) : null;
        this.kernelDensity = !cart && continuous ? new KernelDensitySampler(synthesis.kde()) : null;
        this.cartModel = cart ? new CartModel(synthesis.cart(), continuous) : null;

        var missing = continuous ? synthesis.missing() : null;
        this.missingChoice = nonNull(missing) && !cart ? new WeightedChoice(missing.categorical()) : null;
        this.missingCart = nonNull(missing) && cart ? new CartModel(missing.cart(), false) : null;

    }

    int[] dependsOn() {
        return cart ? cartModel.dependsOn() : new int[0];
    }

    Sample sample(Object[] row, RandomGenerator random) {

        if (!continuous) {
            var value = cart ? cartModel.sampleCategorical(row, random) : categorical.sample(random);
            return new Sample(value, false);
        }

        var sampler = cart ? cartModel.kernelDensity(row) : kernelDensity;
        var value = sampleContinuous(sampler, random);
        if (cart) {
            value = integerType ? (Object) BeanValues.toLong(value) : (Object) BeanValues.toDouble(value);
        }
        return new Sample(value, isMissing(row, random));

    }

    private Object sampleContinuous(KernelDensitySampler sampler, RandomGenerator random) {

        var value = sampler.sample(random);
        if (strictBoundaries && nonNull(min) && nonNull(max)) {
            var attempts = 1;
            while (value < min || value > max) {
                if (attempts++ >= MAX_BOUNDARY_ATTEMPTS) {
                    throw new IllegalStateException("Could not sample column %s within [%s, %s]".formatted(name, min, max));
                }
                value = sampler.sample(random);
            }
        }
        if (decimals == 0) {
            return (long) value;
        }
        var scale = Math.pow(10, decimals);
        return Math.rint(value * scale) / scale;

    }

    private boolean isMissing(Object[] row, RandomGenerator random) {

        if (nonNull(missingChoice)) {
            return BeanValues.toLong(missingChoice.sample(random)) == 1;
        }
        if (nonNull(missingCart)) {
            return BeanValues.toLong(missingCart.sampleCategorical(row, random)) == 1;
        }
        return false;

    }

}
