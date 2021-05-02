import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class Controller {

    @FXML
    public Pane mainPane;
    @FXML
    public ToggleButton buttonRectangle;
    @FXML
    public ToggleButton buttonSquare;
    @FXML
    public ToggleButton buttonCircle;
    @FXML
    public ToggleButton buttonLine;
    @FXML
    public ToggleButton buttonMove;
    @FXML
    public ToggleButton buttonDelete;

    Shapes type; //type for drawing

    boolean isMove = false;
    boolean isDelete = false;

    //used for drawing shapes
    double firstX = 0;
    double firstY = 0;

    //used for moving shapes
    double x3 = 0;
    double y3 = 0;

    public void initialize() {    //this method works first when we run program

        //This ToggleGroup allows only 1 button to be active at a time
        ToggleGroup toggleButtons = new ToggleGroup();
        buttonRectangle.setToggleGroup(toggleButtons);
        buttonSquare.setToggleGroup(toggleButtons);
        buttonCircle.setToggleGroup(toggleButtons);
        buttonLine.setToggleGroup(toggleButtons);
        buttonMove.setToggleGroup(toggleButtons);
        buttonDelete.setToggleGroup(toggleButtons);

        //for icons
        ImageView rect = new ImageView(new Image(this.getClass().getResourceAsStream("icons/rectangle.png")));
        rect.setFitHeight(60);
        rect.setFitWidth(80);
        buttonRectangle.setGraphic(rect);

        ImageView square = new ImageView(new Image(this.getClass().getResourceAsStream("icons/square.png")));
        square.setFitHeight(60);
        square.setFitWidth(70);
        buttonSquare.setGraphic(square);

        ImageView circle = new ImageView(new Image(this.getClass().getResourceAsStream("icons/circle.png")));
        circle.setFitHeight(60);
        circle.setFitWidth(80);
        buttonCircle.setGraphic(circle);

        ImageView line = new ImageView(new Image(this.getClass().getResourceAsStream("icons/line.png")));
        line.setFitHeight(60);
        line.setFitWidth(80);
        buttonLine.setGraphic(line);

        ImageView move = new ImageView(new Image(this.getClass().getResourceAsStream("icons/move.png")));
        move.setFitHeight(60);
        move.setFitWidth(80);
        buttonMove.setGraphic(move);

        ImageView delete = new ImageView(new Image(this.getClass().getResourceAsStream("icons/eraser.png")));
        delete.setFitHeight(55);
        delete.setFitWidth(80);
        buttonDelete.setGraphic(delete);

        mainPane.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (isMove) return;      //to skip drawing if move or delete is selected
                if (isDelete) return;
                double secondX, secondY;
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    firstX = event.getX();
                    firstY = event.getY();
                }

                if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                    secondX = event.getX();
                    secondY = event.getY();

                    if (type != Shapes.LINE) { //we made this because Line would be inverted when drawn , so we didn't swap them, but other shapes needed
                        if (secondX < firstX) { //swap so that any shape can be drawn when we drag to left
                            double temp = secondX;
                            secondX = firstX;
                            firstX = temp;
                        }
                        if (secondY < firstY) {
                            double temp = secondY;
                            secondY = firstY;
                            firstY = temp;
                        }
                    }

                    if (type == null) return;  //to prevent exceptions when no button is toggled
                    //Our Idea Here is to create multiple layers of canvas instance, which allows us to put shapes on
                    //and when selected , delete and move can be done on the same layer
                    Canvas layer = createLayer(firstX, firstY, secondX, secondY); //create a canvas to contain the shape
                    draw(type, layer, new Point2D(secondX, secondY));  //draw the shape in the canvas
                    mainPane.getChildren().add(layer);  //add the new canvas to the mainPane


                    layer.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            if (isMove) { //if is Move toggled, get firstx and y when we click first, second x and y when released and set setX
                                double x4, y4;   //and y for the same canvas
                                if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                                    x3 = mouseEvent.getX();
                                    y3 = mouseEvent.getY();
                                }

                                if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                                    x4 = mouseEvent.getX();
                                    y4 = mouseEvent.getY();
                                    layer.setLayoutX(layer.getLayoutX() + x4 - x3);
                                    layer.setLayoutY(layer.getLayoutY() + y4 - y3);
                                }
                            }
                            if (isDelete) { //if mouse pressed , it will see if there is a layer there and removes it from pane
                                if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                                    mainPane.getChildren().remove(layer);
                                }
                            }
                        }
                    });
                }
            }
        });

    }

    public Canvas createLayer(double firstX, double firstY, double secondX, double secondY) {
        if (secondX < firstX) {
            double temp = secondX;
            secondX = firstX;
            firstX = temp;
        }
        if (secondY < firstY) {
            double temp = secondY;
            secondY = firstY;
            firstY = temp;
        }
        Canvas layer = new Canvas(Math.abs(firstX - secondX) + 2, Math.abs(firstY - secondY) + 2);
        layer.setLayoutX(firstX);
        layer.setLayoutY(firstY);
        return layer;
    }

    public void draw(Shapes type, Canvas layer, Point2D point2) {   //takes types and the the new layer that got created and secondX and secondY and draw
        //we used javafx documentation to see what each method takes as parameter
        switch (type) {
            case RECTANGLE -> layer.getGraphicsContext2D().strokeRect(1, 1, Math.abs(firstX - point2.getX()), Math.abs(firstY - point2.getY()));
            case SQUARE -> layer.getGraphicsContext2D().strokeRect(1, 1, Math.abs(firstX - point2.getX()), Math.abs(firstY - point2.getY()));
            case CIRCLE -> layer.getGraphicsContext2D().strokeOval(1, 1, Math.abs(firstX - point2.getX()), Math.abs(firstY - point2.getY()));
            case LINE -> layer.getGraphicsContext2D().strokeLine(firstX - layer.getLayoutX(), firstY - layer.getLayoutY(),
                    point2.getX() - layer.getLayoutX(), point2.getY() - layer.getLayoutY());
        }
    }

    //Each button when toggled, cancel move and delete and set shape for drawing
    public void drawRectangle(ActionEvent e) {
        isMove = false;
        isDelete = false;
        type = Shapes.RECTANGLE;

    }

    public void drawSquare(ActionEvent e) {
        isMove = false;
        isDelete = false;
        type = Shapes.SQUARE;

    }

    public void drawCircle(ActionEvent e) {
        isMove = false;
        isDelete = false;
        type = Shapes.CIRCLE;

    }

    public void drawLine(ActionEvent e) {

        isMove = false;
        isDelete = false;
        type = Shapes.LINE;
    }


    public void enableMove(ActionEvent e) {
        isMove = true;
        isDelete = false;
        type = null;
    }

    public void enableDelete(ActionEvent e) {
        isDelete = true;
        isMove = false;
        type = null;

    }

}

enum Shapes { //used enums to just keep types
    RECTANGLE, SQUARE, CIRCLE, LINE
}



