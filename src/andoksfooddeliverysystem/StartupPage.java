package andoksfooddeliverysystem;

import java.io.File;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.HostServices;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.Circle;

public class StartupPage extends Application {
    
    // Constants for consistent styling
    private static final String PRIMARY_COLOR = "#e74c3c";
    private static final String SECONDARY_COLOR = "#f39c12";
    private static final String DARK_COLOR = "#2c3e50";
    private static final String LIGHT_COLOR = "#ecf0f1";
    private static final String ACCENT_COLOR = "#e67e22";
    
    // For tracking scroll position and animations
    private double lastScrollPosition = 0;
    
    @Override
    public void start(Stage primaryStage) {
        // Main content container
        VBox content = new VBox();
        content.setSpacing(0);
        content.setPadding(new Insets(0));
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle("-fx-background-color: " + LIGHT_COLOR + ";");

        // Section 1: Hero Banner with animated entrance
        StackPane heroSection = createHeroSection();
        
        // Section 2: About Andoks section with parallax effect
        StackPane aboutSection = createAboutSection();
        
        // Section 3: Menu Showcase with carousel animation
        StackPane menuShowcaseSection = createMenuShowcaseSection();
        
        // Section 4: Customer Testimonials
        VBox testimonialsSection = createTestimonialsSection();
        
        // Section 5: Call to Action
        StackPane ctaSection = createCTASection();
        
        // Section 6: Footer
        //HBox footerSection = createFooterSection();

        // Add all sections to main content
        content.getChildren().addAll(heroSection, aboutSection, menuShowcaseSection, 
                                    testimonialsSection, ctaSection);

        // Create smooth ScrollPane
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("edge-to-edge");
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        // Add scroll listener for animations
        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            handleScrollAnimations(content, scrollPane, newVal.doubleValue());
        });

        // Create scene with custom CSS
        Scene scene = new Scene(scrollPane, 1530, 800);
        scene.getStylesheets().add(getClass().getResource("/styles/startup.css").toExternalForm());
        
        // Configure primary stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Andok's Food Delivery");
        primaryStage.setMaximized(true);
        primaryStage.show();
        
        // Initialize scroll position for animations
        Platform.runLater(() -> {
            scrollPane.setVvalue(0);
            initializeAnimations(content);
        });
    }
    
    private StackPane createHeroSection() {
        StackPane heroSection = new StackPane();
        heroSection.setPrefSize(1530, 800);
        heroSection.getStyleClass().add("hero-section");
        
        // Load the video
        File videoFile = new File("C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/icons/Andoksbg.mp4");
        Media media = new Media(videoFile.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop the video
        mediaPlayer.setAutoPlay(true); // Start automatically

        // Create the video view
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(1530); // or whatever size you want
        mediaView.setPreserveRatio(true);

        // Optional: add drop shadow
        mediaView.setEffect(new DropShadow(20, Color.color(0, 0, 0, 0.5)));
        
        // Semi-transparent overlay for better text visibility
        Rectangle overlay = new Rectangle(1530, 800);
        overlay.setFill(Color.rgb(0, 0, 0, 0.3));
        
        // Hero content container
        VBox heroContent = new VBox(20);
        heroContent.setAlignment(Pos.CENTER);
        heroContent.setMaxWidth(800);
        
        // Andoks logo
        ImageView logo = new ImageView(new Image("file:/C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/icons/andoks_logo.png"));
        logo.setFitWidth(300);
        logo.setPreserveRatio(true);
        logo.setEffect(new DropShadow(20, Color.BLACK));
        
        // Tagline
        Text tagline = new Text("Masarap. Masarap.");
        tagline.setFill(Color.WHITE);
        tagline.setFont(Font.font("Montserrat", FontWeight.LIGHT, 32));
        tagline.setTextAlignment(TextAlignment.CENTER);
        tagline.setEffect(new DropShadow(10, Color.BLACK));
        
        // Description
        Text description = new Text("Experience the authentic taste of Filipino cuisine delivered right to your doorstep");
        description.setFill(Color.WHITE);
        description.setFont(Font.font("Montserrat", 18));
        description.setTextAlignment(TextAlignment.CENTER);
        description.setWrappingWidth(600);
        description.setEffect(new DropShadow(5, Color.BLACK));
        
        // Order Now button with hover effect
        Button orderNowButton = createAnimatedButton("ORDER NOW", PRIMARY_COLOR, DARK_COLOR);
        orderNowButton.setOnAction(e -> {
            Stage loginStage = new Stage();
            new Main().showLogin(loginStage);
        });
        
        // Add all elements to hero content
        heroContent.getChildren().addAll(logo, tagline, description, orderNowButton);
        
        // Add all layers to hero section
        heroSection.getChildren().addAll(mediaView, overlay, heroContent);
        
        return heroSection;
    }
    
    private StackPane createAboutSection() {
        StackPane aboutSection = new StackPane();
        aboutSection.setPrefSize(1530, 600);
        aboutSection.setStyle("-fx-background-color: " + LIGHT_COLOR + ";");
        
        // Create content with two columns
        HBox content = new HBox(50);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50));
        content.setMaxWidth(1200);
        
        // Left column: Image
        ImageView aboutImage = new ImageView(new Image("file:/C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/icons/andoksRestaurant.png"));
        aboutImage.setFitWidth(500);
        aboutImage.setPreserveRatio(true);
        aboutImage.setEffect(new DropShadow(20, Color.color(0, 0, 0, 0.5)));
        
        // Create a clip with rounded corners
        Rectangle clip = new Rectangle(500, 500);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        aboutImage.setClip(clip);
        
        // Right column: Text content
        VBox textContent = new VBox(20);
        textContent.setAlignment(Pos.CENTER_LEFT);
        
        Text aboutTitle = new Text("ABOUT ANDOK'S");
        aboutTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 36));
        aboutTitle.setFill(Color.web(PRIMARY_COLOR));
        
        Text aboutSubtitle = new Text("Serving Authentic Filipino Favorites Since 1985");
        aboutSubtitle.setFont(Font.font("Montserrat", FontWeight.MEDIUM, 18));
        aboutSubtitle.setFill(Color.web(DARK_COLOR));
        
        Text aboutText = new Text("Andok's has been a beloved name in Filipino dining for over three decades, renowned for our signature liempo, litson manok, and other Filipino favorites. What started as a small rotisserie stall has grown into a nationwide sensation, bringing quality, flavor, and value to every Filipino table.\n\nOur commitment to quality ingredients and authentic recipes has made us a trusted name in Filipino cuisine.");
        aboutText.setFont(Font.font("Montserrat", 16));
        aboutText.setFill(Color.web(DARK_COLOR));
        aboutText.setWrappingWidth(500);
        
        Button learnMoreButton = new Button("OUR STORY");
        learnMoreButton.getStyleClass().add("outline-button");
        learnMoreButton.setStyle("-fx-font-family: 'Montserrat'; -fx-font-size: 16px;");
        // Add click event to open Facebook page
        learnMoreButton.setOnAction(event -> {
            try {
                // Get HostServices from the Application class
                HostServices hostServices = getHostServices();
                hostServices.showDocument("https://www.facebook.com/pusongandoks/");
            } catch (Exception e) {
                System.err.println("Failed to open Facebook link: " + e.getMessage());
                // Fallback: Show an error alert
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Could not open Facebook");
                alert.setContentText("Please visit: https://www.facebook.com/pusongandoks/");
                alert.showAndWait();
            }
        });
        
        textContent.getChildren().addAll(aboutTitle, aboutSubtitle, aboutText, learnMoreButton);
        
        // Add columns to content
        content.getChildren().addAll(aboutImage, textContent);
        
        // Add content to section
        aboutSection.getChildren().add(content);
        
        return aboutSection;
    }
    
    private StackPane createMenuShowcaseSection() {
        StackPane menuSection = new StackPane();
        menuSection.setPrefSize(1530, 800);
        menuSection.getStyleClass().add("menu-section");
        
       // Path to your mp4 file
        String videoPath = "file:/C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/icons/Andoksbg2.mp4";

        // Load media and player
        Media bgMedia = new Media(videoPath);
        MediaPlayer mediaPlayer = new MediaPlayer(bgMedia);

        // Auto-play, loop, and mute (if you don't want sound)
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setMute(true);

        // Set up media view like an image view
        MediaView bgVideo = new MediaView(mediaPlayer);
        bgVideo.setFitWidth(1530);
        bgVideo.setFitHeight(800);
        bgVideo.setPreserveRatio(false);
        bgVideo.setOpacity(0.8); // same opacity as before
        // Background overlay with gradient
        Rectangle overlay = new Rectangle(1530, 800);
        Stop[] stops = new Stop[] {
            new Stop(0, Color.rgb(44, 62, 80, 0.8)),
            new Stop(1, Color.rgb(231, 76, 60, 0.8))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        overlay.setFill(gradient);
        
        // Content container
        VBox content = new VBox(40);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50));
        content.setMaxWidth(1200);
        
        // Section title
        Text menuTitle = new Text("OUR MENU FAVORITES");
        menuTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 42));
        menuTitle.setFill(Color.WHITE);
        menuTitle.setEffect(new DropShadow(10, Color.BLACK));
        
        // Menu carousel
        HBox carouselContainer = new HBox(30);
        carouselContainer.setAlignment(Pos.CENTER);
        
        // Load menu items
        List<MenuItem> menuItems = loadMenuItems();
        
        // Create cards for visible items (first 3)
        for (int i = 0; i < Math.min(3, menuItems.size()); i++) {
            MenuItem item = menuItems.get(i);
            StackPane card = createEnhancedMenuCard(item.name(), item.price(), item.imagePath());
            carouselContainer.getChildren().add(card);
        }
        
        // Navigation buttons
        Button prevButton = createCircleButton("<", "-fx-font-size: 24px;");
        Button nextButton = createCircleButton(">", "-fx-font-size: 24px;");
        
        // Setup carousel navigation
        AtomicInteger currentIndex = new AtomicInteger(0);
        
        nextButton.setOnAction(e -> {
            carouselContainer.getChildren().remove(0);
            currentIndex.set((currentIndex.get() + 1) % menuItems.size());
            int newIndex = (currentIndex.get() + 2) % menuItems.size();
            MenuItem newItem = menuItems.get(newIndex);
            StackPane newCard = createEnhancedMenuCard(newItem.name(), newItem.price(), newItem.imagePath());
            
            // Animate new card entrance
            newCard.setScaleX(0.8);
            newCard.setScaleY(0.8);
            newCard.setOpacity(0);
            
            carouselContainer.getChildren().add(newCard);
            
            ScaleTransition st = new ScaleTransition(Duration.millis(300), newCard);
            st.setToX(1);
            st.setToY(1);
            
            FadeTransition ft = new FadeTransition(Duration.millis(300), newCard);
            ft.setToValue(1);
            
            ParallelTransition pt = new ParallelTransition(st, ft);
            pt.play();
        });
        
        prevButton.setOnAction(e -> {
            carouselContainer.getChildren().remove(carouselContainer.getChildren().size() - 1);
            currentIndex.set((currentIndex.get() - 1 + menuItems.size()) % menuItems.size());
            int newIndex = (currentIndex.get()) % menuItems.size();
            MenuItem newItem = menuItems.get(newIndex);
            StackPane newCard = createEnhancedMenuCard(newItem.name(), newItem.price(), newItem.imagePath());
            
            // Animate new card entrance
            newCard.setScaleX(0.8);
            newCard.setScaleY(0.8);
            newCard.setOpacity(0);
            
            carouselContainer.getChildren().add(0, newCard);
            
            ScaleTransition st = new ScaleTransition(Duration.millis(300), newCard);
            st.setToX(1);
            st.setToY(1);
            
            FadeTransition ft = new FadeTransition(Duration.millis(300), newCard);
            ft.setToValue(1);
            
            ParallelTransition pt = new ParallelTransition(st, ft);
            pt.play();
        });
        
        // Navigation container
        HBox navigationButtons = new HBox(20);
        navigationButtons.setAlignment(Pos.CENTER);
        navigationButtons.getChildren().addAll(prevButton, nextButton);
        
        // View All Menu button
        Button viewAllButton = createAnimatedButton("VIEW FULL MENU", LIGHT_COLOR, PRIMARY_COLOR);
        viewAllButton.setStyle(viewAllButton.getStyle() + "-fx-text-fill: " + PRIMARY_COLOR + ";");
         viewAllButton.setOnAction(e -> {
            Stage loginStage = new Stage();
            new Main().showLogin(loginStage);
        });
        // Add all elements to content
        content.getChildren().addAll(menuTitle, carouselContainer, navigationButtons, viewAllButton);
        
        // Add all layers to menu section
        menuSection.getChildren().addAll(bgVideo, overlay, content);
        
        return menuSection;
    }
    
    private VBox createTestimonialsSection() {
    VBox testimonialSection = new VBox(40);
    testimonialSection.setPadding(new Insets(80, 0, 80, 0));
    testimonialSection.setAlignment(Pos.CENTER);
    testimonialSection.setStyle("-fx-background-color: " + LIGHT_COLOR + ";");

    // Section title
    Text sectionTitle = new Text("WHAT OUR CUSTOMERS SAY");
    sectionTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 36));
    sectionTitle.setFill(Color.web(PRIMARY_COLOR));

    // Testimonials container
    HBox testimonials = new HBox(30);
    testimonials.setAlignment(Pos.CENTER);
    testimonials.setPadding(new Insets(20, 0, 50, 0));
    testimonials.setMaxWidth(1200);

    // Fetch data from DB
    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement("SELECT food_review FROM ratings WHERE food_rating = 5 AND food_review IS NOT NULL AND food_review != '' LIMIT 3");
         ResultSet rs = stmt.executeQuery()) {

        // Dummy names and photos for now (you can replace these with actual data if you have them)
        String[] names = {"Maria Santos", "Juan Dela Cruz", "Ana Garcia"};
        String[] images = {
            "file:/C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/icons/customer1.jpg",
            "file:/C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/icons/customer2.jpg",
            "file:/C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/icons/customer3.jpg"
        };

        int index = 0;
        while (rs.next() && index < names.length) {
            String review = rs.getString("food_review");
            testimonials.getChildren().add(
                createTestimonialCard(names[index], review, images[index])
            );
            index++;
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    testimonialSection.getChildren().addAll(sectionTitle, testimonials);
    return testimonialSection;
}

    private StackPane createCTASection() {
        StackPane ctaSection = new StackPane();
        ctaSection.setPrefSize(1530, 500);
        
        Image ctaBg = null;
try {
    ctaBg = new Image("file:///C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/icons/cta_background.png");
    System.out.println("Image loaded successfully.");
} catch (Exception e) {
    System.out.println("Error loading image:");
    e.printStackTrace();
}
ImageView bgView = new ImageView(ctaBg);
        bgView.setFitWidth(1530);
        bgView.setFitHeight(800);
        bgView.setPreserveRatio(false);
        
//        // Overlay
//        Rectangle overlay = new Rectangle(1530, 500);
//        overlay.setFill(Color.rgb(0, 0, 0, 0.7));
//        
        // Content container
        VBox content = new VBox(30);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(800);
        
        // Text elements
        Text ctaTitle = new Text("CRAVING FOR ANDOK'S?");
        ctaTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 42));
        ctaTitle.setFill(Color.WHITE);
        
        Text ctaSubtitle = new Text("Order now and get your Filipino favorites delivered in minutes");
        ctaSubtitle.setFont(Font.font("Montserrat", 20));
        ctaSubtitle.setFill(Color.WHITE);
        ctaSubtitle.setTextAlignment(TextAlignment.CENTER);
        
        // Action buttons
        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);
        
        Button orderButton = createAnimatedButton("ORDER NOW", PRIMARY_COLOR, DARK_COLOR);
        orderButton.setOnAction(e -> {
            Stage loginStage = new Stage();
            new Main().showLogin(loginStage);
        });
     
        buttons.getChildren().addAll(orderButton);
        
        // Add all elements to content
        content.getChildren().addAll(ctaTitle, ctaSubtitle, buttons);
        
        // Add all layers to CTA section
        ctaSection.getChildren().addAll(bgView, content);
        
        return ctaSection;
    }
    
    private HBox createFooterSection() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(50, 0, 30, 0));
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-background-color: " + DARK_COLOR + ";");
        
        // Footer content container
        HBox content = new HBox(50);
        content.setMaxWidth(1200);
        content.setAlignment(Pos.CENTER);
        
        // Logo and company info
        VBox companyInfo = new VBox(15);
        companyInfo.setPrefWidth(300);
        
        ImageView footerLogo = new ImageView(new Image("file:/C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/icons/andoks_logo_small.png"));
        footerLogo.setFitWidth(150);
        footerLogo.setPreserveRatio(true);
        
        Text address = new Text("J P Laurel Street, Nasugbu\nBatangas, Philippines");
        address.setFill(Color.WHITE);
        address.setFont(Font.font("Montserrat", 14));
        
        companyInfo.getChildren().addAll(footerLogo, address);
        
        // Quick links
        VBox quickLinks = new VBox(10);
        quickLinks.setPrefWidth(200);
        
        Text linksTitle = new Text("QUICK LINKS");
        linksTitle.setFill(Color.web(PRIMARY_COLOR));
        linksTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 16));
        
        VBox links = new VBox(8);
        for (String linkText : List.of("Home", "Menu", "Locations", "About Us", "Careers")) {
            Hyperlink link = new Hyperlink(linkText);
            link.setTextFill(Color.WHITE);
            link.setFont(Font.font("Montserrat", 14));
            links.getChildren().add(link);
        }
        
        quickLinks.getChildren().addAll(linksTitle, links);
        
        // Contact
        VBox contact = new VBox(10);
        contact.setPrefWidth(300);
        
        Text contactTitle = new Text("CONTACT US");
        contactTitle.setFill(Color.web(PRIMARY_COLOR));
        contactTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 16));
        
        VBox contactDetails = new VBox(8);
        Text phone = new Text("Phone: (02) 8888-ANDOKS");
        phone.setFill(Color.WHITE);
        phone.setFont(Font.font("Montserrat", 14));
        
        Text email = new Text("Email: andoks@gmail.com");
        email.setFill(Color.WHITE);
        email.setFont(Font.font("Montserrat", 14));
        
        contactDetails.getChildren().addAll(phone, email);
        
        // Social media
        HBox socialMedia = new HBox(15);
        socialMedia.setPadding(new Insets(10, 0, 0, 0));
        
        for (String platform : List.of("facebook", "instagram", "twitter")) {
            ImageView icon = new ImageView(new Image("file:/C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/icons/" + platform + ".png"));
            icon.setFitWidth(24);
            icon.setFitHeight(24);
            icon.setPreserveRatio(true);
            socialMedia.getChildren().add(icon);
        }
        
        contact.getChildren().addAll(contactTitle, contactDetails, socialMedia);
        
        // Newsletter subscription
        VBox newsletter = new VBox(10);
        newsletter.setPrefWidth(300);
        
        Text newsletterTitle = new Text("SUBSCRIBE");
        newsletterTitle.setFill(Color.web(PRIMARY_COLOR));
        //newsletterTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 16));
        
        Text newsletterDesc = new Text("Get updates on promotions and new menu items");
        newsletterDesc.setFill(Color.WHITE);
        newsletterDesc.setFont(Font.font("Montserrat", 14));
        newsletterDesc.setWrappingWidth(280);
        
        HBox subscribeBox = new HBox(0);
        TextField emailField = new TextField();
        emailField.setPromptText("Your email address");
        emailField.setPrefWidth(200);
        
        Button subscribeBtn = new Button("SEND");
        subscribeBtn.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white;");
        
        subscribeBox.getChildren().addAll(emailField, subscribeBtn);
        
        newsletter.getChildren().addAll(newsletterTitle, newsletterDesc, subscribeBox);
        
        // Add all sections to content
        content.getChildren().addAll(companyInfo, quickLinks, contact, newsletter);
        
        // Copyright text
        Text copyright = new Text("© 2025 Andok's Food Delivery. All rights reserved.");
        copyright.setFill(Color.WHITE);
        copyright.setFont(Font.font("Montserrat", 12));
        
        // Main footer layout
        VBox footerLayout = new VBox(30);
        footerLayout.setAlignment(Pos.CENTER);
        footerLayout.getChildren().addAll(content, new Separator(), copyright);
        
        footer.getChildren().add(footerLayout);
        
        return footer;
    }
    
    // Helper methods
    private Button createAnimatedButton(String text, String bgColor, String hoverColor) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: " + bgColor + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-family: 'Montserrat'; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-padding: 12px 30px; " +
            "-fx-background-radius: 30px;"
        );
        
        // Hover effects
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: " + hoverColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-family: 'Montserrat'; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 16px; " +
                "-fx-padding: 12px 30px; " +
                "-fx-background-radius: 30px; " +
                "-fx-cursor: hand;"
            );
            
            ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: " + bgColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-family: 'Montserrat'; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 16px; " +
                "-fx-padding: 12px 30px; " +
                "-fx-background-radius: 30px;"
            );
            
            ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
        
        return button;
    }
    
    private Button createCircleButton(String text, String additionalStyle) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: " + PRIMARY_COLOR + "; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 50%; " +
            "-fx-min-width: 50px; " +
            "-fx-min-height: 50px; " +
            "-fx-padding: 0; " +
            additionalStyle
        );
        
        // Hover effect
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: " + SECONDARY_COLOR + "; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 50%; " +
                "-fx-min-width: 50px; " +
                "-fx-min-height: 50px; " +
                "-fx-padding: 0; " +
                "-fx-cursor: hand; " +
                additionalStyle
            );
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: " + PRIMARY_COLOR + "; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 50%; " +
                "-fx-min-width: 50px; " +
                "-fx-min-height: 50px; " +
                "-fx-padding: 0; " +
                additionalStyle
            );
        });
        
        return button;
    }
    
    private StackPane createEnhancedMenuCard(String name, double price, String imagePath) {
        if (imagePath == null) {
            System.err.println("Image path is null!");
            return new StackPane();
        }
        
        // Card container
        StackPane card = new StackPane();
        card.setPrefSize(320, 450);
        card.setMaxSize(320, 450);
        card.getStyleClass().add("menu-card");
        card.setEffect(new DropShadow(20, Color.color(0, 0, 0, 0.3)));
        
        // Background with rounded corners
        Rectangle background = new Rectangle(320, 450);
        background.setArcWidth(20);
        background.setArcHeight(20);
        background.setFill(Color.WHITE);
        
        // Clip for image
        Rectangle imageClip = new Rectangle(320, 250);
        imageClip.setArcWidth(20);
        imageClip.setArcHeight(20);
        
        // Load image
        try {
            Image image = new Image("file:" + imagePath, 320, 250, true, true);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(320);
            imageView.setFitHeight(250);
            imageView.setPreserveRatio(true);
            imageView.setClip(imageClip);
            
           
            StackPane imageContainer = new StackPane(imageView);
     
            imageContainer.setTranslateY(-100);
            
            // Text container
            VBox textContainer = new VBox(15);
            textContainer.setAlignment(Pos.CENTER);
            textContainer.setPadding(new Insets(260, 20, 30, 20));
            textContainer.setMaxWidth(320);
            
            // Menu item name
            Label nameLabel = new Label(name);
            nameLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, 22));
            nameLabel.setStyle("-fx-text-fill: #000000;"); // Force black via inline style

            nameLabel.setWrapText(true);
            nameLabel.setAlignment(Pos.CENTER);
            nameLabel.setTextAlignment(TextAlignment.CENTER);
            
            // Price with stylized container
            StackPane priceContainer = new StackPane();
            Rectangle priceBg = new Rectangle(100, 36);
            priceBg.setArcWidth(18);
            priceBg.setArcHeight(18);
            priceBg.setFill(Color.web(PRIMARY_COLOR));
            
            Label priceLabel = new Label("₱" + String.format("%.2f", price));
            priceLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, 18));
            priceLabel.setTextFill(Color.WHITE);
            
            priceContainer.getChildren().addAll(priceBg, priceLabel);
            
            // Add to cart button
            Button addToCartBtn = new Button("ADD TO CART");
            addToCartBtn.getStyleClass().add("cart-button");
            addToCartBtn.setStyle(
                "-fx-background-color: " + DARK_COLOR + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-family: 'Montserrat'; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 10px 15px; " +
                "-fx-background-radius: 20px;"
            );
            
            // Hover animation for button
            addToCartBtn.setOnMouseEntered(e -> {
                addToCartBtn.setStyle(
                    "-fx-background-color: " + PRIMARY_COLOR + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-family: 'Montserrat'; " +
                    "-fx-font-size: 14px; " +
                    "-fx-padding: 10px 15px; " +
                    "-fx-background-radius: 20px; " +
                    "-fx-cursor: hand;"
                );
            });
            
            addToCartBtn.setOnMouseExited(e -> {
                addToCartBtn.setStyle(
                    "-fx-background-color: " + DARK_COLOR + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-family: 'Montserrat'; " +
                    "-fx-font-size: 14px; " +
                    "-fx-padding: 10px 15px; " +
                    "-fx-background-radius: 20px;"
                );
            });
            
             addToCartBtn.setOnAction(e -> {
            Stage loginStage = new Stage();
            new Main().showLogin(loginStage);
        });
            
            textContainer.getChildren().addAll(nameLabel, priceContainer, addToCartBtn);
            
            // Assemble the card
            card.getChildren().addAll(background, textContainer, imageContainer);
            
            // Set data for carousel identification
            card.setUserData(new MenuItem(name, price, imagePath));
            
            // Add hover effect for the entire card
            card.setOnMouseEntered(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
                st.setToX(1.05);
                st.setToY(1.05);
                st.play();
            });
            
            card.setOnMouseExited(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
                st.setToX(1.0);
                st.setToY(1.0);
                st.play();
            });
            
            return card;
        } catch (Exception e) {
            System.err.println("Failed to load image: " + imagePath + " - " + e.getMessage());
            return new StackPane();
        }
    }
    
    private VBox createTestimonialCard(String name, String testimonial, String imagePath) {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(350);
        card.setMinHeight(400);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15px; " +
            "-fx-border-radius: 15px;"
        );
        card.setEffect(new DropShadow(10, Color.color(0, 0, 0, 0.1)));
        
        // Quote image
        ImageView quoteIcon = new ImageView(new Image("file:/C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/icons/quote.png"));
        quoteIcon.setFitWidth(40);
        quoteIcon.setPreserveRatio(true);
        
        // Customer testimonial
        Text testimonialText = new Text(testimonial);
        testimonialText.setFont(Font.font("Montserrat", FontWeight.NORMAL, 16));
        testimonialText.setFill(Color.web(DARK_COLOR));
        testimonialText.setWrappingWidth(290);
        testimonialText.setTextAlignment(TextAlignment.CENTER);
        
        // Customer info container
        HBox customerInfo = new HBox(15);
        customerInfo.setAlignment(Pos.CENTER);
        
        // Customer image with circle clip
        ImageView customerImage = new ImageView(new Image(imagePath));
        customerImage.setFitWidth(60);
        customerImage.setFitHeight(60);
        
        // Create circular clip
        Circle clip = new Circle(30);
        clip.setCenterX(30);
        clip.setCenterY(30);
        customerImage.setClip(clip);
        
        // Customer name
        Text customerName = new Text(name);
        customerName.setFont(Font.font("Montserrat", FontWeight.BOLD, 16));
        customerName.setFill(Color.web(PRIMARY_COLOR));
        
        customerInfo.getChildren().addAll(customerImage, customerName);
        
        // Add all elements to card
        card.getChildren().addAll(quoteIcon, testimonialText, customerInfo);
        
        return card;
    }
    
    // Animation handling for scroll effects
    private void handleScrollAnimations(VBox content, ScrollPane scrollPane, double scrollValue) {
        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        double contentHeight = content.getHeight();
        double scrollableAmount = contentHeight - viewportHeight;
        
        // Calculate actual scroll position in pixels
        double scrollPosition = scrollValue * scrollableAmount;
        double scrollDelta = scrollPosition - lastScrollPosition;
        lastScrollPosition = scrollPosition;
        
        // For each section, check if it's in view and animate accordingly
        for (int i = 0; i < content.getChildren().size(); i++) {
            Node section = content.getChildren().get(i);
            double sectionTop = section.getBoundsInParent().getMinY();
            double sectionHeight = section.getBoundsInParent().getHeight();
            
            // Check if section is visible in viewport
            double viewportTop = scrollPosition;
            double viewportBottom = viewportTop + viewportHeight;
            
            if (sectionTop + 100 < viewportBottom && sectionTop + sectionHeight > viewportTop) {
                // Section is visible, perform entrance animation if not already animated
                if (section.getOpacity() < 1) {
                    animateSectionEntrance(section, i);
                }
                
                // Parallax effect for backgrounds (for specific sections)
                if (section instanceof StackPane) {
                    applyParallaxEffect((StackPane) section, scrollDelta);
                }
            }
        }
    }
    
    private void applyParallaxEffect(StackPane section, double scrollDelta) {
        // Only apply to sections with background images
        if (section.getChildren().size() > 0 && section.getChildren().get(0) instanceof ImageView) {
            ImageView bgImage = (ImageView) section.getChildren().get(0);
            // Adjust position slightly for parallax effect
            bgImage.setTranslateY(bgImage.getTranslateY() + (scrollDelta * 0.3));
        }
    }
    
    private void animateSectionEntrance(Node section, int index) {
        // Different animation types based on section index
        switch (index) {
            case 0: // Hero section - fade in
                fadeInAnimation(section);
                break;
            case 1: // About section - slide from left
                slideInFromLeftAnimation(section);
                break;
            case 2: // Menu section - fade in with scale
                fadeInWithScaleAnimation(section);
                break;
            case 3: // Testimonials - slide from right
                slideInFromRightAnimation(section);
                break;
            case 4: // CTA - fade in
                fadeInAnimation(section);
                break;
            case 5: // Footer - slide from bottom
                slideInFromBottomAnimation(section);
                break;
            default:
                fadeInAnimation(section);
        }
    }
    
    private void fadeInAnimation(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(800), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }
    
    private void fadeInWithScaleAnimation(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(800), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        
        ScaleTransition st = new ScaleTransition(Duration.millis(800), node);
        st.setFromX(0.8);
        st.setFromY(0.8);
        st.setToX(1);
        st.setToY(1);
        
        ParallelTransition pt = new ParallelTransition(ft, st);
        pt.play();
    }
    
    private void slideInFromLeftAnimation(Node node) {
        node.setTranslateX(-100);
        node.setOpacity(0);
        
        FadeTransition ft = new FadeTransition(Duration.millis(1000), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        
        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), node);
        tt.setFromX(-100);
        tt.setToX(0);
        
        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.play();
    }
    
    private void slideInFromRightAnimation(Node node) {
        node.setTranslateX(100);
        node.setOpacity(0);
        
        FadeTransition ft = new FadeTransition(Duration.millis(1000), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        
        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), node);
        tt.setFromX(100);
        tt.setToX(0);
        
        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.play();
    }
    
    private void slideInFromBottomAnimation(Node node) {
        node.setTranslateY(100);
        node.setOpacity(0);
        
        FadeTransition ft = new FadeTransition(Duration.millis(1000), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        
        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), node);
        tt.setFromY(100);
        tt.setToY(0);
        
        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.play();
    }
    
    private void initializeAnimations(VBox content) {
        // Set initial state for all sections (invisible)
        content.getChildren().forEach(node -> {
            node.setOpacity(0);
        });
        
        // Start first section animation immediately
        if (!content.getChildren().isEmpty()) {
            animateSectionEntrance(content.getChildren().get(0), 0);
        }
    }
    
    // Record class for menu items
    private record MenuItem(String name, double price, String imagePath) {}
    
    // Method to load menu items from database
    public List<MenuItem> loadMenuItems() {
        List<MenuItem> menuItems = new ArrayList<>();
        String query = "SELECT name, price, image_path FROM menu_items WHERE availability = 'Available' AND category_id = 2";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String imagePath = rs.getString("image_path");
                menuItems.add(new MenuItem(name, price, imagePath));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Add some sample items if database fails
            menuItems.add(new MenuItem("Litson Manok", 299.00, "/C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/menu_images/litson_manok.png"));
            menuItems.add(new MenuItem("Liempo", 249.00, "/C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/menu_images/liempo.png"));
            menuItems.add(new MenuItem("Sisig", 199.00, "/C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/menu_images/sisig.png"));
            menuItems.add(new MenuItem("Bangus", 179.00, "/C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/menu_images/bangus.png"));
            menuItems.add(new MenuItem("Chicken BBQ", 229.00, "/C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/menu_images/chicken_bbq.png"));
        }

        // Ensure we have at least a few items
        if (menuItems.isEmpty()) {
            menuItems.add(new MenuItem("Litson Manok", 299.00, "/C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/menu_images/litson_manok.png"));
            menuItems.add(new MenuItem("Liempo", 249.00, "/C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/menu_images/liempo.png"));
            menuItems.add(new MenuItem("Sisig", 199.00, "/C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/menu_images/sisig.png"));
        }

        return menuItems;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
                    