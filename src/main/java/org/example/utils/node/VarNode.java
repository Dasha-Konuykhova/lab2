package org.example.utils.node;

public class VarNode extends AbstractNode {
    private final String token;

    public VarNode(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
