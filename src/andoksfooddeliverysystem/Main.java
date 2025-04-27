package andoksfooddeliverysystem;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import static javafx.application.Application.launch;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javax.mail.MessagingException;

public class Main extends Application {
    private boolean darkMode = false;
    private Scene scene;
    private Label title;
    private int failedAttempts = 0;
    private boolean isCooldown = false;
    private long cooldownStartTime = 0;
    private final int COOLDOWN_SECONDS = 15;

    
    // Andok's color palette
    private final String ANDOKS_RED = "#D32F2F";
    private final String ANDOKS_YELLOW = "#FFD54F";
    private final String ANDOKS_WHITE = "#FFFFFF";
    private final String ANDOKS_DARK_RED = "#B71C1C";
    
    @Override
    public void start(Stage primaryStage) {
        showLogin(primaryStage);
    }

    public void showLogin(Stage stage) {
        // Create main layout
        BorderPane mainLayout = new BorderPane();
        
        // Left side for logo and branding
        VBox leftPanel = createBrandingPanel();
        
        // Right side for login form
        VBox rightPanel = createLoginFormPanel();
        
        mainLayout.setLeft(leftPanel);
        mainLayout.setCenter(rightPanel);
        
        scene = new Scene(mainLayout, 800, 500);
        scene.getStylesheets().add(Main.class.getResource("/styles/light-theme.css").toExternalForm());

        // Make window corners rounded with transparent background
        stage.initStyle(StageStyle.UNDECORATED);
        Rectangle clipRect = new Rectangle(800, 500);
        clipRect.setArcWidth(20);
        clipRect.setArcHeight(20);
        mainLayout.setClip(clipRect);
        
        // Add drop shadow to the entire window
        mainLayout.setEffect(new DropShadow(15, Color.gray(0.5, 0.5)));
        
        // Set background color
        mainLayout.setStyle("-fx-background-color: " + ANDOKS_WHITE + ";");
        
        // Add window control buttons (minimize, close)
        HBox windowControls = createWindowControls(stage);
        mainLayout.setTop(windowControls);
        
        stage.setScene(scene);
        stage.setTitle("Andok's Food Delivery");
        stage.show();
        
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
        ImageView logo = new ImageView(new Image(Main.class.getResourceAsStream("/icons/andoksLogo.png")));
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
    
    private VBox createLoginFormPanel() {
        VBox loginForm = new VBox(25);
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setPadding(new Insets(40));
        loginForm.setStyle("-fx-background-color: " + ANDOKS_WHITE + ";");
        
        // Welcome text
        title = new Label("Welcome Back!");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: " + ANDOKS_RED + ";");
        
        Label subtitle = new Label("Login to your account");
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
        ImageView userIcon = new ImageView(new Image(Main.class.getResourceAsStream("/icons/user.png")));
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
        
//        // Password fields (hidden and visible)
//        PasswordField passwordField = new PasswordField();
//        passwordField.setPromptText("Password");
//        styleTextField(passwordField);
//        
//        TextField visiblePasswordField = new TextField();
//        visiblePasswordField.setPromptText("Password");
//        styleTextField(visiblePasswordField);
//        visiblePasswordField.setManaged(false);
//        visiblePasswordField.setVisible(false);
//        
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
        ImageView lockIcon = new ImageView(new Image(Main.class.getResourceAsStream("/icons/lock.png")));
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
        ImageView eyeIcon = new ImageView(new Image(Main.class.getResourceAsStream("/icons/eye.png")));
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
//        // Remember me and forgot password
//        CheckBox rememberMe = new CheckBox("Remember me");
//        rememberMe.setStyle("-fx-text-fill: #757575;");
//        
//        Hyperlink forgotPassword = new Hyperlink("Forgot password?");
//        forgotPassword.setStyle("-fx-text-fill: " + ANDOKS_RED + ";");
//        
//        HBox optionsRow = new HBox(rememberMe, forgotPassword);
//        optionsRow.setAlignment(Pos.CENTER);
//        optionsRow.setSpacing(50);
        
        // Login button
        Button loginBtn = new Button("LOG IN");
        loginBtn.setPrefWidth(200);
        loginBtn.setPrefHeight(40);
        loginBtn.setStyle(
            "-fx-background-color: " + ANDOKS_RED + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 20;"
        );
          Label cooldownLabel = new Label();
        cooldownLabel.setStyle(
            "-fx-text-fill: red;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 5 0 0 0;"
        );
        cooldownLabel.setVisible(false);
        
        // Button hover effect
        loginBtn.setOnMouseEntered(e -> 
            loginBtn.setStyle(
                "-fx-background-color: " + ANDOKS_DARK_RED + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 20;"
            )
        );
        
        loginBtn.setOnMouseExited(e -> 
            loginBtn.setStyle(
                "-fx-background-color: " + ANDOKS_RED + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 20;"
            )
        );
        
      loginBtn.setOnAction(e -> {
        if (isCooldown) {
            long secondsPassed = (System.currentTimeMillis() - cooldownStartTime) / 1000;
            long secondsLeft = COOLDOWN_SECONDS - secondsPassed;

            // Don't allow login if time is still left
            if (secondsLeft > 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Too many failed attempts. Please wait " + secondsLeft + " more seconds.");
                alert.showAndWait();
                return;
            } else {
                // If time is up but cooldown flag is still true (e.g. app just didn't update it yet)
                isCooldown = false;
                loginBtn.setDisable(false);
                cooldownLabel.setVisible(false);
                failedAttempts = 0;
            }
        }


        try {
            String username = usernameField.getText();
            String password = passwordField.getText();

            Stage currentStage = (Stage) loginBtn.getScene().getWindow();

            showLoading(loginBtn);

            User loggedInUser = User.login(username, password, currentStage);

            if (loggedInUser != null) {
                System.out.println("Login successful! User role: " + loggedInUser.getRole());
                failedAttempts = 0; // Reset after successful login
            } else {
                failedAttempts++;
                showErrorAnimation(loginBtn);

                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid username or password!", ButtonType.OK);
                alert.getDialogPane().setStyle("-fx-background-color: " + ANDOKS_WHITE + ";");
                ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setStyle(
                    "-fx-background-color: " + ANDOKS_RED + ";" +
                    "-fx-text-fill: white;" +
                    "-fx-background-radius: 20;"
                );
                alert.showAndWait();

                // Check if max attempts reached
               if (failedAttempts >= 3) {
                isCooldown = true;
                  cooldownStartTime = System.currentTimeMillis(); // <— Store the start time
                loginBtn.setDisable(true);
                cooldownLabel.setVisible(true);

                final int[] secondsLeft = {15}; // cooldown duration
                cooldownLabel.setText("Try again in " + secondsLeft[0] + " seconds...");

                Timeline cooldownTimer = new Timeline(
                    new KeyFrame(Duration.seconds(1), ev -> {
                        secondsLeft[0]--;
                        if (secondsLeft[0] > 0) {
                            cooldownLabel.setText("Try again in " + secondsLeft[0] + " seconds...");
                        } else {
                            isCooldown = false;
                            loginBtn.setDisable(false);
                            cooldownLabel.setVisible(false);
                            failedAttempts = 0;
                        }
                    })
                );
                cooldownTimer.setCycleCount(15);
                cooldownTimer.play();
            }
            }
        } catch (MessagingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    });

       

        
        
        // Sign up text
        HBox signUpContainer = new HBox();
        signUpContainer.setAlignment(Pos.CENTER);
        signUpContainer.setSpacing(5);
        
        Label noAccountLabel = new Label("Don't have an account?");
        noAccountLabel.setStyle("-fx-text-fill: #757575;");
        
        Hyperlink signUpLink = new Hyperlink("Sign up");
        signUpLink.setStyle("-fx-text-fill: " + ANDOKS_RED + ";");
        
        signUpLink.setOnAction(e -> {
            Stage currentStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            currentStage.close();  // Close current window
            
            // Open SignUp window
            Signup signUpWindow = new Signup();
            Stage signUpStage = new Stage();
            try {
                signUpWindow.start(signUpStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        signUpContainer.getChildren().addAll(noAccountLabel, signUpLink);
        
       // Add all elements to the form 
        loginForm.getChildren().addAll(
            headerBox,
            usernameBox,
            passwordBox,
            
            loginBtn,
            cooldownLabel,
            signUpContainer
        );
        
        return loginForm;
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
    
    private TextField createStyledTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        styleTextField(textField);
        return textField;
    }
    
    private void styleTextField(TextInputControl textField) {
        textField.setPrefHeight(40);
        textField.setPrefWidth(300);
        textField.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 5 15 5 45;" +
            "-fx-font-size: 14px;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 1px;"
        );
        
        // Focus style
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                textField.setStyle(
                    "-fx-background-color: #f5f5f5;" +
                    "-fx-background-radius: 20;" +
                    "-fx-padding: 5 15 5 45;" +
                    "-fx-font-size: 14px;" +
                    "-fx-border-color: " + ANDOKS_RED + ";" +
                    "-fx-border-radius: 20;" +
                    "-fx-border-width: 2px;"
                );
            } else {
                textField.setStyle(
                    "-fx-background-color: #f5f5f5;" +
                    "-fx-background-radius: 20;" +
                    "-fx-padding: 5 15 5 45;" +
                    "-fx-font-size: 14px;" +
                    "-fx-border-color: #e0e0e0;" +
                    "-fx-border-radius: 20;" +
                    "-fx-border-width: 1px;"
                );
            }
        });
    }
    
    private void showLoading(Button button) {
        String originalText = button.getText();
        button.setText("Logging in...");
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
    
    private void showErrorAnimation(Button button) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), button);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
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
        
        // Fade in for login form
        FadeTransition fadeRight = new FadeTransition(Duration.seconds(1.2), rightPanel);
        fadeRight.setFromValue(0);
        fadeRight.setToValue(1);
        fadeRight.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}