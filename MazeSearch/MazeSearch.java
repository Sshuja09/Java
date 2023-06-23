// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 6
 * Name: Sajja Syed
 * Username: syedsajj
 * ID: 300551462
 */

import ecs100.UI;
import java.awt.*;
import java.util.*;

/**
 * Search for a path to the goal in a maze.
 * The maze consists of a graph of MazeCells:
 *  Each cell has a collection of neighbouring cells.
 *  Each cell can be "visited" and it will remember that it has been visited
 *  A MazeCell is Iterable, so that you can iterate through its neighbour cells with
 *    for(MazeCell neighbour : cell){....
 *
 * The maze has a goal cell (shown in green, two thirds towards the bottom right corner)
 * The maze.getGoal() method returns the goal cell of the maze.
 * The user can click on a cell, and the program will search for a path
 * from that cell to the goal.
 * 
 * Every cell that is looked at during the search is coloured  yellow, and then,
 * if the cell turns out to be on a dead end, it is coloured red.
 */

public class MazeSearch {

    private Maze maze;
    private String search = "first";   // "first", "all", or "shortest"
    private int pathCount = 0;
    private boolean stopNow = false;


    /** CORE
     * Search for a path from a cell to the goal.
     * Return true if we got to the goal via this cell (and don't
     *  search for any more paths).
     * Return false if there is not a path via this cell.
     * 
     * If the cell is the goal, then we have found a path - return true.
     * If the cell is already visited, then abandon this path - return false.
     * Otherwise,
     *  Mark the cell as visited, and colour it yellow [and pause: UI.sleep(delay);]
     *  Recursively try exploring from the cell's neighbouring cells, returning true
     *   if a neighbour leads to the goal
     *  If no neighbour leads to a goal,
     *    colour the cell red (to signal failure)
     *    abandon the path - return false.
     */
    public boolean exploreFromCell(MazeCell cell) {
        if (cell == maze.getGoal()) {
            cell.draw(Color.blue);   // to indicate finding the goal
            return true;
        }
        /*# YOUR CODE HERE */
        cell.visit(); // Mark the cell visited
        cell.draw(Color.yellow); // To indicate the cell is visited
        UI.sleep(delay);
        for(MazeCell neighbour: cell){
            if(!neighbour.isVisited() && exploreFromCell(neighbour)) return true;
        }
        cell.draw(Color.red); // To indicate a dead end
        cell.unvisit(); // Marks the cell as unvisited
        UI.sleep(delay);
        return false;
    }

    /** COMPLETION
     * Search for all paths from a cell,
     * If we reach the goal, then we have found a complete path,
     *  so pause for 1000 milliseconds 
     * Otherwise,
     *  visit the cell, and colour it yellow [and pause: UI.sleep(delay);]
     *  Recursively explore from the cell's neighbours, 
     *  unvisit the cell and colour it white.
     * 
     */
    public void exploreFromCellAll(MazeCell cell) {
        if (stopNow) { return; }    // exit if user clicked the stop now button
        /*# YOUR CODE HERE */

        if (cell == maze.getGoal()) {
            cell.draw(Color.blue);   // to indicate finding the goal
            UI.sleep(1000);
            cell.draw(Color.green); // Reset the goal
        }
        else{

            cell.visit(); // Marks the cell as visited
            cell.draw(Color.yellow); // To indicate the cell is visited
            UI.sleep(delay);
            for (MazeCell neighbour : cell) {
                if (!neighbour.isVisited()) {
                    exploreFromCellAll(neighbour);
               }
                }
            cell.draw(Color.white); // To indicate the call as unvisited
            cell.unvisit(); // Marks the cell as unvisited
            UI.sleep(delay);
            }
    }

    /** CHALLENGE
     * Search for shortest path from a cell,
     * Use Breadth first search.
     */
    public void exploreFromCellShortest(MazeCell start) {
        /*# YOUR CODE HERE */
        Map<MazeCell, MazeCell> parentNode = new HashMap<MazeCell, MazeCell>(); // Keeps track of parent cell
        MazeCell current = null; // Keep track of current cell
        Queue<MazeCell> toVisit = new ArrayDeque<MazeCell>();
        parentNode.put(start, null); // Sets the parent cell of the root cell to null
        toVisit.offer(start);

        while(!toVisit.isEmpty()){
            current = toVisit.poll(); // Updates the current cell to the next cell in the queue
            current.visit(); // Marks the current cell as visited
            if (current == maze.getGoal()) { // IF the current cell is the goal
                current.draw(Color.blue);   // to indicate finding the goal
                break;
            }

            for(MazeCell neighbour: current){ // Loops over the neighbour of the current cell
                if(!neighbour.isVisited()) { // If the neighbour is not already visited
                    toVisit.offer(neighbour);
                    parentNode.put(neighbour, current);
                }
            }
            UI.sleep(delay);
        }

        while(parentNode.get(current) != null){ // while current cell is not the root cell
            current = parentNode.get(current); // Updates the current cell to the parent of the current cell
            current.draw(Color.ORANGE);
            UI.sleep(delay);
        }
    }

    /**
     * EXTRA METHOD
     * Search for shortest path from a cell
     * Allows the user to visualize the different search paths
     */
    public void exploreFromCellShortestWithColor(MazeCell start) {
        /*# YOUR CODE HERE */
        Map<MazeCell, MazeCell> parentNode = new HashMap<MazeCell, MazeCell>(); // Keeps track of parent cell
        MazeCell current = null; // Keep track of current cell
        Queue<MazeCell> toVisit = new ArrayDeque<MazeCell>();
        parentNode.put(start, null); // Sets the parent cell of the root cell to null
        toVisit.offer(start);

        while(!toVisit.isEmpty()){
            current = toVisit.poll(); // Updates the current cell to the next cell in the queue
            current.visit(); // Marks the current cell as visited
            if (current == maze.getGoal()) { // IF the current cell is the goal
                current.draw(Color.blue);   // to indicate finding the goal
                break;
            }
            current.draw(Color.yellow);
            for(MazeCell neighbour: current){ // Loops over the neighbours of the current cell
                if(!neighbour.isVisited()) {  // If the neighbour is not already visited
                    toVisit.offer(neighbour);
                    parentNode.put(neighbour, current);
                }
            }
            UI.sleep(7);
        }

        while(parentNode.get(current) != null){ // while current cell is not the root cell
            current = parentNode.get(current); // Updates the current cell to the parent of the current cell
            current.draw(Color.ORANGE);
            UI.sleep(44);
        }
    }

    //=================================================

    // fields for gui.
    private int delay = 20;
    private int size = 10;
    /**
     * Set up the interface
     */
    public void setupGui(){
        UI.addButton("New Maze", this::makeMaze);
        UI.addSlider("Maze Size", 4, 40,10, (double v)->{size=(int)v;});
        UI.setMouseListener(this::doMouse);
        UI.addButton("First path",    ()->{search="first";});
        UI.addButton("All paths",     ()->{search="all";});
        UI.addButton("Shortest path", ()->{search="shortest";});
        UI.addButton("Shortest path Cooler version", ()->{search="shortestWithColor";});
        UI.addButton("Stop",          ()->{stopNow=true;});
        UI.addSlider("Speed", 1, 101,80, (double v)->{delay=(int)(100-v);});
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.);
    }

    /**
     * Creates a new maze and draws it .
     */
    public void makeMaze(){
        maze = new Maze(size);
        maze.draw();
    }

    /**
     * Clicking the mouse on a cell should make the program
     * search for a path from the clicked cell to the goal.
     */
    public void doMouse(String action, double x, double y){
        if (action.equals("released")){
            maze.reset();
            maze.draw();
            pathCount = 0;
            MazeCell start = maze.getCellAt(x, y);
            if (search=="first"){
                exploreFromCell(start);
            }
            else if (search=="all"){
                stopNow=false;
                exploreFromCellAll(start);
            }
            else if (search=="shortest"){
                exploreFromCellShortest(start);
            }
            else if (search=="shortestWithColor"){
                exploreFromCellShortestWithColor(start);
            }
        }
    }

    public static void main(String[] args) {
        MazeSearch ms = new MazeSearch();
        ms.setupGui();
        ms.makeMaze();
    }
}

