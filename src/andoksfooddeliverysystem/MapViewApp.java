/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class MapViewApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create a WebView
        WebView webView = new WebView();

        // Get the URL of the HTML file in your resources
        String mapUrl = getClass().getResource("/maps/map.html").toExternalForm();

        // Load the HTML file into the WebView
        webView.getEngine().load(mapUrl);

        // Set up the layout
        StackPane root = new StackPane();
        root.getChildren().add(webView);

        // Create and set up the scene
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Google Maps Integration");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

