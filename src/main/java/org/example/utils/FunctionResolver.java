package org.example.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class FunctionResolver {

    static public List<String> getAvailableFunctions() {
        return List.of(
                "sin",
                "cos",
                "tan",
                "tg",
                "cot",
                "ctg",
                "abs"
        );
    }

    static public double call(String name, List<Double> arguments) throws Exception, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<String> availableFunctions = FunctionResolver.getAvailableFunctions();

        if (!availableFunctions.contains(name)) {
            throw new Exception("Unknown function: " + name);
        }

        Method[] methods = FunctionResolver.class.getMethods();

        int argumentsCount = Arrays.stream(methods)
                .filter(method -> method.getName().equals(name))
                .map(Method::getParameterCount)
                .toList().get(0);

        if (arguments.size() < argumentsCount) {
            throw new Exception("Too few arguments. Function '" + name + "' expecting " + argumentsCount + " argument" + ((argumentsCount > 1) ? "s" : ""));
        }

        double res = 0.0;

        // temporary plug
        if (argumentsCount == 1) {
            res = (double) FunctionResolver.class.getMethod(name, double.class).invoke(FunctionResolver.class, arguments.get(0));
        } else if (argumentsCount == 2) {
            res = (double) FunctionResolver.class.getMethod(name, double.class, double.class).invoke(FunctionResolver.class, arguments.get(0), arguments.get(1));
        }

        return res;
    }

    public static double sin(double num) {
        return Math.sin(num);
    }

    public static double cos(double num) {
        return Math.cos(num);
    }

    public static double tan(double num) {
        return Math.tan(num);
    }

    public static double tg(double num) {
        return Math.tan(num);
    }

    public static double cot(double num) {
        return 1.0 / Math.tan(num);
    }

    public static double ctg(double num) {
        return 1.0 / Math.tan(num);
    }

    public static double abs(double num) {
        return Math.abs(num);
    }

}
