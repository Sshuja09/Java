// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 6
 * Name: Sajja Syed
 * Username: syedsajj
 * ID: 300551462
 */

import ecs100.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;

public class BusNetworks {

    /**
     * Map of towns, indexed by their names
     */
    private Map<String, Town> busNetwork = new HashMap<>();

    /** CORE
     * Loads a network of towns from a file.
     * Constructs a Set of Town objects in the busNetwork field
     * Each town has a name and a set of neighbouring towns
     * First line of file contains the names of all the towns.
     * Remaining lines have pairs of names of towns that are connected.
     */
    public void loadNetwork(String filename) {
        try {
            busNetwork.clear();
            UI.clearText();
            List<String> lines = Files.readAllLines(Path.of(filename));
            String firstLine = lines.remove(0);

            /*# YOUR CODE HERE */
            // Creates town objects
            Scanner sc = new Scanner(firstLine);
            while(sc.hasNext()){
                String townName = sc.next(); // Gets the next town name and saves it
                busNetwork.put(townName, new Town(townName)); // Creates and adds the town object with the name
            }

            // Establish connections between towns
            for(String line: lines){
                Scanner scanner = new Scanner(line);
                Town town1 = busNetwork.get(scanner.next()); // Gets the first town from the busNetwork Map
                Town town2 = busNetwork.get(scanner.next()); // Gets the second town from the busNetwork Map
                town1.addNeighbour(town2); // Adds the second town to the first town's neighbours
                town2.addNeighbour(town1); // Adds the first town to the second town's neighbours
            }

            UI.println("Loaded " + busNetwork.size() + " towns:");

        } catch (IOException e) {throw new RuntimeException("Loading data.txt failed" + e);}
    }

    /**  CORE
     * Print all the towns and their neighbours:
     * Each line starts with the name of the town, followed by
     *  the names of all its immediate neighbours,
     */
    public void printNetwork() {
        UI.println("The current network: \n====================");
        /*# YOUR CODE HERE */
        for(Town town: busNetwork.values()){ // Goes through the Town objects
            UI.print(town.getName() + " -> ");
            for(Town neighbour: town.getNeighbours()){ // Goes through the neighbours of the town
                UI.print(neighbour.getName() + ", ");
            }
            UI.println(); // Move the cursor to the next row
        }
    }

    /** COMPLETION
     * Return a set of all the nodes that are connected to the given node.
     * Traverse the network from this node in the standard way, using a
     * visited set, and then return the visited set
     */

    public Set<Town> findAllConnected(Town town) {
        /*# YOUR CODE HERE */
            Set<Town> connected = new HashSet<>();
            connected.add(town); // Adds the town to the set so that the main function will only add the neighbours
            findAllConnected(town, connected);
            connected.remove(town); // Removes the town from the set
            return connected; // Returns the towns that are connected to the given town
    }

    public void findAllConnected(Town town, Set<Town> connected) {
        /*# YOUR CODE HERE */
        for (Town neighbour : town.getNeighbours()) { // Goes through the town's neighbours
            if (!connected.contains(neighbour)) { // Only if the neighbour has not been visited
                connected.add(neighbour); // Adds the neighbour to the connected set
                findAllConnected(neighbour, connected);
            }
        }
    }

    /**  COMPLETION
     * Print all the towns that are reachable through the network from
     * the town with the given name.
     * Note, do not include the town itself in the list.
     */
    public void printReachable(String name){
        Town town = busNetwork.get(name);
        if (town==null){
            UI.println(name+" is not a recognised town");
        }
        else {
            UI.println("\nFrom "+town.getName()+" you can get to:");
            /*# YOUR CODE HERE */
            for(Town reachable: findAllConnected(town)){ // Goes through the reachable towns
                UI.println(reachable);
            }

        }

    }

    /**  COMPLETION
     * Print all the connected sets of towns in the busNetwork
     * Each line of the output should be the names of the towns in a connected set
     * Works through busNetwork, using findAllConnected on each town that hasn't
     * yet been printed out.
     */
    public void printConnectedGroups() {
        UI.println("Groups of Connected Towns: \n================");
        int groupNum = 1;

        //*# YOUR CODE HERE *//
        Set<Town> visited = new HashSet<>(); // To keep track of the visited towns

        for (Town town : busNetwork.values()) { // Goes through each town object

            if (!visited.contains(town)) { // Checks if the town has been visited or not
                UI.printf("Group %d: ", groupNum);

                UI.print(town.getName() + ", "); // Prints the town name

                for (Town neighbour : findAllConnected(town)) { // Goes through the reachable towns from the given town
                    UI.print(neighbour.getName() + ", "); // Prints the town's neighbour
                    visited.add(neighbour); // Adds the neighbour to the visited set
                }

                UI.println(); // Moves the cursor to the next line
                groupNum++; // Increment the group number
                visited.add(town); // Adds the town to the visited
            }
        }
    }

    /**
     * Set up the GUI (buttons and mouse)
     */
    public void setupGUI() {
        UI.addButton("Load", ()->{loadNetwork(UIFileChooser.open());});
        UI.addButton("Print Network", this::printNetwork);
        UI.addTextField("Reachable from", this::printReachable);
        UI.addButton("All Connected Groups", this::printConnectedGroups);
        UI.addButton("Clear", UI::clearText);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1100, 500);
        UI.setDivider(1.0);
        loadNetwork("data-small.txt");
    }

    // Main
    public static void main(String[] arguments) {
        BusNetworks bnw = new BusNetworks();
        bnw.setupGUI();
    }

}
