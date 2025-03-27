package andoksfooddeliverysystem;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
    private boolean darkMode = false;
    private Scene scene;
    private Label title;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(15);
        root.setStyle("-fx-padding: 30; -fx-alignment: center;");

        title = new Label("Welcome! Please Log In");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: black;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(200);

        // Password fields (hidden and visible)
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(200);

        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText("Password");
        visiblePasswordField.setPrefWidth(200);
        visiblePasswordField.setManaged(false); // Hide it initially
        visiblePasswordField.setVisible(false);

        // Eye icon button
        ImageView eyeIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/eye.png")));
        eyeIcon.setFitWidth(20);
        eyeIcon.setFitHeight(20);
        Button eyeButton = new Button();
        eyeButton.setGraphic(eyeIcon);
        eyeButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 5;");
        
        // StackPane to overlay eye icon inside the PasswordField
        StackPane passwordContainer = new StackPane();
        passwordContainer.getChildren().addAll(passwordField, visiblePasswordField, eyeButton);
        StackPane.setAlignment(eyeButton, Pos.CENTER_RIGHT);
        eyeButton.setTranslateX(-10); // Move the eye icon inside the field

        // Toggle password visibility
        eyeButton.setOnAction(e -> {
            boolean isHidden = passwordField.isVisible();
            passwordField.setVisible(!isHidden);
            passwordField.setManaged(!isHidden);
            visiblePasswordField.setVisible(isHidden);
            visiblePasswordField.setManaged(isHidden);

            // Sync text between fields
            if (isHidden) {
                visiblePasswordField.setText(passwordField.getText());
            } else {
                passwordField.setText(visiblePasswordField.getText());
            }
        });

        // Sync text while typing
        passwordField.textProperty().bindBidirectional(visiblePasswordField.textProperty());

      

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        
        loginBtn.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            User loggedInUser = User.login(username, password);

            if (loggedInUser != null) {
                System.out.println("Login successful! User role: " + loggedInUser.getRole());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Login successful!", ButtonType.OK);
                alert.showAndWait();
                // Proceed to the main application window
            } else {
                System.out.println("Invalid credentials.");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid username or password!", ButtonType.OK);
                alert.showAndWait();
            }
        });
        
        Button signUpBtn = new Button("Sign Up");
        signUpBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        signUpBtn.setOnAction(e -> {
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow(); // Get current stage
            stage.close();  // Close current window

            // Open SignUp window
            Signup signUpWindow = new Signup();
            Stage signUpStage = new Stage();
            try {
                signUpWindow.start(signUpStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });



        // HBox to place buttons side by side
        HBox buttonContainer = new HBox(10); // 10 is spacing between buttons
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(loginBtn, signUpBtn);



        scene = new Scene(root, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/styles/light-theme.css").toExternalForm());

        Button toggleThemeBtn = new Button("Toggle Dark Mode");
        toggleThemeBtn.setOnAction(e -> {
            darkMode = !darkMode;
            scene.getStylesheets().clear();
            String theme = darkMode ? "/styles/dark-theme.css" : "/styles/light-theme.css";
            scene.getStylesheets().add(getClass().getResource(theme).toExternalForm());

            // Change title text color dynamically
            title.setStyle(darkMode
                    ? "-fx-font-size: 20px; -fx-text-fill: white;"
                    : "-fx-font-size: 20px; -fx-text-fill: black;");
        });

        // Fade-in animation
        applyFadeAnimation(title, usernameField, passwordContainer, loginBtn, toggleThemeBtn);

        root.getChildren().addAll(title, usernameField, passwordContainer, buttonContainer, toggleThemeBtn);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Screen");
        primaryStage.setWidth(400);
        primaryStage.setHeight(800);


        primaryStage.show();
    }

    private void applyFadeAnimation(javafx.scene.Node... nodes) {
        for (javafx.scene.Node node : nodes) {
            FadeTransition fade = new FadeTransition(Duration.seconds(1), node);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
