package org.example;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.example.utils.FunctionResolver;

import org.example.utils.node.*;

public class Expression {
    private AbstractNode root;
    private final HashMap<String, Double> varNames;

    public Expression() {
        varNames = new HashMap<>();
    }

    public void parse(String expression) throws Exception {
        validate(expression);

        String expr = Pattern.compile("\\s").matcher(expression).replaceAll("");

        root = _parse(expr);
    }

    public double getresult() throws Exception {
        if (root == null) {
            return 0;
        }

        return getvalue(root);
    }

    public double getresult(String expression) throws Exception {
        parse(expression);

        return getvalue(root);
    }

    public void definevariable(String name, double value) throws Exception {
        if (!varNames.containsKey(name)) {
            throw new Exception("Unknown variable: " + name);
        }

        varNames.put(name, value);
    }

    public boolean validate(String expression) throws Exception {
        Pattern notAllowedSymbolsPattern = Pattern.compile("[^a-zA-Z0-9-+*/^()\\[\\]\\s]");
        Matcher notAllowedSymbols = notAllowedSymbolsPattern.matcher(expression);

        if (notAllowedSymbols.find()) {
            throw new Exception("Wrong format. Allowed symbols: a-z, A-Z, 0-9, +, -, *, /, ^");
        }

        Stack<Character> brackets = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == '(' || c == '[') {
                brackets.push(c);
            }

            if (c == ')') {
                if (brackets.peek() != '(') {
                    throw new Exception("Expected ']', actual ')' at pos " + i);
                }

                brackets.pop();
            }

            if (c == ']') {
                if (brackets.peek() != '[') {
                    throw new Exception("Expected ')', actual ']' at pos " + i);
                }

                brackets.pop();
            }
        }

        if (brackets.size() != 0) {
            throw new Exception("There are unclosed parenthesis");
        }

        return true;
    }

    private AbstractNode _parse(String expr) throws Exception {

        AbstractNode n;

        int i = expr.length() - 1;
        int brackets = 0;
        while (i >= 0 && (expr.charAt(i) != '+' && expr.charAt(i) != '-' || brackets != 0)) {
            if (expr.charAt(i) == '(' || expr.charAt(i) == '[') {
                brackets++;
            } else if (expr.charAt(i) == ')' || expr.charAt(i) == ']') {
                brackets--;
            }

            i--;
        }

        if (i > 0) {
            n = new SignNode(expr.charAt(i));
            n.left = _parse(removeBrackets(expr.substring(0, i)));
            n.right = _parse(removeBrackets(expr.substring(i + 1)));

            return n;
        }


        int j = expr.length() - 1;
        brackets = 0;
        while (j >= 0 && (expr.charAt(j) != '*' && expr.charAt(j) != '/' || brackets != 0)) {
            if (expr.charAt(j) == '(' || expr.charAt(j) == '[') {
                brackets++;
            } else if (expr.charAt(j) == ')' || expr.charAt(j) == ']') {
                brackets--;
            }

            j--;
        }

        if (j > 0) {
            n = new SignNode(expr.charAt(j));
            n.left = _parse(removeBrackets(expr.substring(0, j)));
            n.right = _parse(removeBrackets(expr.substring(j + 1)));

            return n;
        } else if (j == 0) {
            throw new Exception("Unexpected '" + expr.charAt(j + 1) + "'");
        }

        j = expr.length() - 1;
        brackets = 0;
        while (j >= 0 && (expr.charAt(j) != '^' || brackets != 0)) {
            if (expr.charAt(j) == '(' || expr.charAt(j) == '[') {
                brackets++;
            } else if (expr.charAt(j) == ')' || expr.charAt(j) == ']') {
                brackets--;
            }

            j--;
        }

        if (j > 0) {
            n = new SignNode(expr.charAt(j));
            n.left = _parse(removeBrackets(expr.substring(0, j)));
            n.right = _parse(removeBrackets(expr.substring(j + 1)));

            return n;
        } else if (j == 0) {
            throw new Exception("Unexpected '" + expr.charAt(j + 1) + "'");
        }

        if (Character.isAlphabetic(expr.charAt(0))) {
            varNames.put(expr, null);
            n = new VarNode(expr);
        } else {
            n = new NumberNode(Double.parseDouble(expr));
        }

        return n;

    }

    private double getvalue(AbstractNode node) throws Exception {
        if (SignNode.class.equals(node.getClass())) {
            SignNode n = (SignNode) node;
            switch (n.signType) {
                case Plus -> {
                    return getvalue(n.left) + getvalue(n.right);
                }
                case Minus -> {
                    return getvalue(n.left) - getvalue(n.right);
                }
                case Multiply -> {
                    return getvalue(n.left) * getvalue(n.right);
                }
                case Divide -> {
                    double r = getvalue(n.right);
                    if (r == 0.0) {
                        throw new Exception("Dividing by zero");
                    }
                    return getvalue(n.left) / r;
                }
                case Pow -> {
                    return Math.pow(getvalue(n.left), getvalue(n.right));
                }
            }
        } else if (NumberNode.class.equals(node.getClass())) {
            NumberNode n = (NumberNode) node;
            return n.getValue();
        } else if (VarNode.class.equals(node.getClass())) {
            VarNode n = (VarNode) node;
            Double value = this.varNames.get(n.getToken());
            if (value != null) {
                return value;
            } else {
                throw new Exception("Uninitialized variable: " + n.getToken());
            }
        } else if (FunctionNode.class.equals(node.getClass())) {
            FunctionNode n = (FunctionNode) node;

            if (!FunctionResolver.getAvailableFunctions().contains(n.getName())) {
                throw new Exception("Unknown function: " + n.getName());
            }

            try {
                return FunctionResolver.call(n.getName(), n.parameters);
            } catch (NoSuchMethodException e) {
                throw new Exception("Unknown function: " + n.getName());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new Exception("Cannot call function: " + n.getName() + "\n" + e.getMessage());
            }
        }

        throw new Exception("Unknown error");
    }

    private String removeBrackets(String expr) throws Exception {
        while ((expr.charAt(0) == '(' || expr.charAt(0) == '[') &&
                getPosOfBracket(expr) == expr.length() - 1) {
            expr = expr.substring(1, expr.length() - 1);
        }

        return expr;
    }

    private int getPosOfBracket(String expr) throws Exception {
        if (expr.charAt(0) != '(' && expr.charAt(0) != '[') {
            return 0;
        }

        int brackets = 0;

        for (int i = 0; i < expr.length(); i++) {
            if (expr.charAt(i) == '(' || expr.charAt(i) == '[') {
                brackets++;
            } else if (expr.charAt(i) == ')' || expr.charAt(i) == ']') {
                brackets--;
            }

            if (brackets == 0) {
                return i;
            }
        }

        throw new Exception("Unknown error on parsing brackets");
    }


}

