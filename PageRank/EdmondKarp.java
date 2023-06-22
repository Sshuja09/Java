
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map;
import javafx.util.Pair;

/** Edmond karp algorithm to find augmentation paths and network flow.
 * 
 * This would include building the supporting data structures:
 * 
 * a) Building the residual graph(that includes original and backward (reverse) edges.)
 *     - maintain a map of Edges where for every edge in the original graph we add a reverse edge in the residual graph.
 *     - The map of edges are set to include original edges at even indices and reverse edges at odd indices (this helps accessing the corresponding backward edge easily)
 *     
 *     
 * b) Using this residual graph, for each city maintain a list of edges out of the city (this helps accessing the neighbours of a node (both original and reverse))

 * The class finds : augmentation paths, their corresponing flows and the total flow
 * 
 * 
 */

public class EdmondKarp {
    // class members

    //data structure to maintain a list of forward and reverse edges - forward edges stored at even indices and reverse edges stored at odd indices
    private static Map<String,Edge> edges; 

    // Augmentation path and the corresponding flow
    private static ArrayList<Pair<ArrayList<String>, Integer>> augmentationPaths =null;

    
    //TODO:Build the residual graph that includes original and reverse edges 
    public static void computeResidualGraph(Graph graph){
        // TODO
        edges = new HashMap<>();

        // Build the residual graph by adding reverse edges for each original edge
        int index = 0;
        for (Edge edge : graph.getOriginalEdges()) {
            String key = Integer.toString(index); // Key for the original edge

            // Add the original edge
            edges.put(key, edge);

            // Add the reverse edge
            String reverseKey = Integer.toString(index + 1); // Key for the reverse edge
            Edge reverseEdge = new Edge(edge.toCity(), edge.fromCity(), edge.transpType(), edge.flow(), 0); // Reverse edge
            edges.put(reverseKey, reverseEdge);

            // Update each city's list of outgoing edges to include both original and reverse edges
            edge.toCity().addEdgeId(reverseKey); // Adding the reverse edge id to the target city (Target -> Start)
            edge.fromCity().addEdgeId(key); // Adding the forward edge id to the start city (Start -> Target)

            index += 2;
        }
        // printResidualGraphData(graph);  //may help in debugging
        // END TODO
    }

    // Method to print Residual Graph 
    public static void printResidualGraphData(Graph graph){
        System.out.println("\nResidual Graph");
        System.out.println("\n=============================\nCities:");
        for (City city : graph.getCities().values()){
            System.out.print(city.toString());

            // for each city display the out edges 
            for(String eId: city.getEdgeIds()){
                System.out.print("["+eId+"] ");
            }
            System.out.println();
        }
        System.out.println("\n=============================\nEdges(Original(with even Id) and Reverse(with odd Id):");
        edges.forEach((eId, edge)->
                System.out.println("["+eId+"] " +edge.toString()));

        System.out.println("===============");
    }

    //=============================================================================
    //  Methods to access data from the graph. 
    //=============================================================================
    /**
     * Return the corresonding edge for a given key
     */

    public static Edge getEdge(String id){
        return edges.get(id);
    }

    /** find maximum flow
     * 
     */
    // TODO: Find augmentation paths and their corresponding flows
    public static ArrayList<Pair<ArrayList<String>, Integer>> calcMaxflows(Graph graph, City from, City to) {
        //TODO
        augmentationPaths = new ArrayList<>(); // List of augmentation paths
        computeResidualGraph(graph);

        Pair<ArrayList<String>, Integer> augmentationPath = bfs(graph, from, to);// The current augmentation path
        while(augmentationPath.getKey() != null){ // If augmentation path is not null
            augmentationPaths.add(augmentationPath); // Add augmentation path to the list
            updateResidualGraph(augmentationPath.getKey(), augmentationPath.getValue()); // Update the residual graph with the bottleneck flow
            augmentationPath = bfs(graph, from, to); // Find the next augmentation path
            }

        // END TODO
        return augmentationPaths;
    }


    // TODO:Use BFS to find a path from s to t along with the correponding bottleneck flow
    public static Pair<ArrayList<String>, Integer>  bfs(Graph graph, City s, City t) {

        ArrayList<String> augmentationPath = new ArrayList<String>();
        HashMap<String, String> backPointer = new HashMap<String, String>();
        // TODO
        Queue<City> queue = new LinkedList<>();
        queue.add(s);
        for(String id: edges.keySet()){
            backPointer.put(id, null);
        }

        while (!queue.isEmpty()) {
            City current = queue.poll();

            for(String edgeId: current.getEdgeIds()){ // Loop through the edges of the current city
                Edge edge = getEdge(edgeId); // Get the edge
                City destination = edge.toCity(); // Get the destination of that edge
                // If the destination is not source, destination hasn't already been visited, edge's capacity is not 0
                if((!destination.getId().equals(s.getId())) && (backPointer.get(destination.getId()) == null) && (edge.capacity() != 0)){
                    backPointer.put(destination.getId(), edgeId); // Add to backPointer
                    if(destination.getId().equals(t.getId())){ // If the current city is the target city
                        String pathEdgeId = backPointer.get(t.getId()); // Get the previous city id
                        while(pathEdgeId != null){ // If it's not null
                            augmentationPath.add(pathEdgeId); // Add the city id to the augmentation path
                            Edge pathEdge = getEdge(pathEdgeId);
                            pathEdgeId = backPointer.get(pathEdge.fromCity().getId());
                        }
                        Collections.reverse(augmentationPath);
                        Integer flow = findBottleneck(augmentationPath); // Get the bottleneck
                        return new Pair<>(augmentationPath, flow);
                    }
                    queue.add(destination);
                }
            }
        }
        // END TODO
        return new Pair(null,0);
    }

    public static int findBottleneck(ArrayList<String> augmentationPath) {
        int minCapacity = Integer.MAX_VALUE;
        for (String edgeId : augmentationPath) {
            Edge edge = getEdge(edgeId);
            minCapacity = Math.min(minCapacity, edge.capacity());
        }
        return minCapacity;
    }

    public static void updateResidualGraph(ArrayList<String> augmentationPath, int bottleneckFlow){
        for(String id: augmentationPath){
            Edge forwardEdge = getEdge(id);
            forwardEdge.setFlow(forwardEdge.flow() + bottleneckFlow);
            forwardEdge.setCapacity(forwardEdge.capacity() - bottleneckFlow);

            int reverseIndex = Integer.parseInt(id) + 1;
            String reverseId = Integer.toString(reverseIndex);

            Edge reverseEge = getEdge(reverseId);
            reverseEge.setCapacity(reverseEge.capacity() + bottleneckFlow);

        }

    }


}


