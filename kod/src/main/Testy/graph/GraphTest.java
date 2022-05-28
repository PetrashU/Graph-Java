package graph;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    public void ConnectedGraphTrue1(){


        HashMap<Integer, ArrayList<Edge>> edges = new HashMap<>();
        ArrayList<Edge> tmp = new ArrayList<>();

        Edge edge1 = new Edge(1,1);
        Edge edge2 = new Edge(3,4);
        tmp.add(edge1);
        tmp.add(edge2);
        edges.put(0, (ArrayList<Edge>) tmp.clone());
        tmp.clear();

        edge1 = new Edge(0,1);
        edge2 = new Edge(2,3);
        tmp.add(edge1);
        tmp.add(edge2);
        edges.put(1,(ArrayList<Edge>) tmp.clone());
        tmp.clear();

        edge1 = new Edge(1,3);
        edge2 = new Edge(5,7);
        tmp.add(edge1);
        tmp.add(edge2);
        edges.put(2,(ArrayList<Edge>) tmp.clone());
        tmp.clear();

        edge1 = new Edge(0,4);
        tmp.add(edge1);
        edges.put(3,(ArrayList<Edge>) tmp.clone());
        tmp.clear();

        edge1 = new Edge(5,2);
        tmp.add(edge1);
        edges.put(4,(ArrayList<Edge>) tmp.clone());
        tmp.clear();

        edge1 = new Edge(4,2);
        edge2 = new Edge(2,7);
        tmp.add(edge1);
        tmp.add(edge2);
        edges.put(5,(ArrayList<Edge>) tmp.clone());
        tmp.clear();

        Graph graph = new Graph(2, 3, edges);

        assertTrue(graph.bfs());
    }

    @Test
    public void MaxWeight7(){

        HashMap<Integer, ArrayList<Edge>> edges = new HashMap<>();
        ArrayList<Edge> tmp = new ArrayList<>();

        Edge edge1 = new Edge(1,1);
        Edge edge2 = new Edge(3,4);
        tmp.add(edge1);
        tmp.add(edge2);
        edges.put(0, (ArrayList<Edge>) tmp.clone());
        tmp.clear();

        edge1 = new Edge(0,1);
        edge2 = new Edge(2,3);
        tmp.add(edge1);
        tmp.add(edge2);
        edges.put(1,(ArrayList<Edge>) tmp.clone());
        tmp.clear();

        edge1 = new Edge(1,3);
        edge2 = new Edge(5,7);
        tmp.add(edge1);
        tmp.add(edge2);
        edges.put(2,(ArrayList<Edge>) tmp.clone());
        tmp.clear();

        edge1 = new Edge(0,4);
        tmp.add(edge1);
        edges.put(3,(ArrayList<Edge>) tmp.clone());
        tmp.clear();

        edge1 = new Edge(5,2);
        tmp.add(edge1);
        edges.put(4,(ArrayList<Edge>) tmp.clone());
        tmp.clear();

        edge1 = new Edge(4,2);
        edge2 = new Edge(2,7);
        tmp.add(edge1);
        tmp.add(edge2);
        edges.put(5,(ArrayList<Edge>) tmp.clone());
        tmp.clear();

        Graph graph = new Graph(2, 3, edges);

        assertEquals(7, graph.getMaxWeight());
    }

    @Test
    void CheckGenerationOfConnectedGraph() {
        Graph graph = new Graph(3, 2);
        graph.generateGraph(true);
        assertTrue(graph.bfs());
    }
    @Test
    void CheckGenerationOfNotConnectedGraph() {
        Graph graph = new Graph(3, 2);
        graph.generateGraph(false);
        assertFalse(graph.bfs());
    }

    @Test
    void CheckReadingAndSaving(){
        Graph graph = new Graph(3,2);
        graph.generateGraph(true);
        File write;
        write = new File("filename.txt");
        try {
            PrintWriter w = new PrintWriter (write);
            graph.saveGraph(w);
            w.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Graph graph1 = new Graph();
        Reader read;
        try {
            read = new FileReader(write);
            BufferedReader r = new BufferedReader(read);
            graph1.readGraph(r);
            r.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(graph.row, graph1.row);
        assertEquals(graph.col, graph1.col);
        for (int i = 0; i < graph.edges.size(); i++)
            for (int j = 0; j < graph.edges.get(i).size(); j++) {
                assertEquals(graph.edges.get(i).get(j).fin, graph1.edges.get(i).get(j).fin);
                assertEquals(graph.edges.get(i).get(j).weight, graph1.edges.get(i).get(j).weight);

            }

    }

    @Test
    void checkDijkstra() {
        HashMap<Integer, ArrayList<Edge>> edges = new HashMap<>();
        ArrayList<Edge> tmp = new ArrayList<>();

        Edge edge1 = new Edge(1,1);
        Edge edge2 = new Edge(3,4);
        tmp.add(edge1);
        tmp.add(edge2);
        edges.put(0, (ArrayList<Edge>) tmp.clone());
        tmp.clear();

        edge1 = new Edge(0,1);
        edge2 = new Edge(2,3);
        tmp.add(edge1);
        tmp.add(edge2);
        edges.put(1,(ArrayList<Edge>) tmp.clone());
        tmp.clear();

        edge1 = new Edge(1,3);
        edge2 = new Edge(5,7);
        tmp.add(edge1);
        tmp.add(edge2);
        edges.put(2,(ArrayList<Edge>) tmp.clone());
        tmp.clear();

        edge1 = new Edge(0,4);
        tmp.add(edge1);
        edges.put(3,(ArrayList<Edge>) tmp.clone());
        tmp.clear();

        edge1 = new Edge(5,2);
        tmp.add(edge1);
        edges.put(4,(ArrayList<Edge>) tmp.clone());
        tmp.clear();

        edge1 = new Edge(4,2);
        edge2 = new Edge(2,7);
        tmp.add(edge1);
        tmp.add(edge2);
        edges.put(5,(ArrayList<Edge>) tmp.clone());
        tmp.clear();

        Graph graph = new Graph(2, 3, edges);

        Path path = graph.dijkstra(0);
        assertArrayEquals(path.cost, new double[]{0.0, 1.0, 4.0, 4.0, 13.0, 11.0});
        assertArrayEquals(path.last, new int [] {-1,0,1,0,5,2});
    }
}
