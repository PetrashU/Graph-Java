package graph;

import java.io.PrintWriter;
import java.util.ArrayList;

public class Path {
    double cost[];
    int last[];

    public Path(){
    }
    public double getMaxCost(){
        if (cost.length == 0){
            return 0;
        }
        double max = cost[0];
        for(int i = 0; i < cost.length; i++){
            if (cost[i] > max)
                max = cost[i];
        }
        return max;
    }
    public double getMinCost(){
        if (cost.length == 0){
            return 0;
        }
        double min = cost[0];
        for(int i = 0; i < cost.length; i++){
            if (cost[i] < min)
                min = cost[i];
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
            weight.add(graph.weights[i * graph.col * graph.row + last[i]]);
            i = last[i];
        }
        for (int j = path.size() - 1; j > 0; j--) {
            w.print( path.get(j) +" -" + weight.get(j-1) + "- ");
        }
        w.println( path.get(0));
        w.println( "Długość ścieżki równa " + cost[node]);
    }
}
