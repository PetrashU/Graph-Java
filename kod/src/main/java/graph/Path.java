package graph;

import java.io.PrintWriter;
import java.util.ArrayList;

public class Path {
    public double[] cost;
    public int[] last;

    public Path(){
    }
    public Path(double[] cost, int[] last){
        this.cost = cost;
        this.last = last;
    }
    public double getMaxCost(){
        if (cost.length == 0){
            return 0;
        }
        double max = cost[0];
        for (double v : cost) {
            if (v > max)
                max = v;
        }
        return max;
    }
    public double getMinCost(){
        if (cost.length == 0){
            return 0;
        }
        double min = cost[0];
        for (double v : cost) {
            if (v < min)
                min = v;
        }
        return min;
    }
    public void savePath(PrintWriter w, Graph graph, int node){
        ArrayList<Integer> path = new ArrayList<>();
        ArrayList<Double> weight = new ArrayList<>();
        w.println("Najkrótsza ścieżka:");
        int i = node;
        path.add(node);
        while (last[i] != -1) {
            path.add(last[i]);
            for (Edge edge : graph.edges.get(i)){
                if (edge.fin == last[i])
                    weight.add(edge.weight);
            }
            i = last[i];
        }
        for (int j = path.size() - 1; j > 0; j--) {
            w.print( path.get(j) +" -" + weight.get(j-1) + "- ");
        }
        w.println( path.get(0));
        w.println( "Długość ścieżki równa " + cost[node]);
    }
}
