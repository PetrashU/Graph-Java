package graph;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import Kratka.ColorScale;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Graph {
    public int row;
    public int col;

    public int iter = row*col;
    public double[] weights;

    public double minWeight;
    public double maxWeight;
    public String ErrorMassage;
    public ArrayList<String> WarningMassage;
    public double[][] nodeCoordinates;


    public Graph(){
    }
    public Graph(int row, int col){
        this.row = row;
        this.col = col;
        iter = col * row;
        weights = new double[iter*iter];
        nodeCoordinates = new double[iter][2];
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


    public void drawGraph(GraphicsContext gc){
        gc.setFill(Color.ANTIQUEWHITE);
        gc.setLineWidth(2);
        int nodeSize = 20;
        int edgeSize = 4*nodeSize;
        while ((col+1)*edgeSize > 850 || (row+1)*edgeSize > 600){
            if (nodeSize == 1){
                break;
            }
            nodeSize--;
            edgeSize = 4*nodeSize;
        }
        int xnode = (850 - (col-1)*edgeSize)/2 - nodeSize/2;
        int ynode = (600 - (row-1)*edgeSize)/2 - nodeSize/2;
        //wstawiam rząd po rzędzie czyli numeruję od lewej do prawej i schodzę w dół
        for (int i=0; i<row; i++) {
            for (int j = 0; j < col; j++) {
                nodeCoordinates[i*col + j][0] = xnode + j * edgeSize;
                nodeCoordinates[i*col + j][1] = ynode + i * edgeSize;
                gc.fillOval(nodeCoordinates[i*col + j][0], nodeCoordinates[i*col + j][1], nodeSize, nodeSize);
            }
        }
        ColorScale colorscale = new ColorScale(minWeight, maxWeight);
        for (int i=0; i<iter; i++){
            for (int j=i; j<iter; j++){
                gc.setStroke(colorscale.ColorOfValue(weights[i*iter + j]));
                if (weights[i*iter + j] > 0 ) {
                    gc.strokeLine(nodeCoordinates[i][0]+nodeSize/2, nodeCoordinates[i][1]+nodeSize/2, nodeCoordinates[j][0]+nodeSize/2, nodeCoordinates[j][1]+nodeSize/2);
                }
            }
        }

    }
    public void generateGraph(boolean connect) {
        Random random = new Random();
        boolean condition1, condition2, condition3, condition4;
        for (int i = 0; i < iter * iter; i++) {
            weights[i] = 0.0;
        }
        int blank = -1;
        if (!connect) {
            blank = random.nextInt() % iter;
        }
        for (int i = 0; i < iter; i++) {
            for (int j = i; j < iter; j++) {
                condition1 = (j == i + 1 && ((i + 1) % col != 0));
                condition2 = (j == i - 1 && ((i % col) != 0));
                condition3 = (j == i - col && i >= col);
                condition4 = (j == i + col && i < iter - col);

                if (condition1 || condition2 || condition3 || condition4) {
                    if (connect || random.nextDouble(1) > 0.5) {
                        weights[i * iter + j] = minWeight + random.nextDouble(1) * (maxWeight - minWeight);
                        weights[j * iter + i] = weights[i * iter + j];
                    } else {
                        weights[i * iter + j] = 0.0;
                        weights[j * iter + i] = 0.0;
                    }
                    if (i == blank || j == blank) {
                        weights[i * iter + j] = 0.0;
                        weights[j * iter + i] = 0.0;
                    }
                }
            }
        }
    }
    public void readGraph(Reader r){

    }
    public void saveGraph(PrintWriter w){
        w.println(row + " " + col);
        boolean flag = true;
        for (int i=0; i<iter; i++){
            flag = true;
            for (int j=0; j<iter; j++){
                if (weights[i * iter + j] > 0.0){
                    if (flag){
                        w.print("\t");
                        flag = false;
                    }
                    w.print("  " + j + " :" + weights[i * iter + j]);
                }
            }
            w.print("\n");
        }
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
        path.cost = new double[iter];
        path.last = new int[iter];
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
