// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 5
 * Name:Sajja Syed
 * Username: syedsajj
 * ID: 300551462
 */

import ecs100.*;

import java.util.*;

/** 
 * Calculator for Cambridge-Polish Notation expressions
 * (see the description in the assignment page)
 * User can type in an expression (in CPN) and the program
 * will compute and print out the value of the expression.
 * The template provides the method to read an expression and turn it into a tree.
 * You have to write the method to evaluate an expression tree.
 *  and also check and report certain kinds of invalid expressions
 */

public class CPNCalculator{

    /**
     * Adds the values than are in the given list and returns the total
     */
     private static double add(List<Double> args){
        double total = args.get(0); // Sets the total to the first element
        for(double arg: args.subList(1, args.size())){ // Goes through the list starting from 1 index
            total += arg; // Adds all the values to the total
        }
        return total;
    }

    /**
     * Subtracts the values than are in the given list and returns the total
     */
    private static double subtract(List<Double> args){
        double total = args.get(0); // Sets the total to the first element
        for (double arg : args.subList(1, args.size())) { // Goes through the list starting from 1 index
            total -= arg; // Subtract all the values from total
        }
        return total;
    }

    /**
     * Multiply the values than are in the given list and returns the total
     */
    private static double multiply(List<Double> args){
        double total = args.get(0); // Sets the total to the first element
        for(double arg: args.subList(1, args.size())){ // Goes through the list starting from 1 index
            total *= arg; // Multiply all the values and add them to total
        }
        return total;
    }

    /**
     * Divides the values than are in the given list and returns the total
     */
    private static double divide(List<Double> args){
        double total = args.get(0); // Sets the total to the first element
        for(double arg: args.subList(1, args.size())){ // Goes through the list starting from 1 index
            total /= arg; // Divides total with arg and save it in total
        }
        return total;
    }

    /**
     * Depending on the operator and the size of the passed in list calculates the log to either base 10 or base e
     * Prints out a message when invalid number of operands are given
     */
    private static double logHandler(String operator, List<Double> args) {
        if (args.size() > 2) { // If more than 2 operands are given
            UI.println("Too many operands for log");
            return Double.NaN;
        }

        double total = 0;
        if(operator.equals("log")){ // For log to base 10

            if(args.size() == 1){ // Log of base 10
                total = Math.log10(args.get(0));
            }else if (args.size() == 2){ // Log of the first element to the base of the second
                total = ((Math.log10(args.get(0))) / (Math.log10(args.get(1))));
            }
        }
        else if(operator.equals("ln")){ // For log to base e

            if(args.size() == 1){ // Log of base e
                total = Math.log(args.get(0));
            }else if (args.size() == 2){ // Log of the first element to the base of the second
                total = ((Math.log(args.get(0))) / (Math.log(args.get(1))));
            }
        }
        return total;
        }

    /**
     * Depending on the size of the list calculates the distance between 2 points in either 2D or 3D
     * Prints out a message when invalid number of operands are given
     */
        private static double dist(List<Double> args) {

            double x1, y1, z1, x2, y2, z2; // Variables for different values

            if (args.size() == 6 || args.size() == 4) { // If the arguments passed in are sufficient
                if (args.size() == 4) {
                    x1 = args.get(0); // Calculating distance between two points in 2D
                    y1 = args.get(1);
                    x2 = args.get(2);
                    y2 = args.get(3);
                    return Math.hypot((x1 - x2), (y1 - y2));
                }
                else { // Calculating distance between two points in 3D
                    x1 = args.get(0); // Calculating distance between two points in 3D
                    y1 = args.get(1);
                    z1 = args.get(2);
                    x2 = args.get(3);
                    y2 = args.get(4);
                    z2 = args.get(5);

                    double xDiff = x1 - x2;
                    double yDiff = y1 - y2;
                    double zDiff = z1 - z2;

                    return Math.sqrt((Math.pow(xDiff, 2)) + (Math.pow(yDiff, 2)) + (Math.pow(zDiff, 2)));
                }
            } else {

                UI.println("Wrong operands for dist"); // Report error when invalid number of operands are passed in
                return Double.NaN;
            }
        }

    /**
     * Raise the value to the value after it
     * If less than 2 values are given it will prints out a message
     */
    private static double toPower(List<Double> args){
        if(args.size() > 1){
            double total = args.get(0); // Sets the total to the first element
            for (double arg : args.subList(1, args.size())) { // Goes through the list starting from 1 index
                total = Math.pow(total, arg); // Raise total to arg and save the result in total
            }
            return total;
        }
        else{
            UI.println("At least two operands are required");
            return Double.NaN;
        }
    }

    /**
     * Returns the average of the values in the given list
     */
    private static double average(List<Double> args){
        double total = add(args); // Add all the values
        return total / args.size(); // Divides total with number of elements and returns it
    }

    /**
     * Calculate and return the sin of the given value
     */
    private static double sin(List<Double> args){
        return Math.sin(args.get(0));
    }

    /**
     * Calculate and return the cos of the given value
     */
    private static double cos(List<Double> args){
        return Math.cos(args.get(0));
    }

    /**
     * Calculate and return the tan of the given value
     */
    private static double tan(List<Double> args){
        return Math.tan(args.get(0));
    }

    /**
     * Prints out a message if an invalid operator has been passed in
     */
    private static double invalidHandler(String operator){
        UI.printf("The operator %s is invalid. ", operator);
        return Double.NaN;
    }

    /**
     * Depending on the operator calls the corresponding method
     * Calls invalidHandler if an invalid operator has passed in
     */
    private double applyOperator(String operator, List<Double> numbers){
        switch (operator) {
            case "+":
                return add(numbers);
            case "-":
                return subtract(numbers);
            case "*":
                return multiply(numbers);
            case "/":
                return divide(numbers);
            case "log":
            case "ln":
                return logHandler(operator, numbers);
            case "^":
                return toPower(numbers);
            case "avg":
                return average(numbers);
            case "dist":
                return dist(numbers);
            case "cos":
                return cos(numbers)
            ;case "tan":
                return tan(numbers);
            case "sin":
                return sin(numbers);
            default:
                return invalidHandler(operator);
        }
    }

    public double resolveConstant(String constant) {
        switch (constant) {
            case "PI":
                return Math.PI;
            case "E" :
                return Math.E;
            default:
                UI.printf("The constant %s is invalid. ", constant);
                return Double.NaN;
        }
    }
    /**
     * Setup GUI then run the calculator
     */
    public static void main(String[] args){
        CPNCalculator calc = new CPNCalculator();
        calc.setupGUI();
        calc.runCalculator();
    }

    /** Setup the gui */
    public void setupGUI(){
        UI.addButton("Clear", UI::clearText); 
        UI.addButton("Quit", UI::quit); 
        UI.setDivider(1.0);
    }

    /**
     * Run the calculator:
     * loop forever:  (a REPL - Read Eval Print Loop)
     *  - read an expression,
     *  - evaluate the expression,
     *  - print out the value
     * Invalid expressions could cause errors when reading or evaluating
     * The try-catch prevents these errors from crashing the program - 
     *  the error is caught, and a message printed, then the loop continues.
     */
    public void runCalculator(){
        UI.println("Enter expressions in pre-order format with spaces");
        UI.println("eg   ( * (  + 4 5 8 3 -10 ) 7 ( / 6 4 ) 18 )");
        while (true){
            UI.println();
            try {
                GTNode<ExpElem> expr = readExpr();
                double value = evaluate(expr);
                UI.println(" -> " + value);
            }catch(Exception e){UI.println("Something went wrong! "+e);}
        }
    }

    /**
     * Evaluate an expression and return the value
     * Returns Double.NaN if the expression is invalid in some way.
     * If the node is a number
     *  => just return the value of the number
     * or it is a named constant
     *  => return the appropriate value
     * or it is an operator node with children
     *  => evaluate all the children and then apply the operator.
     */
    public double evaluate(GTNode<ExpElem> expr){
        if (expr==null){
            return Double.NaN;
        }

        /*# YOUR CODE HERE */
        if(expr.getItem().isOperator()) {
            double number; // For leaf node value
            List<Double> operandList = new ArrayList<Double>(); // to contains the leaf node values

            for(GTNode<ExpElem> child : expr){
               number = evaluate(child); // Stores the leaf node value to number
               operandList.add(number); // Adds the value to the list
            }

            return applyOperator(expr.getItem().operator, operandList); //Calculates the leaf nodes values and returns it
        }

         else if (!expr.getItem().isOperator()) {
             return expr.getItem().value; // Returns the value of the node if it is a leaf node
        }

        return Double.NaN;
    }

    /** 
     * Reads an expression from the user and constructs the tree.
     */ 
    public GTNode<ExpElem> readExpr(){
        String expr = UI.askString("expr:");
        return readExpr(new Scanner(expr));   // the recursive reading method
    }

    /**
     * Recursive helper method.
     * Uses the hasNext(String pattern) method for the Scanner to peek at next token
     */
    public GTNode<ExpElem> readExpr(Scanner sc){
        if (sc.hasNextDouble()) {                     // next token is a number: return a new node
            return new GTNode<ExpElem>(new ExpElem(sc.nextDouble()));
        }
        else if (sc.hasNext("\\(")) {                 // next token is an opening bracket
            sc.next();                                // read and throw away the opening '('
            ExpElem opElem = new ExpElem(sc.next());  // read the operator
            GTNode<ExpElem> node = new GTNode<ExpElem>(opElem);  // make the node, with the operator in it.
            while (! sc.hasNext("\\)")){              // loop until the closing ')'
                GTNode<ExpElem> child = readExpr(sc); // read each operand/argument
                node.addChild(child);                 // and add as a child of the node
            }
            sc.next();                                // read and throw away the closing ')'
            return node;
        }
        else {                                        // next token must be a named constant (PI or E)
                                                      // make a token with the name as the "operator"
            String t = sc.next();
            return new GTNode<ExpElem>(new ExpElem(resolveConstant(t)));
        }
    }

}

