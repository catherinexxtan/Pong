package cs1302.game;

import java.util.Random;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.shape.Line;

import javafx.util.Duration;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.AnimationTimer;

/**
 * Create the classic arcade game Pong.
 */
public class Pong extends Application {

    // setting GUI dimensions
    private static final double GUI_WIDTH = 800;
    private static final double GUI_HEIGHT = 600;

    // setting constants
    private static final double BALL_RADIUS = 20;
    private static final double USER_BAR_WIDTH = 100;
    private static final double USER_BAR_HEIGHT = 20;

    private boolean isGameStarted;

    // score keeping
    private int user1Score = 0;
    private int user2Score = 0;

    // ball starting position
    private double ballX = GUI_WIDTH / 2;
    private double ballY = GUI_WIDTH / 2;

    // ball speeds for x and y direction
    private double ballXSpeed = 1;
    private double ballYSpeed = 1;

    // setting player positions for paddles
    private int paddle1X = 0;
    private double paddle1Y = GUI_HEIGHT / 2;
    private double paddle2X = GUI_WIDTH - 20; //- USER_BAR_WIDTH;
    private double paddle2Y = GUI_HEIGHT / 2;

    GraphicsContext graphics;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("PONG!!!");
        stage.setResizable(false);

        // creating background
        Canvas canvas = new Canvas(GUI_WIDTH, GUI_HEIGHT);
        graphics = canvas.getGraphicsContext2D();
        Line line = new Line();
        line.setStartX(GUI_WIDTH / 2);
        line.setEndX(GUI_WIDTH / 2);
        line.setStartY(GUI_HEIGHT);
        line.setEndY(0);
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(6);

        // making timeline
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(15), e -> runNow(graphics)));
        timeline.setCycleCount(Timeline.INDEFINITE);

        // mouse event handler
        canvas.setOnMouseMoved(e -> paddle1Y = e.getY());
        canvas.setOnMouseClicked(e -> isGameStarted = true);
        stage.setScene(new Scene(new StackPane(canvas, line)));
        stage.show();
        timeline.play();
    } // start

    /**
     * runNow creates the graphics for the Pong game.
     * @param graphics Takes in the GraphicsContext as parameter.
     */
    private void runNow(GraphicsContext graphics) {
        // background + text
        makeBackground();

        if (isGameStarted) {
            // set ball movement
            ballX += ballXSpeed;
            ballY += ballYSpeed;

            // making computer opponent (player 2)
            makeOpponent();

            // draw ball
            graphics.fillOval(ballX, ballY, BALL_RADIUS, BALL_RADIUS);
        } else {
            // displays game instructions
            startGameText();

            // set ball position to center
            ballX = GUI_WIDTH / 2;
            ballY = GUI_HEIGHT / 2;

            // set ball speed/direction
            ballXSpeed = new Random().nextInt(2) == 0 ? 1 : -1;
            ballYSpeed = new Random().nextInt(2) == 0 ? 1 : -1;
        } // if

        // make scoreboard
        makeScoreboard();

        // ensure ball stays within frame
        ballGoalkeeper();

        // calculating player scores
        player1Score();
        player2Score();

        // increase ball speed after player hits it
        if ( ((ballX + BALL_RADIUS > paddle2X) && ballY >= paddle2Y
             && ballY <= paddle2Y + USER_BAR_HEIGHT)
             || ((ballX < paddle1X + USER_BAR_WIDTH) && ballY >= paddle1Y
             && ballY <= paddle1Y + USER_BAR_HEIGHT)) {
            ballXSpeed += 2 * Math.signum(ballXSpeed);
            ballYSpeed += 2 * Math.signum(ballYSpeed);
            ballXSpeed *= -1;
            ballYSpeed *= -1;
        } // if
    } // run

    /**
     * player1Score keeps track of player 1 score.
     * @return boolean returns false if player1 has made winning shot.
     */
    public boolean player1Score() {
        if (ballX > paddle2X + USER_BAR_WIDTH) {
            user1Score++;
            isGameStarted = false;
        } // if
        return false;
    } // player1Score

    /**
     * player2Score keeps track of player 2 score.
     @return boolean returns false if player2 has made winning shot.
    */
    public boolean player2Score() {
        if (ballX < paddle1X - USER_BAR_WIDTH) {
            user2Score++;
            isGameStarted = false;
        } // if
        return false;
    } // player1Score

    /**
     * ballGoalkeeper ensures the ball does not go out of frame in the Y direction.
     */
    public void ballGoalkeeper() {
        if (ballY > GUI_HEIGHT || ballY < 0) {
            ballYSpeed *= -1;
        } // if
    } // ballGoalkeeper

    /**
     * makeScoreboard creates the scoreboard.
     * @return GraphicsContext returns visual graphics.
     */
    public GraphicsContext makeScoreboard() {
        graphics.fillText(user1Score + "\t\t\t\t\t\t\t" + user2Score, GUI_WIDTH / 2, 100);
        graphics.fillRect(paddle2X, paddle2Y, USER_BAR_HEIGHT, USER_BAR_WIDTH);
        graphics.fillRect(paddle1X, paddle1Y, USER_BAR_HEIGHT, USER_BAR_WIDTH);
        return graphics;
    } // makeScoreboard

    /**
     * makeBackground creates the visual aspect of the game.
     * @return GraphicsContext returns visual graphics for bg.
     */
    public GraphicsContext makeBackground() {
        // background + text
        graphics.setFill(Color.web("#C9BCFE"));
        graphics.fillRect(0, 0, GUI_WIDTH, GUI_HEIGHT);
        graphics.setFill(Color.WHITE);
        graphics.setFont(Font.font(25));
        return graphics;
    } // makeBackground

    /**
     * startGameText prompts the user with instructions.
     * @return GraphicsContext displays game instructions at start of game.
     */
    public GraphicsContext startGameText() {
        graphics.setFill(Color.WHITE);
        graphics.setTextAlign(TextAlignment.CENTER);
        graphics.strokeText("CLICK TO START\t\t\tMOVE WITH MOUSE", GUI_WIDTH / 2, GUI_HEIGHT / 2);
        return graphics;
    } // startGameText

    /**
     * makeOpponent creates player2. This is computer operated.
     * @return double returns the Y position for player2.
     */
    public double makeOpponent() {
        // making computer opponent (player 2)
        if (ballX < GUI_WIDTH - GUI_WIDTH / 4) {
            paddle2Y = ballY - USER_BAR_HEIGHT / 2;
        } else {
            paddle2Y = ballY > paddle2Y + USER_BAR_HEIGHT / 2 ? paddle2Y += 1 : paddle2Y - 1;
        } // if
        return paddle2Y;
    } // makeOpponent

    public static void main(String[] args) {
        launch(args);
    } // main

} // Pong
