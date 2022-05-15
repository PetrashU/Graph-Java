package graph;

import java.io.PrintWriter;
import java.io.Reader;

public class Graph {
    public int row;
    public int col;
    public double weights[];

    public Graph(){
    }
    public Graph(int row, int col){
        this.row = row;
        this.col = col;
        int iter = col * row;
        weights = new double[iter*iter];
    }
    public double getMaxWeight(){
        if (weights.length == 0)
            return 0;
        double max = weights[0];
        for(int i = 0; i < weights.length; i++){
            if (weights[i] > max)
                max = weights[i];
        }
        return max;
    }
    public double getMinWeight() {
        if (weights.length == 0)
            return 0;
        double min;
        int i = 0;
        do {
            min = weights[i];
            i++;
        } while (weights[i] == 0);
        for (i = 0; i < weights.length; i++) {
            if (weights[i] < min && weights[i] != 0)
                min = weights[i];
        }
        return min;
    }
    public void generateGraph(boolean connect){

    };
    public void readGraph(Reader r){

    };
    public void SaveGraph(PrintWriter w){

    };
   /* public boolean bfs(){
    };
    public Path dijkstra(int st){

    };
*/
}
