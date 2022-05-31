package Kratka;

import graph.Graph;
import graph.Path;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
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
    public Color nodeColor = Color.ANTIQUEWHITE;
    public Color pathColor = Color.WHITE;
    public ColorScale edgeScale = new ColorScale();
    public ColorScale nodeScale = new ColorScale();
    public ArrayList<Integer> nodelist = new ArrayList<>();     //lista wierzchołków końcowych


    @Override
    public void start(Stage primarystage) throws Exception {

        Label size = new Label("Size:");            //Opis pól podawania rozmiaru grafu
        TextField textrow = new TextField();
        textrow.setMaxWidth(50);
        textrow.setText(String.valueOf(row));
        Text sizex = new Text("x");
        TextField textcol = new TextField();
        textcol.setMaxWidth(50);
        textcol.setText(String.valueOf(col));
        HBox sizebox = new HBox(5,textrow,sizex,textcol);

        Label weight = new Label("Edge weight range:");         //Opis pól podawania zakresu losowanych wag
        TextField textweightlow = new TextField();
        textweightlow.setMaxWidth(50);
        textweightlow.setText(String.valueOf(minweight));
        Text weightsymbol = new Text("-");
        TextField textweighthigh = new TextField();
        textweighthigh.setMaxWidth(50);
        textweighthigh.setText(String.valueOf(maxweight));
        HBox weightbox = new HBox(textweightlow,weightsymbol,textweighthigh);

        Label con = new Label("Connectivity");              //Opis wybioru opcji spójności grafu
        RadioButton connected = new RadioButton("Connected");
        connected.setSelected(true);
        RadioButton notconnected = new RadioButton("Not connected");
        RadioButton randcon = new RadioButton("Random");
        ToggleGroup connectivity = new ToggleGroup();
        connected.setToggleGroup(connectivity);
        notconnected.setToggleGroup(connectivity);
        randcon.setToggleGroup(connectivity);
        VBox connectbox = new VBox(connected,notconnected,randcon);

        HBox firstline = new HBox(40, size,sizebox,weight,weightbox,con,connectbox);        //pierwsza linijka

        FlowPane root = new FlowPane();
        root.setVgap(5);
        root.getChildren().add(firstline);

        Label edgemin = new Label(String.valueOf(minweight));               //Inicjalizowanie etykiet tekstowych dla mapy kolorowej
        Label edgemax = new Label(String.valueOf(maxweight));                       //aby dalej można było ich zmieniać
        Label nodemin = new Label();
        Label nodemax = new Label();


        Button generate = new Button("Generate");       //Generator
        generate.setOnAction(actionEvent -> {
            try {
                String trow = textrow.getText();        //pobieramy dane z pól tekstowych i sprawdzamy poprawność
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
                graph = null;       //jeżeli wszystkie pobrane dane poprawne, zmieniamy poprzednie dane, rysunki i teksty z etykiet
                path = null;
                nodelist.clear();
                gc.setFill(Color.BLACK);
                gc.fillRect(0,0,850,600);
                graph = new Graph(row, col);        //nowy graf

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
                graph.generateGraph(connection);        //generujemy graf zgodnie z podanymi parametrami

                graph.drawGraph(gc, 850, 600, edgeScale, nodeColor);        //rusujemy graf
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(e.getMessage());
                alert.showAndWait();
            }
        });

        Button read = new Button("Read graph");       //Wczytywanie z pliku
        read.setOnAction(e ->{
            FileChooser filechooser = new FileChooser();
            filechooser.getExtensionFilters().addAll(new ExtensionFilter("txt file", "*.txt"));
            File readfile = filechooser.showOpenDialog(primarystage);
            try {
                if (readfile != null) {
                    BufferedReader r = new BufferedReader(new FileReader(readfile));
                    graph = null;           //usuwamy poprzednie dane
                    graph = new Graph();
                    path = null;
                    gc.setFill(Color.BLACK);
                    gc.fillRect(0, 0, 850, 600);
                    nodelist.clear();
                    graph.readGraph(r);     //czytamy graf
                    r.close();
                    if (graph.ErrorMassage != null) {       //jeżeli otrzymujemy błąd, to przekazujemy użytkownikowi i kończymy wczytywanie
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(graph.ErrorMassage);
                        alert.showAndWait();
                        return;
                    }
                    if (graph.WarningMassage != null) {         //komunikaty o błędach poprawionych
                        for (int i = 0; i < graph.WarningMassage.size(); i++) {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("WARNING");
                            alert.setHeaderText(graph.WarningMassage.get(i));
                            alert.showAndWait();
                        }
                    }
                    row = graph.row;        //zmieniamy dane w etykietach, polach i zmiennych
                    textrow.setText(String.valueOf(row));
                    col = graph.col;
                    textcol.setText(String.valueOf(col));
                    graph.minWeight = graph.getMinWeight();
                    minweight = graph.getMinWeight();
                    edgemin.setText(String.valueOf(minweight));
                    textweightlow.setText(String.valueOf(minweight));
                    edgeScale.changeMin(minweight);
                    graph.maxWeight = graph.getMaxWeight();
                    maxweight = graph.getMaxWeight();
                    edgemax.setText(String.valueOf(maxweight));
                    textweighthigh.setText(String.valueOf(maxweight));
                    edgeScale.changeMax(maxweight);

                    nodemax.setText("");
                    nodemin.setText("");

                    if (graph.bfs())
                        connected.setSelected(true);
                    else
                        notconnected.setSelected(true);
                    graph.drawGraph(gc,850,600, edgeScale, nodeColor);
                }
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(ex.getMessage());
                alert.showAndWait();
            }

        });
        root.getChildren().add(read);

        Button savegraph = new Button("Save graph");        //Zapisywanie grafu do pliku
        savegraph.setOnAction(e ->{
            FileChooser filechooser = new FileChooser();
            filechooser.getExtensionFilters().addAll(new ExtensionFilter("txt file", "*.txt"));
            File savefile = filechooser.showSaveDialog(primarystage);
            try {
                if (savefile != null) {
                    PrintWriter w = new PrintWriter(savefile);
                    graph.saveGraph(w);     //zapis
                    w.close();
                }
            } catch (IOException ex) {      //komunikat o błędzie
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(ex.getMessage());
                alert.showAndWait();
            }
        });


        Button savepath = new Button("Save paths");

        savepath.setOnAction(e ->{          //zapisanie wyznaczonych ścieżek
            if (nodelist.size() == 0) {         //nie ma ścieżek
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
                        path.savePath(w, graph, integer);       //zapis ścieżki
                    }
                    w.close();
                }
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(ex.getMessage());
                alert.showAndWait();
            }
        });
        Button clean = new Button("Clean paths");       //czyszczenie ścieżek
        clean.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                clearPaths();
                nodemin.setText("");
                nodemax.setText("");
            }
        });
        Button delete = new Button("Delete");           //usuwanie grafu

        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                graph = null;       //usuwamy graf i powiązane zmienne
                path = null;
                nodelist.clear();
                gc.setFill(Color.BLACK);        //czyścimy rysunek
                gc.fillRect(0, 0, 850, 600);
                edgemax.setText("");
                edgemin.setText("");
                nodemin.setText("");
                nodemax.setText("");

            }
        });

        HBox secondline = new HBox(70, generate, read, savegraph, savepath, clean, delete);
        root.getChildren().add(secondline);


        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                MouseButton mb = me.getButton();
                int startNode = 0;
                int finishNode = graph.row * graph.col - 1;
                //wierzchołek startowy - wybór poprzez lewy przycisk myszy
                if (mb == MouseButton.PRIMARY) {
                    //clearPaths();
                    if (!graph.bfs()) {         //sprawdzamy spójność
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Graf nie jest spójny! Nie można użyć algorytmu Dijkstry");
                        alert.showAndWait();
                        return;
                    }
                    //indeks wierzchołka startowego jest wyznaczany na podstawie współrzędnych miejsca kliknięcia
                    double xl = me.getX();
                    double yl = me.getY();
                    double sum = 1000; //zmienna pomocnicza, która na pewno będzie większa niż suma różnicy między współrzędnymi wierzchołka a kliknięcia
                    for (int i = 0; i < (graph.col * graph.row); i++) {
                        if (sum > Math.abs(graph.nodeCoordinates[i][0] - xl) + Math.abs(graph.nodeCoordinates[i][1] - yl)) {
                            sum = Math.abs(graph.nodeCoordinates[i][0] - xl) + Math.abs(graph.nodeCoordinates[i][1] - yl);
                            startNode = i;
                        }
                    }
                    path = new Path();
                    path = graph.dijkstra(startNode);       //używamy algorytmu Dijkstry

                    double nodecostmin = path.getMinCost();     //zmieniamy powiązane dane i etykiety
                    double nodecostmax = path.getMaxCost();

                    nodemin.setText(String.valueOf(nodecostmin));
                    nodemax.setText(String.format("%.2f",nodecostmax));

                    nodeScale.set(nodecostmin, nodecostmax);

                    addColorNodes(850, 600, nodeScale);     //kolorujemy wierzchołki poprzez metodę
                    gc.setFill(pathColor);              //odznaczamy zaznaczony wierzchołek kolorem
                    gc.fillOval(graph.nodeCoordinates[startNode][0], graph.nodeCoordinates[startNode][1], graph.getNodesSize(850,600), graph.getNodesSize(850,600));


                }
                //wierzchołek końcowy - wybór poprzez prawy przycisk myszy
                else if (mb == MouseButton.SECONDARY) {
                    if (path == null){      //poprzednio nie został użyty algorytm Dijkstry
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Nie został uruchomiomy algorytm Dijkstry! Proszę spoczątku kliknąć na początkowy wierzchołek PRAWYM klawiszem!");
                        alert.showAndWait();
                        return;
                    }
                    double xr = me.getX();      //pobieramy współrzędne kliknięcia i wyznaczamy odpowiadający wierzchołek
                    double yr = me.getY();
                    double sum = 1000;
                    for (int i=0; i<(graph.col * graph.row); i++) {
                        if (sum > Math.abs(graph.nodeCoordinates[i][0] - xr) + Math.abs(graph.nodeCoordinates[i][1] - yr)){
                            sum = Math.abs(graph.nodeCoordinates[i][0] - xr) + Math.abs(graph.nodeCoordinates[i][1] - yr);
                            finishNode = i;
                        }
                    }
                    gc.setFill(pathColor);      //odznaczamy wierzchołek
                    gc.fillOval(graph.nodeCoordinates[finishNode][0], graph.nodeCoordinates[finishNode][1], graph.getNodesSize(850,600), graph.getNodesSize(850,600));
                    nodelist.add(finishNode);       //dodajemy wierzchołek końcowy do listy
                    drawPath(finishNode, pathColor);        //rysujemy ścieżkę
                }

            }
        };

        Canvas canvas = new Canvas(850, 600);
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 850, 600);
        canvas.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);

        Tooltip tooltip = new Tooltip();        //pokazywanie numeru wierzchołka po najechaniu na niego muszką
        tooltip.setStyle("-fx-font-size: 15");
        Tooltip.install(canvas, tooltip);
        canvas.setOnMouseMoved(e ->{
            if (graph == null){
                return;
            }
            int startNode = 0;
            double xl = e.getX();
            double yl = e.getY();
            double sum = 1000;
            for (int i = 0; i < (graph.col * graph.row); i++) {
                if (sum > Math.abs(graph.nodeCoordinates[i][0] - xl) + Math.abs(graph.nodeCoordinates[i][1] - yl)) {
                    sum = Math.abs(graph.nodeCoordinates[i][0] - xl) + Math.abs(graph.nodeCoordinates[i][1] - yl);
                    startNode = i;
                }
            }
            tooltip.setText(Integer.toString(startNode));
        });
        canvas.setOnMouseExited(e->{
            tooltip.hide();
        });

        root.getChildren().add(canvas);


        Label edgecolorlable = new Label("Edge color scale");       //segment z mapą kolorów
        HBox colorfirst = new HBox(350, edgemin, edgecolorlable, edgemax);
        root.getChildren().add(colorfirst);

        edgeScale.set(minweight, maxweight);
        ImageView scaleimg = new ImageView();
        scaleimg.setImage(edgeScale.DrawColorScale(850, 20));

        root.getChildren().add(scaleimg);

        nodemin.setText("");
        nodemax.setText("");
        Label nodecolorlable = new Label("Node color scale");
        HBox colorsecond = new HBox(350, nodemin, nodecolorlable, nodemax);
        root.getChildren().add(colorsecond);


        Button changescale = new Button("Modify color scale");      //Moduł zmiany kolorów mapy, wierzchołków i ścieżek
        changescale.setOnAction(e -> {
            ColorPicker colorPicker1 = new ColorPicker(); //color max weight edge
            colorPicker1.setValue(edgeScale.maxColor);
            ColorPicker colorPicker2 = new ColorPicker(); //color min weight edge
            colorPicker2.setValue(edgeScale.minColor);
            ColorPicker colorPicker3 = new ColorPicker(); //color node
            colorPicker3.setValue(nodeColor);
            ColorPicker colorPicker4 = new ColorPicker(); //color path
            colorPicker4.setValue(pathColor);
            Label cpLabel1 = new Label("Max weight edge color");
            Label cpLabel2 = new Label("Min weight edge color");
            Label cpLabel3 = new Label("Node color");
            Label cpLabel4 = new Label("Path color");
            Label cpTitle = new Label("Choose colors: ");
            Label[] cpLabels;
            cpLabels = new Label[]{cpLabel1, cpLabel2, cpLabel3, cpLabel4};
            FlowPane chooseColors = new FlowPane(Orientation.VERTICAL);
            chooseColors.setPadding(new Insets(50, 50, 50, 50));
            chooseColors.setVgap(10);
            chooseColors.setColumnHalignment(HPos.CENTER);
            ColorPicker[] colorPickers;
            colorPickers = new ColorPicker[]{colorPicker1, colorPicker2, colorPicker3, colorPicker4};
            chooseColors.getChildren().add(cpTitle);
            for (int i=0; i<colorPickers.length; i++) {
                chooseColors.getChildren().add(cpLabels[i]);
                chooseColors.getChildren().add(colorPickers[i]);
            }
            Stage stageColorScale = new Stage();
            stageColorScale.setWidth(250);
            stageColorScale.setHeight(450);
            Button action = new Button("OK");
            action.setOnAction(k ->{                //po zatwierdzeniu zmieniamy rysunek według istniejących danych
                edgeScale.changeMaxColor(colorPicker1.getValue());
                edgeScale.changeMinColor(colorPicker2.getValue());
                nodeScale.changeMaxColor(edgeScale.maxColor);
                nodeScale.changeMinColor(edgeScale.minColor);
                nodeColor = colorPicker3.getValue();
                pathColor = colorPicker4.getValue();
                stageColorScale.close();
                scaleimg.setImage(edgeScale.DrawColorScale(850, 20));
                if (graph != null){
                    gc.setFill(Color.BLACK);
                    gc.fillRect(0, 0, 850, 600);
                    graph.drawGraph(gc, 850,600, edgeScale, nodeColor);
                    if (path != null){
                        addColorNodes(850,600,nodeScale);
                        gc.setFill(pathColor);
                        int startNode = path.getStart();
                        gc.fillOval(graph.nodeCoordinates[startNode][0], graph.nodeCoordinates[startNode][1], graph.getNodesSize(850,600), graph.getNodesSize(850,600));}
                    if (nodelist.size() != 0) {
                        for (Integer integer : nodelist){
                            gc.setFill(pathColor);
                            gc.fillOval(graph.nodeCoordinates[integer][0], graph.nodeCoordinates[integer][1], graph.getNodesSize(850,600), graph.getNodesSize(850,600));
                            drawPath(integer, pathColor);}
                    }
                }
            });
            chooseColors.getChildren().add(action);
            Scene sceneColorScale = new Scene(chooseColors);
            stageColorScale.setScene(sceneColorScale);
            stageColorScale.setResizable(false);
            stageColorScale.show();

        });

        CheckBox indices = new CheckBox("Display nodes indices");       //Opcja pokazywania numerów wewnątrz wierzchąłków
        indices.setOnAction(e -> {
            if (indices.isSelected()) {
                graph.displayNodesIndices(gc, 850, 600);
            }
        });
        HBox bottomline = new HBox(600, changescale, indices);
        root.getChildren().add(bottomline);

        Scene scene = new Scene(root, 850, 800);

        primarystage.setScene(scene);
        primarystage.setTitle("Kratka");
        primarystage.show();
    }

    public void addColorNodes(int width, int height, ColorScale scale) {        //metoda dodawania kolorów do wierzchołków
        for (int i=0; i<row; i++){
            for (int j=0; j<col; j++){
                gc.setFill(scale.ColorOfValue(path.cost[i * graph.col + j]));       //rysujemy wierzchołki kolorem, odpowiadającym kosztu dojścia
                gc.fillOval(graph.nodeCoordinates[i*col + j][0], graph.nodeCoordinates[i*col + j][1], graph.getNodesSize(width,height), graph.getNodesSize(width,height));
            }
        }
    }
    public void drawPath(int finishNode, Color color){      //rysowanie ścieżki
        double nodeSize = graph.getNodesSize(850,600);
        gc.setStroke(color);
        gc.setLineWidth(nodeSize/2);
        int sEdge = finishNode;
        int fEdge = path.last[sEdge];
        while (fEdge != -1){        //schodzimy od wierzchołka końcowego do początkowego
            gc.strokeLine(graph.nodeCoordinates[sEdge][0] + nodeSize/2, graph.nodeCoordinates[sEdge][1] + nodeSize/2,
                    graph.nodeCoordinates[fEdge][0] + nodeSize/2, graph.nodeCoordinates[fEdge][1] + nodeSize/2);
            sEdge = fEdge;
            fEdge = path.last[fEdge];
        }
    }
    public void clearPaths(){       //czyszczenie ścieżek
        gc.setFill(Color.BLACK);        //usuwamy rysunek
        gc.fillRect(0, 0, 850, 600);
        graph.drawGraph(gc, 850, 600, edgeScale, nodeColor);       //rysujemy graf
        path = null;        //usuwamy powiązane dane
        nodelist.clear();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
