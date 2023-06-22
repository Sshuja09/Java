import java.util.Collections;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

//=============================================================================
//   TODO   Finding Articulation Points
//   Finds all the articulation points in the undirected graph, without walking edges
//   Labels each stop with the number of the subgraph it is in
//   sets the subGraphCount of the graph to the number of subgraphs.
//=============================================================================

public class ArticulationPoints{


    // Find all the subgraphs in the graph
    private static Set<Set<Stop>> findSubGraphs(Graph graph) {
        Set<Stop> visited = new HashSet<>();
        Set<Set<Stop>> subGraphs = new HashSet<>();

        // Use depth-first search to find all the stops in each subgraph
        for (Stop stop : graph.getStops()) {
            if (!visited.contains(stop)) {
                Set<Stop> subGraph = new HashSet<>();
                findSubGraph(stop, visited, subGraph);
                subGraphs.add(subGraph);
            }
        }
        return subGraphs;
    }

    // Depth-first search algorithm to find all stops in the subgraph
    private static void findSubGraph(Stop stop, Set<Stop> visited, Set<Stop> subGraph) {
        visited.add(stop);
        subGraph.add(stop);

        for (Stop neighbour : stop.getNeighbours()) {
            if (!visited.contains(neighbour)) {
                findSubGraph(neighbour, visited, subGraph);
            }
        }
    }

    // Based on....

    // Returns the collection of nodes that are articulation points
    // in the UNDIRECTED graph with no walking edges.
    //
    public static Collection<Stop> findArticulationPoints(Graph graph) {
        System.out.println("calling findArticulationPoints");
        graph.computeNeighbours();   // To ensure that all stops have a set of (undirected) neighbour stops

        Map<Stop, Integer> depths = new HashMap<>(); // A map to store the depth of each visited stop
        Set<Stop> articulationPoints = new HashSet<>(); // A set to store the articulation points found

        // Find all the subgraphs
        Set<Set<Stop>> subGraphs = findSubGraphs(graph);

        // Set all depths to infinity (i.e., unvisited)
        for (Stop n : graph.getStops()) {
            depths.put(n, Integer.MAX_VALUE);
        }

        // Apply the articulation points algorithm to each subgraph
        for (Set<Stop> subGraph : subGraphs) {
            // Choose the first stop in the subgraph as the starting node
            Stop startStop = subGraph.iterator().next();

            // Run the depth-first search algorithm and find articulation points
            int subtreeID = 0;
            depths.put(startStop, 0); // Set the depth of the starting node to 0
            for (Stop n : startStop.getNeighbours()) {
                if (depths.get(n) == Integer.MAX_VALUE) { // If the neighbour has not been visited yet
                    // Recursively find articulation points in the subtree rooted at the neighbour
                    recordAPoints(n, 1, startStop, depths, articulationPoints);
                    subtreeID++;
                }
            }

            // If starting node has more than 1 subtree, it is an articulation point
            if (subtreeID > 1) {
                articulationPoints.add(startStop);
            }
        }

        return articulationPoints;
    }






    // Recursively find articulation points in the subtree rooted at the node
    private static int recordAPoints(Stop stop, int depth, Stop fromNode, Map<Stop, Integer> depths, Set<Stop> articulationPoints){
        // Set the depth of the node and the reach back value to the current depth
        depths.put(stop, depth);
        int reachBack = depth;

        // Check all the neighbours of the node
        for(Stop n: stop.getNeighbours()){

            // Skip the parent node
            if (n != fromNode) {
                int d = depths.get(n); // Depth of the node
                if (d < Integer.MAX_VALUE) {
                    // If the neighbor has already been visited
                    reachBack = Math.min(reachBack, d);
                } else {
                    // If the neighbor hasn't been visited yet
                    // Recursively find articulation points in the subtree rooted at the neighbor
                    int childReach = recordAPoints(n, depth + 1, stop, depths, articulationPoints);
                    reachBack = Math.min(childReach, reachBack);
                    // If the subtree rooted at neighbor has no connection to ancestors of node (except fromNode), then node is an articulation point
                    if (childReach >= depth && stop != fromNode) {
                        articulationPoints.add(stop);
                    }
                }
            }
        }

        return reachBack;
    }

}
