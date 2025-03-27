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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;



public class Signup extends Application  {

    private boolean darkMode = false;
    private Label title;
    private Scene scene;

    public void start(Stage primaryStage) {
        Stage signUpStage = new Stage();
        signUpStage.setTitle("Sign Up");

        // Make it a modal window (blocks interaction with login)
        signUpStage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(15);
        root.setStyle("-fx-padding: 30; -fx-alignment: center;");

        title = new Label("Welcome! Sign Up your account");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: black;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(200);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setPrefWidth(200);

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
        eyeButton.setTranslateX(-10);

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
                // Close the current Signup window
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.close();  

                // Open the LogIN window
                Stage loginStage = new Stage();
                Main loginWindow = new Main();
                try {
                    loginWindow.start(loginStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });


        
        // Sign Up Button
        Button signUpBtn = new Button("Sign Up");
        signUpBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        signUpBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            
             if (User.emailExists(email)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Email already in use! Try another one.", ButtonType.OK);
                alert.showAndWait();
                return;  // Stop signup process
            }

            // Validate user input
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "All fields are required!", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            boolean isSignedUp = User.signUp(username, email, password);

            if (isSignedUp) {
                System.out.println("Sign Up successful!");
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Signed Up successfully!", ButtonType.OK);
                alert.showAndWait();

                // Clear fields after successful signup
                usernameField.clear();
                emailField.clear();
                passwordField.clear();

                // TODO: Redirect to login or main application window
            } else {
                System.out.println("Sign Up failed.");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Sign Up failed. Try again!", ButtonType.OK);
                alert.showAndWait();
            }
        });

        // HBox for buttons
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
         buttonContainer.getChildren().addAll(loginBtn, signUpBtn);

        // Dark Mode Toggle
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

        // Apply fade-in animation
        applyFadeAnimation(title, usernameField, emailField, passwordContainer, buttonContainer, toggleThemeBtn);

        root.getChildren().addAll(title, usernameField, emailField, passwordContainer, buttonContainer, toggleThemeBtn);

        scene = new Scene(root, 400, 400);
        scene.getStylesheets().add(getClass().getResource("/styles/light-theme.css").toExternalForm());

        signUpStage.setScene(scene);
         signUpStage.setWidth(400);
        signUpStage.setHeight(800);
         signUpStage.setTitle("Sign Up Screen");
        signUpStage.show(); // Open in a new window and wait until it's closed
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
