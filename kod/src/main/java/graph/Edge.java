package graph;

public class Edge {
    public int fin;
    public double weight;
    public Edge(){}
    public Edge(int fin, double weight){        //krawędź
        this.fin = fin;                 //wierzchołek końcowy
        this.weight = weight;           //waga
    }


}

