package Kratka;

import graph.Edge;
import graph.Graph;
import graph.Path;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Kratka extends Application {

    public int row = 10;
    public int col = 10;
    public double minweight = 0.0;
    public double maxweight = 10.0;
    public GraphicsContext gc;
    public Graph graph;
    public Path path;
    public ColorScale edgeScale;
    public ColorScale nodeScale;
    public ArrayList<Integer> nodelist = new ArrayList<>();     //lista wierzchołków końcowych


    @Override
    public void start(Stage primarystage) throws Exception {

        Label size = new Label("Size:");
        TextField textrow = new TextField();
        textrow.setMaxWidth(50);
        textrow.setText(String.valueOf(row));
        Text sizex = new Text("x");
        TextField textcol = new TextField();
        textcol.setMaxWidth(50);
        textcol.setText(String.valueOf(col));
        HBox sizebox = new HBox(5,textrow,sizex,textcol);

        Label weight = new Label("Edge weight range:");
        TextField textweightlow = new TextField();
        textweightlow.setMaxWidth(50);
        textweightlow.setText(String.valueOf(minweight));
        Text weightsymbol = new Text("-");
        TextField textweighthigh = new TextField();
        textweighthigh.setMaxWidth(50);
        textweighthigh.setText(String.valueOf(maxweight));
        HBox weightbox = new HBox(textweightlow,weightsymbol,textweighthigh);

        Label con = new Label("Connectivity");
        RadioButton connected = new RadioButton("Connected");
        connected.setSelected(true);
        RadioButton notconnected = new RadioButton("Not connected");
        RadioButton randcon = new RadioButton("Random");
        ToggleGroup connectivity = new ToggleGroup();
        connected.setToggleGroup(connectivity);
        notconnected.setToggleGroup(connectivity);
        randcon.setToggleGroup(connectivity);
        VBox connectbox = new VBox(connected,notconnected,randcon);

        HBox firstline = new HBox(40, size,sizebox,weight,weightbox,con,connectbox);

        FlowPane root = new FlowPane();
        root.setVgap(5);
        root.getChildren().add(firstline);
        Label edgemin = new Label(String.valueOf(minweight));
        Label edgemax = new Label(String.valueOf(maxweight));
        Label nodemin = new Label();
        Label nodemax = new Label();


        Button generate = new Button("Generate");
        generate.setOnAction(actionEvent -> {
            try {
                String trow = textrow.getText();
                String tcol = textcol.getText();
                row = Integer.parseInt(trow);
                if (row <= 0 ){
                    textrow.setStyle("-fx-text-box-border: red;");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Liczba wierszy nie może być równa lub mniejsza 0");
                    alert.setContentText("Proszę zmienić podaną liczbę wierszy");
                    alert.showAndWait();
                    textrow.setStyle("");
                    return;
                }
                col = Integer.parseInt(tcol);
                if (col <= 0 ){
                    textcol.setStyle("-fx-text-box-border: red;");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Liczba kolumn nie może być równa lub mniejsza 0");
                    alert.setContentText("Proszę zmienić podaną liczbę kolumn");
                    alert.showAndWait();
                    textcol.setStyle("");
                    return;
                }

                String tweightlow = textweightlow.getText();
                String tweighthigh = textweighthigh.getText();
                minweight = Double.parseDouble(tweightlow);
                if (minweight < 0 ){
                    textweightlow.setStyle("-fx-text-box-border: red;");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Dolna granica zakresu losowanych wag nie może być mniejsza od 0");
                    alert.setContentText("Proszę zmienić dolną granicę zakresu wag");
                    alert.showAndWait();
                    textweightlow.setStyle("");
                    return;
                }
                maxweight = Double.parseDouble(tweighthigh);
                if (maxweight <= 0 ){
                    textweighthigh.setStyle("-fx-text-box-border: red;");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Górna granica zakresu losowanych wag nie może być mniejsza lub równa 0");
                    alert.setContentText("Proszę zmienić górną granicę zakresu wag");
                    alert.showAndWait();
                    textweighthigh.setStyle("");
                    return;
                }
                graph = null;
                path = null;
                nodelist.clear();
                gc.setFill(Color.BLACK);
                gc.fillRect(0,0,850,600);
                graph = new Graph(row, col);

                nodemax.setText("");
                nodemin.setText("");

                graph.minWeight = minweight;
                edgemin.setText(String.valueOf(minweight));
                edgeScale.min = minweight;
                graph.maxWeight = maxweight;
                edgemax.setText(String.valueOf(maxweight));
                edgeScale.max = maxweight;
                RadioButton rb = (RadioButton) connectivity.getSelectedToggle();
                String connect = rb.getText();
                boolean connection;
                if (Objects.equals(connect, "Connected"))
                    connection = true;
                else if (Objects.equals(connect, "Not connected"))
                    connection = false;
                else {
                    Random random = new Random();
                    connection = random.nextBoolean();
                }
                // Test: System.out.println(row + " "+ col +" "+ minweight+ " "+ maxweight+ " "+ connection);
                graph.generateGraph(connection);

                /*wypisywanie wag grafu do testowania
                for (int i=0; i<graph.weights.length; i++){
                    System.out.println(graph.weights[i]);
                }
                 */


                graph.drawGraph(gc, 850, 600, edgeScale);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(e.getMessage());
                alert.showAndWait();
            }
        });

        Button read = new Button("Read");
        read.setOnAction(e ->{
            FileChooser filechooser = new FileChooser();
            filechooser.getExtensionFilters().addAll(new ExtensionFilter("txt file", "*.txt"));
            File readfile = filechooser.showOpenDialog(primarystage);
            try {
                if (readfile != null) {
                    BufferedReader r = new BufferedReader(new FileReader(readfile));
                    graph = null;
                    graph = new Graph();
                    path = null;
                    gc.setFill(Color.BLACK);
                    gc.fillRect(0, 0, 850, 600);
                    nodelist.clear();
                    graph.readGraph(r);
                    r.close();
                    if (graph.ErrorMassage != null) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(graph.ErrorMassage);
                        alert.showAndWait();
                        return;
                    }
                    if (graph.WarningMassage != null) {
                        for (int i = 0; i < graph.WarningMassage.size(); i++) {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("WARNING");
                            alert.setHeaderText(graph.WarningMassage.get(i));
                            alert.showAndWait();
                        }
                    }
                    row = graph.row;
                    textrow.setText(String.valueOf(row));
                    col = graph.col;
                    textcol.setText(String.valueOf(col));
                    graph.minWeight = graph.getMinWeight();
                    minweight = graph.getMinWeight();
                    edgemin.setText(String.valueOf(minweight));
                    textweightlow.setText(String.valueOf(minweight));
                    edgeScale.min = minweight;
                    graph.maxWeight = graph.getMaxWeight();
                    maxweight = graph.getMaxWeight();
                    edgemax.setText(String.valueOf(maxweight));
                    textweighthigh.setText(String.valueOf(maxweight));
                    edgeScale.max = maxweight;

                    nodemax.setText("");
                    nodemin.setText("");

                    if (graph.bfs())
                        connected.setSelected(true);
                    else
                        notconnected.setSelected(true);
                    graph.drawGraph(gc,850,600, edgeScale);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });
        root.getChildren().add(read);

        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                MouseButton mb = me.getButton();
                int startNode = 0;
                int finishNode = graph.row * graph.col - 1;
                if (mb == MouseButton.PRIMARY) {
                    clearPaths();
                    if (!graph.bfs()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Graf nie jest spójny! Nie można użyć algorytmu Dijkstry");
                        alert.showAndWait();
                        return;
                    }
                    double xl = me.getX();
                    double yl = me.getY();
                    //System.out.println("wspolrzedne lewy guzik x, y: " + xl + ", " +yl);
                    double sum = 1000;
                    for (int i = 0; i < (graph.col * graph.row); i++) {
                        if (sum > Math.abs(graph.nodeCoordinates[i][0] - xl) + Math.abs(graph.nodeCoordinates[i][1] - yl)) {
                            sum = Math.abs(graph.nodeCoordinates[i][0] - xl) + Math.abs(graph.nodeCoordinates[i][1] - yl);
                            startNode = i;
                        }
                    }
                    path = new Path();
                    path = graph.dijkstra(startNode);

                    double nodecostmin = path.getMinCost();
                    double nodecostmax = path.getMaxCost();

                    nodemin.setText(String.valueOf(nodecostmin));
                    nodemax.setText(String.valueOf(nodecostmax));

                    nodeScale = new ColorScale(nodecostmin, nodecostmax);

                    addColorNodes(850, 600, nodeScale);

                    //System.out.println("startowy node: " + st);
                }
                else if (mb == MouseButton.SECONDARY) {
                    if (path == null){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Nie został uruchomiomy algorytm Dijkstry! Proszę spoczątku kliknąć na początkowy wierzchołek PRAWYM klawiszem!");
                        alert.showAndWait();
                        return;
                    }
                    double xr = me.getX();
                    double yr = me.getY();
                    //System.out.println("wspolrzedne prawy guzik x, y: " + xr + ", " +yr);
                    double sum = 1000;
                    for (int i=0; i<(graph.col * graph.row); i++) {
                        if (sum > Math.abs(graph.nodeCoordinates[i][0] - xr) + Math.abs(graph.nodeCoordinates[i][1] - yr)){
                            sum = Math.abs(graph.nodeCoordinates[i][0] - xr) + Math.abs(graph.nodeCoordinates[i][1] - yr);
                            finishNode = i;
                        }
                    }
                    nodelist.add(finishNode);
                    drawPath(finishNode);

                    //System.out.println("koncowy node: " + node);
                }

            }
        };


        Button savegraph = new Button("Save graph");
        savegraph.setOnAction(e ->{
            FileChooser filechooser = new FileChooser();
            filechooser.getExtensionFilters().addAll(new ExtensionFilter("txt file", "*.txt"));
            File savefile = filechooser.showSaveDialog(primarystage);
            try {
                if (savefile != null) {
                    PrintWriter w = new PrintWriter(savefile);

                    // for (int i=0; i<graph.weights.length; i++){
                    //      System.out.println(graph.weights[i]);
                    //  }

                    graph.saveGraph(w);
                    w.close();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });


        Button savepath = new Button("Save paths");

        savepath.setOnAction(e ->{
            if (nodelist.size() == 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Nie ma znalezionych scieżek do zapisania!");
                alert.showAndWait();
                return;
            }
            FileChooser filechooser = new FileChooser();
            filechooser.getExtensionFilters().addAll(new ExtensionFilter("txt file", "*.txt"));
            File savefile = filechooser.showSaveDialog(primarystage);
            try {
                if (savefile != null) {
                    PrintWriter w = new PrintWriter(savefile);
                    for (Integer integer : nodelist) {
                        path.savePath(w, graph, integer);
                    }
                    w.close();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        Button clean = new Button("Clean");
        clean.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                clearPaths();
            }
        });
        Button delete = new Button("Delete");

        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                graph = null;
                path = null;
                nodelist.clear();
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, 850, 600);
                edgemax.setText("");
                edgemin.setText("");
                nodemin.setText("");
                nodemax.setText("");

            }
        });

        HBox secondline = new HBox(70, generate, read, savegraph, savepath, clean, delete);
        root.getChildren().add(secondline);

        Canvas canvas = new Canvas(850, 600);
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 850, 600);
        canvas.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);
        root.getChildren().add(canvas);


        Label edgecolor = new Label("Edge color scale");
        HBox colorfirst = new HBox(325, edgemin, edgecolor, edgemax);
        root.getChildren().add(colorfirst);

        edgeScale = new ColorScale(minweight, maxweight);
        ImageView scaleimg = new ImageView(edgeScale.DrawColorScale(850, 20));

        root.getChildren().add(scaleimg);

       /*double nodecostmin = 0.0;
        double nodecostmax = 10.0;

        nodemin.setText(String.valueOf(nodecostmin));
        nodemax.setText(String.valueOf(nodecostmax));*/
        Label nodecolor = new Label("Node color scale");
        HBox colorsecond = new HBox(325, nodemin, nodecolor, nodemax);
        root.getChildren().add(colorsecond);

        Button changescale = new Button("Modify color scale");
        changescale.setOnAction(e -> {
            ColorPicker colorPicker1 = new ColorPicker(); //color max weight edge
            ColorPicker colorPicker2 = new ColorPicker(); //color min weight edge
            ColorPicker colorPicker3 = new ColorPicker(); //color node
            ColorPicker colorPicker4 = new ColorPicker(); //color path
            Label cpLabel1 = new Label("Max weight edge color");
            Label cpLabel2 = new Label("Min weight edge color");
            Label cpLabel3 = new Label("Node color");
            Label cpLabel4 = new Label("Path color");
            Label cpTitle = new Label("Choose colors: ");
            Label[] cpLabels;
            cpLabels = new Label[]{cpLabel1, cpLabel2, cpLabel3, cpLabel4};
            FlowPane chooseColors = new FlowPane();
            chooseColors.setPadding(new Insets(10, 20, 10, 30));
            chooseColors.setVgap(4);
            ColorPicker[] colorPickers;
            colorPickers = new ColorPicker[]{colorPicker1, colorPicker2, colorPicker3, colorPicker4};
            chooseColors.getChildren().add(cpTitle);
            for (int i=0; i<colorPickers.length; i++) {
                chooseColors.getChildren().add(cpLabels[i]);
                chooseColors.getChildren().add(colorPickers[i]);
            }
            Stage stageColorScale = new Stage();
            stageColorScale.setWidth(200);
            stageColorScale.setHeight(300);
            Scene sceneColorScale = new Scene(chooseColors);
            stageColorScale.setScene(sceneColorScale);
            stageColorScale.setResizable(false);
            stageColorScale.show();
        });

        root.getChildren().add(changescale);

        Scene scene = new Scene(root, 850, 800);

        primarystage.setScene(scene);
        primarystage.setTitle("Kratka");
        primarystage.show();
    }

    public void addColorNodes(int width, int height, ColorScale scale) {
        int iter = graph.row * graph.col;
        double[][] nodeCoordinates = new double[iter][2];
        gc.setLineWidth(2);
        int nodeSize = 20;
        int edgeSize = 4 * nodeSize;
        while ((graph.col + 1) * edgeSize > width || (graph.row + 1) * edgeSize > height) {
            if (nodeSize == 1) {
                break;
            }
            nodeSize--;
            edgeSize = 4 * nodeSize;
        }
        int xnode = (width - (graph.col - 1) * edgeSize) / 2 - nodeSize / 2;
        int ynode = (height - (graph.row - 1) * edgeSize) / 2 - nodeSize / 2;
        //wstawiam rząd po rzędzie czyli numeruję od lewej do prawej i schodzę w dół
        for (int i = 0; i < graph.row; i++) {
            for (int j = 0; j < graph.col; j++) {
                nodeCoordinates[i * graph.col + j][0] = xnode + j * edgeSize;
                nodeCoordinates[i * graph.col + j][1] = ynode + i * edgeSize;
                gc.setFill(scale.ColorOfValue(path.cost[i * graph.col + j]));
                gc.fillOval(nodeCoordinates[i * graph.col + j][0], nodeCoordinates[i * graph.col + j][1], nodeSize, nodeSize);
            }
        }
    }
    public void drawPath(int finishNode){
        int nodeSize = graph.getNodesSize(850,600);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        int sEdge = finishNode;
        int fEdge = path.last[sEdge];
        while (fEdge != -1){
            gc.strokeLine(graph.nodeCoordinates[sEdge][0] + nodeSize/2, graph.nodeCoordinates[sEdge][1] + nodeSize/2,
                    graph.nodeCoordinates[fEdge][0] + nodeSize/2, graph.nodeCoordinates[fEdge][1] + nodeSize/2);
            sEdge = fEdge;
            fEdge = path.last[fEdge];
        }
    }
    public void clearPaths(){
        int nodeSize = graph.getNodesSize(850,600);
        for (int sEdge : nodelist) {
            int fEdge = path.last[sEdge];
            while (fEdge != -1) {
                for (Edge edge : graph.edges.get(sEdge)) {
                    if (edge.fin == fEdge)
                        gc.setStroke(edgeScale.ColorOfValue(edge.weight));
                }
                gc.strokeLine(graph.nodeCoordinates[sEdge][0] + nodeSize / 2, graph.nodeCoordinates[sEdge][1] + nodeSize / 2,
                        graph.nodeCoordinates[fEdge][0] + nodeSize / 2, graph.nodeCoordinates[fEdge][1] + nodeSize / 2);
                sEdge = fEdge;
                fEdge = path.last[fEdge];
            }
        }
        nodelist.clear();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
