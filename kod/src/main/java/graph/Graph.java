package graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import Kratka.ColorScale;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

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
    public ArrayList<String> WarningMassage = new ArrayList<>();
    public double[][] nodeCoordinates;



    public Graph(){
    }
    public Graph(int row, int col){
        this.row = row;
        this.col = col;
        int iter = row * col;
        for (int i = 0; i < iter; i++)
            edges.put(i, new ArrayList<Edge>());
        nodeCoordinates = new double[iter][2];
    }
    public Graph(int row, int col, HashMap<Integer, ArrayList<Edge>> edges){
        this.row = row;
        this.col = col;
        this.edges.putAll(edges);
        int iter = row * col;
        nodeCoordinates = new double[iter][2];
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

    public double getNodesSize(int width, int height){
        double scalar = 3;
        double nodeSize = 30;
        double edgeSize = scalar * nodeSize;
        while ((col+1)*edgeSize > width || (row+1)*edgeSize > height){
            if (nodeSize <= 2 || scalar <= 0.5){
                break;
            }
            nodeSize -= 0.5;
            scalar -= 0.03;
            edgeSize = scalar * nodeSize;
        }
        return nodeSize;
    }

    public void drawGraph(GraphicsContext gc, int width, int height, ColorScale scale, Color nodeColor){
        gc.setFill(nodeColor);
        double nodeSize = getNodesSize(width, height);
        double num = (30 - nodeSize)*2;
        double scalar = 3 - 0.03*num;
        double edgeSize = scalar * nodeSize;
        int iter = row * col;
        gc.setLineWidth(nodeSize/4);
        double xnode = (width - (col-1)*edgeSize)/2 - nodeSize/2;
        double ynode = (height - (row-1)*edgeSize)/2 - nodeSize/2;
        //wstawiam rząd po rzędzie czyli numeruję od lewej do prawej i schodzę w dół
        for (int i=0; i<row; i++) {
            for (int j=0; j<col; j++) {
                nodeCoordinates[i*col + j][0] = xnode + j * edgeSize;
                nodeCoordinates[i*col + j][1] = ynode + i * edgeSize;
            }
        }
        for (int i=0; i<iter; i++){
            for (Edge edge : edges.get(i)){
                int j = edge.fin;
                gc.setStroke(scale.ColorOfValue(edge.weight));
                gc.strokeLine(nodeCoordinates[i][0]+nodeSize/2, nodeCoordinates[i][1]+nodeSize/2, nodeCoordinates[j][0]+nodeSize/2, nodeCoordinates[j][1]+nodeSize/2);
            }
        }
        for (int i=0; i<row; i++){
            for (int j=0; j<col; j++){
                gc.fillOval(nodeCoordinates[i*col + j][0], nodeCoordinates[i*col + j][1], nodeSize, nodeSize);
            }
        }

    }
    public void displayNodesIndices(GraphicsContext gc, int width, int height){
        gc.setLineWidth(1);
        double nodeSize = getNodesSize(width, height);
        gc.setStroke(Color.BLACK);
        gc.setFont(Font.font("Lucida Sans Unicode", nodeSize/2));
        double x; //zmienna używana do wyśrodkowania indeksów zależnie od wartości
        for (int i=0; i<row*col; i++) {
            if (i<10)
                x = nodeSize/6;
            else if (i<100)
                x = 0;
            else
                x = - (nodeSize/4);
            gc.strokeText(Integer.toString(i), nodeCoordinates[i][0] + nodeSize/4 + x, nodeCoordinates[i][1] + nodeSize*0.75);
        }
    }
    public void generateGraph(boolean connect) {
        Random random = new Random();
        int iter = row * col;

        boolean condition1, condition2, condition3, condition4;

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
    public void readGraph(BufferedReader r) {    //trzeba dodać obsługę błędów
        try {
            int index;
            int lineNumber = 0;
            boolean flag = false;
            String line = r.readLine();
            Scanner scanner = new Scanner(line);
            row = scanner.nextInt();
            col = scanner.nextInt();
            scanner.close();
            int iter = col * row;
            for (int i = 0; i < iter; i++)
                edges.put(i, new ArrayList<Edge>());
            nodeCoordinates = new double[iter][2];
            while ((line = r.readLine()) != null) {
                    //mam linię w postaci stringu - mogę teraz na niej operować
                    line = line.trim();
                    String[] data = line.split("[\\s:]+");
                    //data to tablica danych w postaci stringów z jednej linii
                    for (int i = 0; i < data.length; i += 2) {
                        index = Integer.parseInt(data[i]);
                        Edge tmp = new Edge();
                        tmp.fin = index;
                        tmp.weight = Double.parseDouble(data[i + 1]);
                        if (tmp.weight < 0) {
                            tmp.weight = Math.abs(tmp.weight);
                            System.out.println(tmp.weight);
                            flag = true;
                        }
                        edges.get(lineNumber).add(tmp);
                    }
                    lineNumber++;
                }
            if (flag){
                WarningMassage.add("Wystąpiła ujemna waga w grafie. Waga została zamieniona na jej wartość bezwzględną.");
            }
        }
        catch(IOException e){
            ErrorMassage = "Nieprawidłowy format grafu.";
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
        PriorityQueue<Integer> queue = new PriorityQueue<Integer>();

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
    public static class NodeAndCost{
        int node;
        double cost;
        public NodeAndCost(int n, double c){ node= n; cost = c;};
    }
    public static class CostComparator implements Comparator<NodeAndCost>{
        @Override
        public int compare(NodeAndCost o1, NodeAndCost o2) {
            return Double.compare(o1.cost, o2.cost);
        }
    }

    public static class NodesQueue extends java.util.ArrayList<NodeAndCost> {
        public void updatenodes(int n, double cost){
            for (NodeAndCost nodeAndCost : this){
                if (nodeAndCost.node == n){
                    nodeAndCost.cost = cost;
                    return;
                }
            }
            this.add(new NodeAndCost(n, cost));
        }
    }

    public Path dijkstra(int st){
        boolean[] visited = new boolean[row*col];		//tabela, pokazująca, czy odpowiedni węzeł był odwiedzony
        NodesQueue queue = new NodesQueue();
        double tmp;
        int iter = row * col;
        double []cost = new double[iter];
        int[] last = new int[iter];
        Arrays.fill(visited, false);
        Arrays.fill(cost, Double.POSITIVE_INFINITY);
        Arrays.fill(last, -1);
        cost[st] = 0;

        queue.add(new NodeAndCost(st, 0));
        while (queue.size() != 0){
            queue.sort(new CostComparator());
            NodeAndCost node = queue.get(0);
            queue.remove(0);
            int i = node.node;
            double l = node.cost;
            if (!visited[i]){     //przypisujemy dojścia do sąsiadów
                for (Edge edge : edges.get(i)) {
                    int k = edge.fin;
                    tmp = l + edge.weight;
                    if (tmp < cost[k]) {
                        cost[k] = tmp;
                        last[k] = i;
                        queue.updatenodes(k, tmp);
                    }
                }
            }
        }
        return new Path(cost, last);
    }

}
