/**
 * AStar search (and Dijkstra search) uses a priority queue of partial paths
 * that the search is building.
 * Each partial path needs several pieces of information, to specify
 * the path to that point, its cost so far, and its estimated total cost
 */

public class PathItem implements Comparable<PathItem> {

    // TODO
    private Stop node;
    private Edge edge;
    private double totalCost;
    private double estimateCost;

    public PathItem(Stop s, Edge e, Double t, Double c) {
        this.node = s;
        this.edge = e;
        this.totalCost = t;
        this.estimateCost = c;
    }

    public Stop getStop() {
        return node;
    }

    public Edge getEdge() {
        return edge;
    }

    public double getCost() {
        return totalCost;
    }

    public double geEstimate() {
        return estimateCost;
    }

    @Override
    public int compareTo(PathItem O) {
        if (this.estimateCost > O.estimateCost) {
            return 1;
        } else if (this.estimateCost < O.estimateCost) {
            return -1;
        } else {
            return 0;
        }
    }

}
