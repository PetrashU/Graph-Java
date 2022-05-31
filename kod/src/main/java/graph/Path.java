package graph;

import java.io.PrintWriter;
import java.util.ArrayList;

public class Path {
    public double[] cost;   //koszty dojścia do wierzchołków(numer wierzchołka = indeks w szeregu)
    public int[] last;      //azereg poprzedników

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
    public void savePath(PrintWriter w, Graph graph, int node){     //zapisanie do pliku
        ArrayList<Integer> path = new ArrayList<>();        //lista do odtwarzania ścieżki
        ArrayList<Double> weight = new ArrayList<>();       //lista do zapisywania odpowiednich wag
        int i = node;
        path.add(node);
        while (last[i] != -1) {   //dopóki nie osągniemy wierzchołka początkowego
            path.add(last[i]);      //dodajemy do ścieżki numer wierzchołka
            for (Edge edge : graph.edges.get(i)){       //szukamy krawędzi, która łączy obecny wierzchołek z poprzednikiem
                if (edge.fin == last[i])
                    weight.add(edge.weight);        //dodajemy wagę tej krawędzi do listy
            }
            i = last[i];        //przechodzimy do poprzednika
        }
        w.println("Najkrótsza ścieżka od " + path.get(path.size()-1) + " do " + path.get(0) + ":");
        for (int j = path.size() - 1; j > 0; j--) {
            w.print( path.get(j) +" -" + weight.get(j-1) + "- ");       //wypisujemy ścieżkę dzięki pomocniczym listom
        }
        w.println( path.get(0));
        w.println( "Długość ścieżki równa " + cost[node]);
    }
}
