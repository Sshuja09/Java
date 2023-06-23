// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 4
 * Name: Sajja Syed
 * Username: syedsajj
 * ID: 300551462
 */

/**
 * Implements a decision tree that asks a user yes/no questions to determine a decision.
 * Eg, asks about properties of an animal to determine the type of animal.
 * <p>
 * A decision tree is a tree in which all the internal nodes have a question,
 * The answer to the question determines which way the program will
 * proceed down the tree.
 * All the leaf nodes have the decision (the kind of animal in the example tree).
 * <p>
 * The decision tree may be a predermined decision tree, or it can be a "growing"
 * decision tree, where the user can add questions and decisions to the tree whenever
 * the tree gives a wrong answer.
 * <p>
 * In the growing version, when the program guesses wrong, it asks the player
 * for another question that would help it in the future, and adds it (with the
 * correct answers) to the decision tree.
 */

import ecs100.*;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.awt.Color;

public class DecisionTree {

    public DTNode theTree;    // root of the decision tree;
    boolean grown = false; // Keeps track of if the tree has grown or not

    /**
     * Setup the GUI and make a sample tree
     */
    public static void main(String[] args) {
        DecisionTree dt = new DecisionTree();
        dt.setupGUI();
        dt.loadTree("sample-animal-tree.txt");
    }

    /**
     * Set up the interface
     */
    public void setupGUI() {
        UI.addButton("Load Tree", () -> {
            loadTree(UIFileChooser.open("File with a Decision Tree"));
        });
        UI.addButton("Print Tree", this::printTree);
        UI.addButton("Run Tree", this::runTree);
        UI.addButton("Grow Tree", this::growTree);
        UI.addButton("Save Tree", this::saveTree);  // for completion
        // UI.addButton("Draw Tree", this::drawTree);  // for challenge
        UI.addButton("Reset", () -> {
            loadTree("sample-animal-tree.txt");
        });
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.5);
    }

    /**
     * Create a file to save the tree to
     */
    private void saveTree() {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(UIFileChooser.save());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintStream printStream = new PrintStream(fileOutputStream);
        saveNode(theTree, printStream);

    }

    /**
     * Goes through the tree and check if the given node is a leave note or not
     * If it is not a leave node it prints the node's text as a question and recall itself
     * If the node is leave node it prints the node's text as an answer.
     */
    private void saveNode(DTNode node, PrintStream printStream) {
        if (!node.isAnswer()) {
            printStream.println("Question: " + node.getText());
            saveNode(node.getYes(), printStream);
            saveNode(node.getNo(), printStream);
        } else {
            printStream.println("Answer: " + node.getText());
        }


    }

    /**
     * Print out the contents of the decision tree in the text pane.
     * The root node should be at the top, followed by its "yes" subtree,
     * and then its "no" subtree.
     * Needs a recursive "helper method" which is passed a node.
     * <p>
     * COMPLETION:
     * Each node should be indented by how deep it is in the tree.
     * The recursive "helper method" is passed a node and an indentation string.
     * (The indentation string will be a string of space characters)
     */

    public void printNode(DTNode node, int layer) {
        String indent = "   ";

        if (node.isAnswer()) {
            UI.println(node.getText());

        } else {
            UI.println(node.getText() + "?");

            UI.print(indent.repeat(layer + 1) + "Yes: "); //Indent the line depending on the layer and print "Yes"
            printNode(node.getYes(), layer + 1);

            UI.print(indent.repeat(layer + 1) + "No: "); //Indent the line depending on the layer and print "No"
            printNode(node.getNo(), layer + 1);
        }
    }

    public void printTree() {
        UI.clearText();
        /*# YOUR CODE HERE */
        printNode(theTree, 0);


    }

    /**
     * Run the tree by starting at the top (of theTree), and working
     * down the tree until it gets to a leaf node (a node with no children)
     * If the node is a leaf it prints the answer in the node
     * If the node is not a leaf node, then it asks the question in the node,
     * and depending on the answer, goes to the "yes" child or the "no" child.
     */

    public void runNode(DTNode node) {
        if (!node.isAnswer()) {
            String answer = UI.askString("Is it True: " + node.getText() + "(Y/N)"); //Asks the question in the node and saves it in a variable
            answer = answer.toLowerCase(Locale.ROOT); //Convert every letter in the answer to lower case

            if (answer.startsWith("y")) { // If the answer starts with "Y" it call itself with the "Yes Child"
                runNode(node.getYes());

            } else if (answer.startsWith("n")) { // If the answer starts with "N" it call itself with the "No Child"
                runNode(node.getNo());

            } else {
                UI.println("Please enter 'YES' or 'NO'"); // Prints a message if the user enters invalid input
                runNode(node);
            }

        } else {
            UI.println("The answer is: " + node.getText());

        }
    }

    public void runTree() {
        /*# YOUR CODE HERE */
        UI.clearText();
        runNode(theTree);

    }

    /**
     * Grow the tree by allowing the user to extend the tree.
     * Like runTree, it starts at the top (of theTree), and works its way down the tree
     * until it finally gets to a leaf node.
     * If the current node has a question, then it asks the question in the node,
     * and depending on the answer, goes to the "yes" child or the "no" child.
     * If the current node is a leaf it prints the decision, and asks if it is right.
     * If it was wrong, it
     * - asks the user what the decision should have been,
     * - asks for a question to distinguish the right decision from the wrong one
     * - changes the text in the node to be the question
     * - adds two new children (leaf nodes) to the node with the two decisions.
     */

    private void growNode(DTNode node) {
        if (!node.isAnswer()) { //Checks if the give node is not a leave node
            String answer = UI.askString("Is it True: " + node.getText() + "(Y/N)"); //Asks the question in the node and saves it in a variable
            answer = answer.toLowerCase(Locale.ROOT); //Convert every letter in the answer to lower case

            if (answer.startsWith("y")) { // If the answer starts with "Y" it call itself with the "Yes Child"
                growNode(node.getYes());

            } else if (answer.startsWith("n")) { // If the answer starts with "N" it call itself with the "No Child"
                growNode(node.getNo());

            } else {
                UI.println("Please enter 'YES' or 'NO'"); // Prints a message if the user enters invalid input
                growNode(node);
            }

        } else {
            String answer = UI.askString("I think I know. Is it a " + node.getText() + "?"); // Asks if the answer is the correct answer
            answer = answer.toLowerCase(Locale.ROOT); //Change every letter in the variable to lower case

            if (answer.startsWith("n")) {

                String rightAnswer = UI.askString("OK, What should the answer be? "); // Asks for the right answer
                rightAnswer = capName(rightAnswer); //Capitalize the right answer

                UI.println("Oh, I can't distinguish a " + node.getText() + " from a " + rightAnswer);

                String distinguisher = UI.askString("Tell me something that is True for a " + rightAnswer
                        + " but not for a " + node.getText() + "\nProperty: "); // Ask for a property that is unique for the right answer
                distinguisher = capName(distinguisher); //Capitalize the distinguisher

                DTNode yesChild = new DTNode(rightAnswer); // Creates a yes child with the right answer
                DTNode noChild = new DTNode(node.getText()); // Creates a no child with the current node text

                node.setText(distinguisher); // Changes the current node text to the unique property
                node.setChildren(yesChild, noChild); // Set the children for the node
                grown = true;
            }
        }
    }

    public void growTree() {
        /*# YOUR CODE HERE */
        UI.clearText();
        //grown = false;
        growNode(theTree);
        if(grown) {
            UI.println("\nThank you!, I've updated my decision tree.");
        }else{
            UI.println("\nWho is the smart one now !!!");
        }

    }

    /**
     * Capitalize the given string
     */
    private String capName(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    // You will need to define methods for the Completion and Challenge parts.

    // Written for you

    /**
     * Loads a decision tree from a file.
     * Each line starts with either "Question:" or "Answer:" and is followed by the text
     * Calls a recursive method to load the tree and return the root node,
     * and assigns this node to theTree.
     */
    public void loadTree(String filename) {
        if (!Files.exists(Path.of(filename))) {
            UI.println("No such file: " + filename);
            return;
        }
        try {
            theTree = loadSubTree(new ArrayDeque<String>(Files.readAllLines(Path.of(filename))));
        } catch (IOException e) {
            UI.println("File reading failed: " + e);
        }
    }

    /**
     * Loads a tree (or subtree) from a Scanner and returns the root.
     * The first line has the text for the root node of the tree (or subtree)
     * It should make the node, and
     * if the first line starts with "Question:", it loads two subtrees (yes, and no)
     * from the scanner and add them as the  children of the node,
     * Finally, it should return the  node.
     */
    public DTNode loadSubTree(Queue<String> lines) {
        Scanner line = new Scanner(lines.poll());
        String type = line.next();
        String text = line.nextLine().trim();
        DTNode node = new DTNode(text);
        if (type.equals("Question:")) {
            DTNode yesCh = loadSubTree(lines);
            DTNode noCh = loadSubTree(lines);
            node.setChildren(yesCh, noCh);
        }
        return node;

    }


}
