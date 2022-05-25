package graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import Kratka.ColorScale;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Graph {
    public int row;
    public int col;
    // public double[] weights;
    public HashMap<Integer, ArrayList<Edge>> edges = new HashMap<>();   //graf w postaci listy: Integer - numer wierzchołka; ArrayList<Edge> - lista wszystkich krawędzi od niego
                            //edges.get(i)... - siągamy po listę krawędzi od wierzchołka i
                            //edges.get(i).size() - liczba tych krawędzi
                            //edges.get(i).get(j).fin/weight - siągamy po krawędz j, prowadzoną od wierzchołka nr.i  i bierzemy wierzchołek końcowy fin lub wagę weight
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
        int iter = row * col;
        for (int i = 0; i < iter; i++)
            edges.put(i,new ArrayList<Edge>());
        nodeCoordinates = new double[iter][2];
    }
    public Graph(int row, int col, HashMap<Integer, ArrayList<Edge>> edges){
        this.row = row;
        this.col = col;
        this.edges = edges;
    }
    public double getMaxWeight(){
        if (edges.size() == 0)
            return 0;
        double max = 0;
        for(int i = 0; i < (row*col); i++)
            for (Edge edge : edges.get(i)){
                if (edge.weight > max)
                    max = edge.weight;
            }
        return max;
    }
    public double getMinWeight() {
        if (edges.size() == 0)
            return 0;
        double min = Double.POSITIVE_INFINITY;
        for(int i = 0; i < (row*col); i++)
            for (Edge edge : edges.get(i)){
                if (edge.weight < min)
                    min = edge.weight;
            }
        return min;
    }

    public int getNodesSize(int width, int height){
        int nodeSize = 20;
        int edgeSize = 4* nodeSize;
        int iter = row * col;
        //jeżeli graf się nie mieści, to go skaluję aż do skutku (do min. nodeSize == 1)
        //zachowując zależność że edgeSize jest 4 razy dłuższy niż rozmiar node'a
        while ((col+1)*edgeSize > width || (row+1)*edgeSize > height){
            if (nodeSize == 1){
                break;
            }
            nodeSize--;
            edgeSize = 4* nodeSize;
        }
        return nodeSize;
    }

    public void drawGraph(GraphicsContext gc, int width, int height, ColorScale scale){
        gc.setFill(Color.ANTIQUEWHITE);
        gc.setLineWidth(2);
        int nodeSize = 20;
        int edgeSize = 4*nodeSize;
        int iter = row * col;
        //jeżeli graf się nie mieści, to go skaluję aż do skutku (do min. nodeSize == 1)
        //zachowując zależność że edgeSize jest 4 razy dłuższy niż rozmiar node'a
        while ((col+1)*edgeSize > width || (row+1)*edgeSize > height){
            if (nodeSize == 1){
                break;
            }
            nodeSize--;
            edgeSize = 4*nodeSize;
        }
        int xnode = (width - (col-1)*edgeSize)/2 - nodeSize/2;
        int ynode = (height - (row-1)*edgeSize)/2 - nodeSize/2;
        //wstawiam rząd po rzędzie czyli numeruję od lewej do prawej i schodzę w dół
        for (int i=0; i<row; i++) {
            for (int j = 0; j < col; j++) {
                nodeCoordinates[i*col + j][0] = xnode + j * edgeSize;
                nodeCoordinates[i*col + j][1] = ynode + i * edgeSize;
                gc.fillOval(nodeCoordinates[i*col + j][0], nodeCoordinates[i*col + j][1], nodeSize, nodeSize);
            }
        }
        for (int i=0; i<iter; i++){
            for (int j=i; j<iter; j++){
                for (Edge edge : edges.get(i)){
                    if (edge.fin == j){
                        gc.setStroke(scale.ColorOfValue(edge.weight));
                        gc.strokeLine(nodeCoordinates[i][0]+nodeSize/2, nodeCoordinates[i][1]+nodeSize/2, nodeCoordinates[j][0]+nodeSize/2, nodeCoordinates[j][1]+nodeSize/2);
                    }
                }
            }
        }

    }
    public void generateGraph(boolean connect) {
        Random random = new Random();
        int iter = row * col;

        boolean condition1, condition2, condition3, condition4;

        int blank = -1;
        if (!connect) {
            blank = random.nextInt() % iter;
        }
        ArrayList<Edge> list = new ArrayList<>();
        for (int i = 0; i < iter; i++) {
            for (int j = i; j < iter; j++) {
                condition1 = (j == i + 1 && ((i + 1) % col != 0));
                condition2 = (j == i - 1 && ((i % col) != 0));
                condition3 = (j == i - col && i >= col);
                condition4 = (j == i + col && i < iter - col);
                if (condition1 || condition2 || condition3 || condition4) {
                    if (connect || random.nextDouble(1) > 0.5) {
                        Edge tmp = new Edge();
                        Edge tmp1 = new Edge();
                        tmp.fin = j;
                        tmp.weight = minWeight + random.nextDouble(1) * (maxWeight - minWeight);
                        edges.get(i).add(tmp);
                        tmp1.fin = i;
                        tmp1.weight = tmp.weight;
                        edges.get(j).add(tmp1);
                    }
                }
            }
        }
    }
    public void readGraph(BufferedReader r) throws IOException {    //trzeba dodać obsługę błędów
        try {
            int index;
            int lineNumber = 0;
            String line = r.readLine();
            Scanner scanner = new Scanner(line);
            row = scanner.nextInt();
            col = scanner.nextInt();
            scanner.close();
            int iter = col * row;
            for (int i = 0; i < iter; i++)
                edges.put(i,new ArrayList<Edge>());
            nodeCoordinates = new double[iter][2];
            while ((line = r.readLine()) != null) {
                //mam linię w postaci stringu - mogę teraz na niej operować
                line = line.trim();
                String[] data = line.split("[\\s:]+");
                //data to tablica danych w postaci stringów z jednej linii
                for (int i=0; i< data.length; i+=2){
                    index = Integer.parseInt(data[i]);
                    Edge tmp = new Edge();
                    tmp.fin = index;
                    tmp.weight = Double.parseDouble(data[i+1]);
                    edges.get(lineNumber).add(tmp);
                }
                lineNumber++;
            }
        }
        catch(IOException ioe){
            ErrorMassage = ioe.getMessage();
        }
    }

    public void saveGraph(PrintWriter w){
        w.println(row + " " + col);
        int iter = row * col;
        boolean flag = true;
        for (int i=0; i<iter; i++){
            flag = true;
            for (Edge edge : edges.get(i)){
                if (flag){
                    w.print("\t");
                    flag = false;
                }
                w.print("  " + edge.fin + " :" + edge.weight);
            }
            w.print("\n");
        }
    }
    public boolean bfs()
    {
        boolean[] flag = new boolean[col*row];
        LinkedList<Integer> queue = new LinkedList<Integer>();

        flag[0]=true;
        queue.add(0);

        int s;
        while (queue.size() != 0)
        {
            s = queue.poll();

            for (Edge edge : edges.get(s)) {
                int n = edge.fin;
                if (!flag[n]) {
                    flag[n] = true;
                    queue.add(n);
                }
            }
        }
        for (int i = 0; i < (row*col); i++)
            if(!flag[i])
                return false;
        return true;
    }
    public Path dijkstra(int st){
        int[] q = new int[row*col];		//tabela, pokazująca, czy odpowiedni węzeł był odwiedzony
        int min_i;
        double tmp, min;
        int iter = row * col;
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
                for(int i = 0; i < iter; i++){		//przypisujemy dojścia do sąsiadów
                    for (Edge edge : edges.get(min_i)) {
                        if ((edge.fin == i))
                        {
                            int k = edge.fin;
                            tmp = min + edge.weight;
                            if (tmp < path.cost[k]){
                                path.cost[k] = tmp;
                                path.last[k] = min_i;
                            }
                        }
                    }
                }
                q[min_i] = 1;
            }
        } while (min_i != -1);
        return path;
    }

}
