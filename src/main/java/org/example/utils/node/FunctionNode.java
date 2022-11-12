package org.example.utils.node;

import java.util.List;

public class FunctionNode extends AbstractNode {
    private final String name;
    public List<Double> parameters;

    FunctionNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}