package andoksfooddeliverysystem;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class OpenStreet extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Create the WebView and WebEngine
            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();
            
            // Check if WebEngine is initialized properly
            if (webEngine == null) {
                System.out.println("Error: WebEngine not initialized!");
            }

            // Load the Leaflet.js map with OpenStreetMap
            String htmlContent = "<!DOCTYPE html>"
                    + "<html>"
                    + "<head>"
                    + "<link rel='stylesheet' href='https://unpkg.com/leaflet/dist/leaflet.css' />"
                    + "<script src='https://unpkg.com/leaflet/dist/leaflet.js'></script>"
                    + "</head>"
                    + "<body>"
                    + "<div id='map' style='width: 100%; height: 500px;'></div>"
                    + "<script>"
                    + "var map = L.map('map').setView([51.505, -0.09], 13);"
                    + "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(map);"
                    + "L.marker([51.505, -0.09]).addTo(map)"
                    + ".bindPopup('<b>Hello world!</b><br />I am a popup.').openPopup();"
                    + "</script>"
                    + "</body>"
                    + "</html>";

            // Load the HTML content in WebView
            webEngine.loadContent(htmlContent);
            
            // Check if content is loaded correctly
            if (webEngine.getLocation() == null || webEngine.getLocation().isEmpty()) {
                System.out.println("Error: Content not loaded in WebView.");
            }

            // Create the scene and show the stage
            Scene scene = new Scene(webView, 800, 600);
            primaryStage.setTitle("OpenStreetMap with Leaflet");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace(); // Print any errors that occur during startup
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
