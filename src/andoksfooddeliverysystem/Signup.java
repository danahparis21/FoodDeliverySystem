package andoksfooddeliverysystem;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Signup extends Application {
    private boolean darkMode = false;
    private Scene scene;
    private Label title;
    
    // Andok's color palette
    private final String ANDOKS_RED = "#D32F2F";
    private final String ANDOKS_YELLOW = "#FFD54F";
    private final String ANDOKS_WHITE = "#FFFFFF";
    private final String ANDOKS_DARK_RED = "#B71C1C";

    @Override
    public void start(Stage primaryStage) {
        Stage signUpStage = new Stage();
        
        // Make it a modal window (blocks interaction with login)
        signUpStage.initModality(Modality.APPLICATION_MODAL);
        
        // Create main layout
        BorderPane mainLayout = new BorderPane();
        
        // Left side for logo and branding
        VBox leftPanel = createBrandingPanel();
        
        // Right side for signup form
        VBox rightPanel = createSignupFormPanel();
        
        mainLayout.setLeft(leftPanel);
        mainLayout.setCenter(rightPanel);
        
        scene = new Scene(mainLayout, 800, 550);
        scene.getStylesheets().add(getClass().getResource("/styles/light-theme.css").toExternalForm());

        // Make window corners rounded with transparent background
        signUpStage.initStyle(StageStyle.UNDECORATED);
        Rectangle clipRect = new Rectangle(800, 550);
        clipRect.setArcWidth(20);
        clipRect.setArcHeight(20);
        mainLayout.setClip(clipRect);
        
        // Add drop shadow to the entire window
        mainLayout.setEffect(new DropShadow(15, Color.gray(0.5, 0.5)));
        
        // Set background color
        mainLayout.setStyle("-fx-background-color: " + ANDOKS_WHITE + ";");
        
        // Add window control buttons (minimize, close)
        HBox windowControls = createWindowControls(signUpStage);
        mainLayout.setTop(windowControls);
        
        signUpStage.setScene(scene);
        signUpStage.setTitle("Andok's Food Delivery - Sign Up");
        signUpStage.show();
        
        
        // Apply animations
        applyEntranceAnimations(leftPanel, rightPanel);
    }
    
    private VBox createBrandingPanel() {
        VBox branding = new VBox(20);
        branding.setPrefWidth(350);
        branding.setAlignment(Pos.CENTER);
        branding.setPadding(new Insets(40));
        branding.setStyle("-fx-background-color: " + ANDOKS_RED + ";");
        
        // Logo
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/icons/andoksLogo.png")));
        // If you don't have the logo, you can create a text logo
        if (logo.getImage().isError()) {
            // Create text logo as fallback
            Text logoText = new Text("ANDOK'S");
            logoText.setFont(Font.font("System", FontWeight.BOLD, 48));
            logoText.setFill(Color.web(ANDOKS_YELLOW));
            
            // Create a chicken icon using text as a simple placeholder
            Text chickenIcon = new Text("🍗");
            chickenIcon.setFont(Font.font("System", 60));
            chickenIcon.setFill(Color.web(ANDOKS_YELLOW));
            
            VBox textLogo = new VBox(10, chickenIcon, logoText);
            textLogo.setAlignment(Pos.CENTER);
            branding.getChildren().add(textLogo);
        } else {
            logo.setFitWidth(200);
            logo.setPreserveRatio(true);
            branding.getChildren().add(logo);
        }
        
        // Tagline
        Label tagline = new Label("Tasty Filipino Chicken");
        tagline.setFont(Font.font("System", FontWeight.BOLD, 18));
        tagline.setStyle("-fx-text-fill: " + ANDOKS_YELLOW + ";");
        
        Label deliveryTag = new Label("FOOD DELIVERY SYSTEM");
        deliveryTag.setFont(Font.font("System", 14));
        deliveryTag.setStyle("-fx-text-fill: " + ANDOKS_WHITE + ";");
        
        branding.getChildren().addAll(tagline, deliveryTag);
        
        return branding;
    }
    
    private VBox createSignupFormPanel() {
        VBox signupForm = new VBox(25);
        signupForm.setAlignment(Pos.CENTER);
        signupForm.setPadding(new Insets(40));
        signupForm.setStyle("-fx-background-color: " + ANDOKS_WHITE + ";");
        
        // Welcome text
        title = new Label("Create Account");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: " + ANDOKS_RED + ";");
        
        Label subtitle = new Label("Join Andok's delivery service");
        subtitle.setFont(Font.font("System", 14));
        subtitle.setStyle("-fx-text-fill: #757575;");
        
        VBox headerBox = new VBox(5, title, subtitle);
        headerBox.setAlignment(Pos.CENTER);
        
        // Username field with icon
        HBox usernameBox = new HBox(0);
        usernameBox.setAlignment(Pos.CENTER_LEFT);
        usernameBox.setPrefWidth(300);
        usernameBox.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 1px;"
        );
        
        // User icon
        ImageView userIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/user.png")));
        StackPane userIconPane = new StackPane();
        if (!userIcon.getImage().isError()) {
            userIcon.setFitHeight(16);
            userIcon.setFitWidth(16);
            userIconPane.getChildren().add(userIcon);
        } else {
            Label userLabel = new Label("👤");
            userIconPane.getChildren().add(userLabel);
        }
        userIconPane.setPadding(new Insets(0, 10, 0, 15));
        
        // Username field
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefHeight(40);
        usernameField.setPrefWidth(270);
        usernameField.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background-radius: 0;" +
            "-fx-border-color: transparent;" +
            "-fx-border-width: 0;" +
            "-fx-padding: 0 10px 0 0;" +
            "-fx-font-size: 14px;"
        );
        
        // Add components to the username box
        usernameBox.getChildren().addAll(userIconPane, usernameField);
        
        // Handle focus effect on the HBox container
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                usernameBox.setStyle(
                    "-fx-background-color: #f5f5f5;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-color: " + ANDOKS_RED + ";" +
                    "-fx-border-radius: 20;" +
                    "-fx-border-width: 2px;"
                );
            } else {
                usernameBox.setStyle(
                    "-fx-background-color: #f5f5f5;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-color: #e0e0e0;" +
                    "-fx-border-radius: 20;" +
                    "-fx-border-width: 1px;"
                );
            }
        });
        
        // Email field with icon
        HBox emailBox = new HBox(0);
        emailBox.setAlignment(Pos.CENTER_LEFT);
        emailBox.setPrefWidth(300);
        emailBox.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 1px;"
        );
        
        // Email icon
        ImageView emailIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/email.png")));
        StackPane emailIconPane = new StackPane();
        if (!emailIcon.getImage().isError()) {
            emailIcon.setFitHeight(16);
            emailIcon.setFitWidth(16);
            emailIconPane.getChildren().add(emailIcon);
        } else {
            Label emailLabel = new Label("✉️");
            emailIconPane.getChildren().add(emailLabel);
        }
        emailIconPane.setPadding(new Insets(0, 10, 0, 15));
        
        // Email field
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setPrefHeight(40);
        emailField.setPrefWidth(270);
        emailField.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background-radius: 0;" +
            "-fx-border-color: transparent;" +
            "-fx-border-width: 0;" +
            "-fx-padding: 0 10px 0 0;" +
            "-fx-font-size: 14px;"
        );
        
        // Add components to the email box
        emailBox.getChildren().addAll(emailIconPane, emailField);
        
        // Handle focus effect on the HBox container
        emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                emailBox.setStyle(
                    "-fx-background-color: #f5f5f5;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-color: " + ANDOKS_RED + ";" +
                    "-fx-border-radius: 20;" +
                    "-fx-border-width: 2px;"
                );
            } else {
                emailBox.setStyle(
                    "-fx-background-color: #f5f5f5;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-color: #e0e0e0;" +
                    "-fx-border-radius: 20;" +
                    "-fx-border-width: 1px;"
                );
            }
        });
        
        // Password fields (hidden and visible) with icons
        HBox passwordBox = new HBox(0);
        passwordBox.setAlignment(Pos.CENTER_LEFT);
        passwordBox.setPrefWidth(300);
        passwordBox.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 1px;"
        );
        
        // Lock icon
        ImageView lockIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/lock.png")));
        StackPane lockIconPane = new StackPane();
        if (!lockIcon.getImage().isError()) {
            lockIcon.setFitHeight(16);
            lockIcon.setFitWidth(16);
            lockIconPane.getChildren().add(lockIcon);
        } else {
            Label lockLabel = new Label("🔒");
            lockIconPane.getChildren().add(lockLabel);
        }
        lockIconPane.setPadding(new Insets(0, 10, 0, 15));
        
        // Password field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(40);
        passwordField.setPrefWidth(240);
        passwordField.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background-radius: 0;" +
            "-fx-border-color: transparent;" +
            "-fx-border-width: 0;" +
            "-fx-padding: 0 30px 0 0;" +
            "-fx-font-size: 14px;"
        );
        
        // Visible text field (for showing password)
        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText("Password");
        visiblePasswordField.setPrefHeight(40);
        visiblePasswordField.setPrefWidth(240);
        visiblePasswordField.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background-radius: 0;" +
            "-fx-border-color: transparent;" +
            "-fx-border-width: 0;" +
            "-fx-padding: 0 30px 0 0;" +
            "-fx-font-size: 14px;"
        );
        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);
        
        // Eye icon button
        ImageView eyeIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/eye.png")));
        Button eyeButton = new Button();
        if (!eyeIcon.getImage().isError()) {
            eyeIcon.setFitWidth(20);
            eyeIcon.setFitHeight(20);
            eyeButton.setGraphic(eyeIcon);
        } else {
            eyeButton.setText("👁");
        }
        eyeButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 0 15px 0 0;"
        );
        
        // Toggle password visibility
        eyeButton.setOnAction(e -> {
            boolean isHidden = passwordField.isVisible();
            passwordField.setVisible(!isHidden);
            passwordField.setManaged(!isHidden);
            visiblePasswordField.setVisible(isHidden);
            visiblePasswordField.setManaged(isHidden);
            
            if (isHidden) {
                visiblePasswordField.setText(passwordField.getText());
            } else {
                passwordField.setText(visiblePasswordField.getText());
            }
        });
        
        // Create stack pane to overlay the fields
        StackPane fieldPane = new StackPane();
        fieldPane.getChildren().addAll(passwordField, visiblePasswordField);
        fieldPane.setAlignment(Pos.CENTER_LEFT);
        
        // Add components to the password box
        passwordBox.getChildren().addAll(lockIconPane, fieldPane, eyeButton);
        
        // Handle focus effect on the HBox container
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                passwordBox.setStyle(
                    "-fx-background-color: #f5f5f5;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-color: " + ANDOKS_RED + ";" +
                    "-fx-border-radius: 20;" +
                    "-fx-border-width: 2px;"
                );
            } else {
                passwordBox.setStyle(
                    "-fx-background-color: #f5f5f5;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-color: #e0e0e0;" +
                    "-fx-border-radius: 20;" +
                    "-fx-border-width: 1px;"
                );
            }
        });
        
        visiblePasswordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                passwordBox.setStyle(
                    "-fx-background-color: #f5f5f5;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-color: " + ANDOKS_RED + ";" +
                    "-fx-border-radius: 20;" +
                    "-fx-border-width: 2px;"
                );
            } else {
                passwordBox.setStyle(
                    "-fx-background-color: #f5f5f5;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-color: #e0e0e0;" +
                    "-fx-border-radius: 20;" +
                    "-fx-border-width: 1px;"
                );
            }
        });
        
        // Sync text while typing
        passwordField.textProperty().bindBidirectional(visiblePasswordField.textProperty());
        
        // Terms checkbox
        CheckBox termsCheckBox = new CheckBox("I agree to the Terms & Conditions");
        termsCheckBox.setStyle("-fx-text-fill: #757575;");
        
        // Sign up button
        Button signUpBtn = new Button("SIGN UP");
        signUpBtn.setPrefWidth(200);
        signUpBtn.setPrefHeight(40);
        signUpBtn.setStyle(
            "-fx-background-color: " + ANDOKS_RED + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 20;"
        );
        
        // Button hover effect
        signUpBtn.setOnMouseEntered(e -> 
            signUpBtn.setStyle(
                "-fx-background-color: " + ANDOKS_DARK_RED + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 20;"
            )
        );
        
        signUpBtn.setOnMouseExited(e -> 
            signUpBtn.setStyle(
                "-fx-background-color: " + ANDOKS_RED + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 20;"
            )
        );
        
        signUpBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            
            if (!termsCheckBox.isSelected()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please agree to the Terms & Conditions", ButtonType.OK);
                styleAlert(alert);
                alert.showAndWait();
                return;
            }
            
            if (!email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please use a valid Gmail address.", ButtonType.OK);
                styleAlert(alert);
                alert.showAndWait();
                return;
            }

            
            if (User.emailExists(email)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Email already in use! Try another one.", ButtonType.OK);
                styleAlert(alert);
                alert.showAndWait();
                return; // Stop signup process
            }
            
            // Validate user input
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "All fields are required!", ButtonType.OK);
                styleAlert(alert);
                alert.showAndWait();
                return;
            }
            
            // Show loading animation
            showLoading(signUpBtn);
            
            boolean isSignedUp = User.signUp(username, email, password);
            
            if (isSignedUp) {
                System.out.println("Sign Up successful!");
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Signed Up successfully!", ButtonType.OK);
                styleAlert(alert);
                alert.showAndWait();
                
                // Clear fields after successful signup
                usernameField.clear();
                emailField.clear();
                passwordField.clear();
                termsCheckBox.setSelected(false);
            } else {
                System.out.println("Sign Up failed.");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Sign Up failed. Try again!", ButtonType.OK);
                styleAlert(alert);
                alert.showAndWait();
            }
        });
        
        // Login text
        HBox loginContainer = new HBox();
        loginContainer.setAlignment(Pos.CENTER);
        loginContainer.setSpacing(5);
        
        Label alreadyAccountLabel = new Label("Already have an account?");
        alreadyAccountLabel.setStyle("-fx-text-fill: #757575;");
        
        Hyperlink loginLink = new Hyperlink("Log in");
        loginLink.setStyle("-fx-text-fill: " + ANDOKS_RED + ";");
        
        loginLink.setOnAction(e -> {
            // Close the current Signup window
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.close();
            
            // Open the Login window
            Stage loginStage = new Stage();
            Main loginWindow = new Main();
            try {
                loginWindow.start(loginStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        loginContainer.getChildren().addAll(alreadyAccountLabel, loginLink);
        
        // Add all elements to the form
        signupForm.getChildren().addAll(
            headerBox,
            usernameBox,
            emailBox,
            passwordBox,
            termsCheckBox,
            signUpBtn,
            loginContainer
        );
        
        return signupForm;
    }
    
    private HBox createWindowControls(Stage stage) {
        HBox controls = new HBox(10);
        controls.setPadding(new Insets(10));
        controls.setAlignment(Pos.TOP_RIGHT);
        
        Button minimizeBtn = new Button("—");
        minimizeBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #757575;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;"
        );
        minimizeBtn.setOnAction(e -> stage.setIconified(true));
        
        Button closeBtn = new Button("✕");
        closeBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + ANDOKS_RED + ";" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> stage.close());
        
        controls.getChildren().addAll(minimizeBtn, closeBtn);
        return controls;
    }
    
    private void showLoading(Button button) {
        String originalText = button.getText();
        button.setText("Processing...");
        button.setDisable(true);
        
        // Reset button after 2 seconds
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    javafx.application.Platform.runLater(() -> {
                        button.setText(originalText);
                        button.setDisable(false);
                    });
                }
            },
            2000
        );
    }
    
    private void styleAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
            "-fx-background-color: " + ANDOKS_WHITE + ";" +
            "-fx-border-color: " + ANDOKS_RED + ";" +
            "-fx-border-width: 2px;"
        );
        
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setStyle(
            "-fx-background-color: " + ANDOKS_RED + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20px;"
        );
    }
    
    private void applyFadeAnimation(Node... nodes) {
        for (Node node : nodes) {
            FadeTransition fade = new FadeTransition(Duration.seconds(1), node);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }
    }
    
    private void applyEntranceAnimations(Node leftPanel, Node rightPanel) {
        // Slide in from left for branding panel
        TranslateTransition slideLeft = new TranslateTransition(Duration.seconds(0.8), leftPanel);
        slideLeft.setFromX(-350);
        slideLeft.setToX(0);
        slideLeft.play();
        
        // Fade in for signup form
        FadeTransition fadeRight = new FadeTransition(Duration.seconds(1.2), rightPanel);
        fadeRight.setFromValue(0);
        fadeRight.setToValue(1);
        fadeRight.play();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}