package org.example.utils.node;

public class SignNode extends AbstractNode {
    public Type signType;

    public SignNode(char type) {
        setSign(type);
    }

    public void setSign(char type) {
        switch (type) {
            case '+' -> this.signType = Type.Plus;
            case '-' -> this.signType = Type.Minus;
            case '*' -> this.signType = Type.Multiply;
            case '/' -> this.signType = Type.Divide;
            case '^' -> this.signType = Type.Pow;
            default -> this.signType = Type.Invalid;
        }
    }

    public enum Type {
        Plus,
        Minus,
        Multiply,
        Divide,
        Pow,
        Invalid
    }
}
