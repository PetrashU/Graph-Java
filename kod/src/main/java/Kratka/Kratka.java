package Kratka;

import javafx.application.Application;
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
import java.io.File;

public class Kratka extends Application {

    public int row = 10;
    public int col = 10;
    public double minweight = 0.0;
    public double maxweight = 10.0;
    public GraphicsContext gc;


    @Override
    public void start(Stage primarystage) throws Exception {

        Label size = new Label("Size:");
        TextField trow = new TextField();
        trow.setMaxWidth(50);
        trow.setText(String.valueOf(row));
        Text sizex = new Text("x");
        TextField tcol = new TextField();
        tcol.setMaxWidth(50);
        tcol.setText(String.valueOf(col));
        HBox sizebox = new HBox(5,trow,sizex,tcol);

        Label weight = new Label("Edge weight range:");
        TextField weightlow = new TextField();
        weightlow.setMaxWidth(50);
        weightlow.setText(String.valueOf(minweight));
        Text weightsymbol = new Text("-");
        TextField weighthigh = new TextField();
        weighthigh.setMaxWidth(50);
        weighthigh.setText(String.valueOf(maxweight));
        HBox weightbox = new HBox(weightlow,weightsymbol,weighthigh);

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
        
        Button read = new Button("Read");
        read.setOnAction(e ->{
            FileChooser filechooser = new FileChooser();
            filechooser.getExtensionFilters().addAll(new ExtensionFilter("txt file", "*.txt"));
            File readfile = filechooser.showOpenDialog(primarystage);
        });
        root.getChildren().add(read);
        
        Button savegraph = new Button("Save graph");
        Button savepath = new Button("Save path");
        Button clean = new Button("Clean");
        Button delete = new Button("Delete");
        HBox secondline = new HBox(70,generate,read,savegraph,savepath,clean,delete);

        root.getChildren().add(secondline);

        Canvas canvas = new Canvas(850,600);
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,850,600);

        root.getChildren().add(canvas);

        double edgeweightmin = minweight;
        double edgeweightmax = maxweight;
        Label edgemin = new Label(String.valueOf(edgeweightmin));
        Label edgemax = new Label(String.valueOf(edgeweightmax));
        Label edgecolor = new Label("Edge color scale");
        HBox colorfirst = new HBox(325, edgemin,edgecolor, edgemax);
        root.getChildren().add(colorfirst);

        ColorScale edgescale = new ColorScale(minweight, maxweight);
        ImageView scaleimg = new ImageView(edgescale.DrawColorScale(850, 20));

        root.getChildren().add(scaleimg);

        double nodecostmin = 0.0;
        double nodecostmax = 10.0;
        Label nodemin = new Label(String.valueOf(nodecostmin));
        Label nodemax = new Label(String.valueOf(nodecostmax));
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

