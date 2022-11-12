package org.example;

public class Main {
    public static void main(String[] args) {
        Expression calc = new Expression();

        try {
            calc.parse("(2 + var) ^ 2");
            calc.definevariable("var", 10);
            System.out.println("(2 + var) ^ 2 = " + calc.getresult());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}