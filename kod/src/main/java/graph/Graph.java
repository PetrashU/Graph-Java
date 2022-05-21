package graph;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;

public class Graph {
    public int row;
    public int col;
    public double weights[];

   // public double minWeight;
    //public double maxWeight;
    public String ErrorMassage;
    public ArrayList<String> WarningMassage;


    public Graph(){
    }
    public Graph(int row, int col){
        this.row = row;
        this.col = col;
        int iter = col * row;
        weights = new double[iter*iter];
    }
    public Graph(int row, int col, double[] weights){
        this.row = row;
        this.col = col;
        this.weights = weights;
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

    }
    public void readGraph(Reader r){

    }
    public void saveGraph(PrintWriter w){

    }
    public boolean bfs(){
        ArrayList<Integer> queue = new ArrayList<>();		//kolejka pryorytetowa
        boolean[] flag= new boolean[col * row];		//flaga odwiedzenia wierzchołka
        int k = 0;
        int n = 1;
        queue.add(0);
        Arrays.fill(flag, false);
        for (int i = 0; i < n; i++){		//przechodzimy po wszystkich wierzchołkach z kolejki
            flag[queue.get(k)]= true;			//odznaczamy obecny wierzchołek jako odwiedzony
            for (int j = 0; j < (col * row); j++)
                if (this.weights[queue.get(k) * col * row + j] != 0 && !flag[j])	//szukamy krawędzi
                {
                    n++;		//zwiększamy liczbę wierzchołków w kolecje
                    queue.add(j);	//dodajemy do kolejki wierzchołek, do którego prowadzi krawędź
                }

            k++;	//idziemy do kolejnego numera w kolejce
        }
        //sprawdzamy odwiedzenie wszystkich wierzchołków
        for (int i=0; i < (col * row); i++)
            if (!flag[i])
                return false;
        return true;    //spójny
    }
    public Path dijkstra(int st){
        int[] q = new int[row*col];		//tabela, pokazująca, czy odpowiedni węzeł był odwiedzony
        int min_i;
        double tmp, min;
        Path path = new Path();
        Arrays.fill(q, 0);
        Arrays.fill(path.cost, Double.POSITIVE_INFINITY);
        Arrays.fill(path.last, -1);
        path.cost[st] = 0;
        do{				//wykonujemy pętlę aż indeks węzła o najmniejszym koszcie dojścia nie będzie pusty
            min = Double.POSITIVE_INFINITY;
            min_i = -1;
            for(int i = 0; i < (row * col); i++){	//szukamy węzeł o najmniejszym koszcie dojścia
                if ((q[i] == 0) && (path.cost[i] < min)){
                    min = path.cost[i];
                    min_i = i;
                }
            }
            if (min_i != -1)
            {
                for( int i = 0; i < (col*row); i++){		//przypisujemy dojścia do sąsiadów
                    if (this.weights[min_i * col * row + i] > 0)
                    {
                        tmp = min + this.weights[min_i * col * row + i];
                        if (tmp < path.cost[i]){
                            path.cost[i] = tmp;
                            path.last[i] = min_i;
                        }
                    }
                }
                q[min_i] = 1;
            }
        } while (min_i != -1);
        return path;
    }

}
