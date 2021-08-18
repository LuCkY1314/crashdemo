package com.crtip.test.r8crashdemo;


import com.crtip.test.r8crashdemo.utils.Test;

/**
 * Created by chan on 2018/1/15.
 * calculator for hotfix test.
 */

public class Calculator {

    public static final String ADDITION = "addition";
    public static final String SUBTRACTION = "subtraction";
    public static final String MULTIPLICATION = "multiplication";
    public static final String DIVISION = "division";

    public int calculate(int num1, int num2, String operation){
        Test.test(operation);
        switch (operation){
            case ADDITION:
                return num1 + num2;
            case SUBTRACTION:
                return num1 - num2;
            case MULTIPLICATION:
                return num1 * num2;
            case DIVISION:
                if (num2 == 0){
                    throw new IllegalArgumentException("Can't be divided by 0");
                }
                return num1 / num2;
            default:
                throw new IllegalArgumentException("Vague operation");
        }
    }

}
