package com.lxb.flink.api.common.resources;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.lxb.flink.utl.Preconditions.checkArgument;
import static com.lxb.flink.utl.Preconditions.checkNotNull;

public abstract class Resource implements Serializable {
    private final String name;

    private final BigDecimal value;

    protected Resource(String name, double value) {
        this(name, BigDecimal.valueOf(value));
    }

    protected Resource(String name, BigDecimal value) {
        checkNotNull(value);
        checkArgument(value.compareTo(BigDecimal.ZERO) >= 0, "Resource value must be no less than 0");

        this.name = checkNotNull(name);
        this.value = value;
    }

    public Resource merge(Resource other) {
        checkNotNull(other, "Cannot merge with null resources");
        checkArgument(getClass() == other.getClass(), "Merge with different resource type");
        checkArgument(name.equals(other.name), "Merge with different resource name");

        return create(value.add(other.value));
    }

    public Resource subtract(Resource other) {
        checkNotNull(other, "Cannot subtract null resources");
        checkArgument(getClass() == other.getClass(), "Minus with different resource type");
        checkArgument(name.equals(other.name), "Minus with different resource name");
        checkArgument(value.compareTo(other.value) >= 0, "Try to subtract a larger resource from this one.");

        return create(value.subtract(other.value));
    }

    public Resource multiply(BigDecimal multiplier) {
        return create(value.multiply(multiplier));
    }

    public Resource multiply(int multiplier) {
        return multiply(BigDecimal.valueOf(multiplier));
    }

    public Resource divide(BigDecimal by) {
        return create(value.divide(by, 16, RoundingMode.DOWN));
    }

    public Resource divide(int by) {
        return divide(BigDecimal.valueOf(by));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && getClass() == o.getClass()) {
            Resource other = (Resource) o;

            // Two Resources are considered equal if the values equals disregarding the scales
            return name.equals(other.name) && value.compareTo(other.value) == 0;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("Resource(%s: %s)", name, value);
    }

    public String getName() {
        return name;
    }

    public BigDecimal getValue() {
        return value;
    }

    protected abstract Resource create(BigDecimal value);

}
