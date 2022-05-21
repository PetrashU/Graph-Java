package Kratka;

import graph.Graph;
import graph.Path;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import javax.swing.*;
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

    public Label edgemin;
    public Label edgemax;
    public Label nodemin;
    public Label nodemax;
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

        Button generate = new Button("Generate");
        generate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
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
                    if (graph == null)
                        graph = new Graph(row, col);
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
                    graph.minWeight = minweight;
                    edgemin.setText(String.valueOf(minweight));
                    graph.maxWeight = maxweight;
                    edgemax.setText(String.valueOf(maxweight));
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
                    /* wypisywanie wag grafu do testowania
                    for (int i=0; i<graph.weights.length; i++){
                        System.out.println(graph.weights[i]);
                    }
                     */

                    //drawGraph();  --trzeba napisać
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(e.getMessage());
                    alert.showAndWait();
                }
            }
        });

        Button read = new Button("Read");
        read.setOnAction(e ->{
            if (graph == null)
                graph = new Graph();
            FileChooser filechooser = new FileChooser();
            filechooser.getExtensionFilters().addAll(new ExtensionFilter("txt file", "*.txt"));
            File readfile = filechooser.showOpenDialog(primarystage);
            try {
                if (readfile != null) {
                    Reader r = new FileReader(readfile);
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
                    // graph.minWeight = graph.getMinWeight();
                    minweight = graph.getMinWeight();
                    edgemin.setText(String.valueOf(minweight));
                    textweightlow.setText(String.valueOf(minweight));
                    //graph.maxWeight = graph.getMaxWeight();
                    maxweight = graph.getMaxWeight();
                    edgemax.setText(String.valueOf(maxweight));
                    textweighthigh.setText(String.valueOf(maxweight));
                    if (graph.bfs())
                        connected.setSelected(true);
                    else
                        notconnected.setSelected(true);
                    //drawGraph();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });
        root.getChildren().add(read);

        Button savegraph = new Button("Save graph");
        savegraph.setOnAction(e ->{
            FileChooser filechooser = new FileChooser();
            filechooser.getExtensionFilters().addAll(new ExtensionFilter("txt file", "*.txt"));
            File savefile = filechooser.showSaveDialog(primarystage);
            try {
                if (savefile != null) {
                    PrintWriter w = new PrintWriter(savefile);
                    for (int i=0; i<graph.weights.length; i++){
                        System.out.println(graph.weights[i]);
                    }
                    graph.saveGraph(w);
                    w.close();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        Button savepath = new Button("Save path");
        savepath.setOnAction(e ->{
            FileChooser filechooser = new FileChooser();
            filechooser.getExtensionFilters().addAll(new ExtensionFilter("txt file", "*.txt"));
            File savefile = filechooser.showSaveDialog(primarystage);
            try {
                if (savefile != null) {
                    PrintWriter w = new PrintWriter(savefile);
                    /*jeszcze nie ukończone*/
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        Button clean = new Button("Clean");
        Button delete = new Button("Delete");
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                graph = null;
                //draw_graph();
            }
        });
        HBox secondline = new HBox(70,generate,read,savegraph,savepath,clean,delete);

        root.getChildren().add(secondline);

        Canvas canvas = new Canvas(850,600);
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,850,600);

        root.getChildren().add(canvas);


        edgemin = new Label(String.valueOf(minweight));
        edgemax = new Label(String.valueOf(maxweight));
        Label edgecolor = new Label("Edge color scale");
        HBox colorfirst = new HBox(325, edgemin,edgecolor, edgemax);
        root.getChildren().add(colorfirst);

        ColorScale edgescale = new ColorScale(minweight, maxweight);
        ImageView scaleimg = new ImageView(edgescale.DrawColorScale(850, 20));

        root.getChildren().add(scaleimg);

        double nodecostmin = 0.0;
        double nodecostmax = 10.0;
        nodemin = new Label(String.valueOf(nodecostmin));
        nodemax = new Label(String.valueOf(nodecostmax));
        Label nodecolor = new Label("Node color scale");
        HBox colorsecond = new HBox(325, nodemin,nodecolor, nodemax);
        root.getChildren().add(colorsecond);

        Button changescale = new Button("Modify color scale");
        root.getChildren().add(changescale);

        Scene scene = new Scene(root,850,800);

        primarystage.setScene(scene);
        primarystage.setTitle("Kratka");
        primarystage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
