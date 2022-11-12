package org.example.utils.node;

public class NumberNode extends AbstractNode {
    private final double value;

    public NumberNode(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
