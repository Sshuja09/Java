import java.util.*;

//=============================================================================
//   TODO   Finding Components
//   Finds all the strongly connected subgraphs in the graph
//   Labels each stop with the number of the subgraph it is in
//   sets the subGraphCount of the graph to the number of subgraphs.
//   Uses Kosaraju's_algorithm   (see lecture slides, based on
//   https://en.wikipedia.org/wiki/Kosaraju%27s_algorithm)
//=============================================================================

public class Components{

    // Use a visited set to record which stops have been visited
    // If using Kosaraju's, us a Map<Stop,Stop> to record the root node of each stop.

    
    public static void findComponents(Graph graph) {
        System.out.println("calling findComponents");
        graph.resetSubGraphIds();

        Set<Stop> visited = new HashSet<>(); // To keep track of the visited nodes
        Stack<Stop> fringe = new Stack<>(); // To search in backward order

        // Forward search
        for (Stop s : graph.getStops()) {
            if (!visited.contains(s)) {
                dfs1(s, visited, fringe);
            }
        }

        // Backward search
        visited.clear();
        int id = 0;
        while (!fringe.isEmpty()) {
            Stop stop = fringe.pop();
            if (!visited.contains(stop)) {
                dfs2(stop, visited, id);
                id++;
            }
        }

        graph.setSubGraphCount(id);
    }

    private static void dfs1(Stop stop, Set<Stop> visited, Stack<Stop> fringe) {
        visited.add(stop);
        for (Edge edge : stop.getForwardEdges()) {
            if (!visited.contains(edge.toStop())) { // Checks if the neighbour has been visited or not
                dfs1(edge.toStop(), visited, fringe); // Search from the neighbour
            }
        }
        fringe.push(stop);
    }

    private static void dfs2(Stop stop, Set<Stop> visited, int id) {
        visited.add(stop);
        stop.setSubGraphId(id);
        for (Edge edge : stop.getBackwardEdges()) {
            if (!visited.contains(edge.fromStop())) { // Checks if the neighbour has been visited
                dfs2(edge.fromStop(), visited, id); // Search from the neighbour
            }
        }
    }
}