import javafx.util.Pair;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * Write a description of class PageRank here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class PageRank
{
    //class members 
    private static double dampingFactor = .85;
    private static int iter = 10;
    /**
     * build the fromLinks and toLinks 
     */
    //TODO: Build the data structure to support Page rank. For each edge in the graph add the corresponding cities to the fromLinks and toLinks
    public static void computeLinks(Graph graph){
        // TODO
        for(Edge edge : graph.getOriginalEdges()){
            edge.fromCity().addToLinks(edge.toCity()); // Sourcec -> Target
            edge.toCity().addFromLinks(edge.fromCity()); // Target -> Source
        }

        //printPageRankGraphData(graph);  ////may help in debugging
        // END TODO
    }

    public static void printPageRankGraphData(Graph graph){
        System.out.println("\nPage Rank Graph");

        for (City city : graph.getCities().values()){
            System.out.print("\nCity: "+city.toString());
            //for each city display the in edges 
            System.out.print("\nIn links to cities:");
            for(City c:city.getFromLinks()){

                System.out.print("["+c.getId()+"] ");
            }

            System.out.print("\nOut links to cities:");
            //for each city display the out edges 
            for(City c: city.getToLinks()){
                System.out.print("["+c.getId()+"] ");
            }
            System.out.println();;

        }    
        System.out.println("=================");
    }
    //TODO: Compute rank of all nodes in the network and display them at the console
    public static void computePageRank(Graph graph){
        // TODO
        int numberOfCities = graph.getCities().size(); // NUMber of nodes
        Map<City, Double> pageRank = new HashMap<>();

        for(City city : graph.getCities().values()){
            pageRank.put(city, 1.0/numberOfCities);
        }

        for(int i = 0; i < iter; i++){
            for(City city : graph.getCities().values()){
                double cityRank = 0;
                for(City backCity : city.getFromLinks()){
                    double neighbourShare = pageRank.get(backCity)/backCity.getToLinks().size();
                    cityRank = cityRank + neighbourShare;
                }
                cityRank = (1 - dampingFactor)/numberOfCities + dampingFactor * cityRank;
                pageRank.put(city, cityRank);
            }
        }

        System.out.printf("Iteration %d:\n\n", iter);
        for(City city : graph.getCities().values()){
            double cityRank = pageRank.get(city);
            System.out.println(city + " " + cityRank);
        }
        
        // END TODO

    }
}
