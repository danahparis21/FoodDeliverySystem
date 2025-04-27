package andoksfooddeliverysystem;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class PieChartUtils {

    // Color constants
    private static final String[] CHART_COLORS = {
            "#FF9AA2", "#FFB7B2", "#FFDAC1", "#E2F0CB", 
            "#B5EAD7", "#C7CEEA", "#F2D5F8", "#E0BBE4"
    };
    private static final String WHITE = "#FFFFFF";
    private static final String DARK_TEXT = "#333333";
    private static final String LIGHT_TEXT = "#757575";
    private static final String BACKGROUND = "#F9FAFE";
    
    // Chart dimensions
    private static final int CHART_SIZE = 200; // Base chart size
    private static final int LABEL_SPACE = 120; // Space for labels on each side
    
    /**
     * Creates a styled pie chart with uniform size and interactive features
     */
    public static StackPane createStyledPieChart(String title, PieChart.Data[] data) {
        // Create and style the pie chart
        PieChart chart = new PieChart();
        chart.setLabelsVisible(false); // We'll create our own labels
        chart.setLegendVisible(false);
        chart.setAnimated(true);
        chart.setStartAngle(90);
        
        // Set chart size
        chart.setPrefSize(CHART_SIZE, CHART_SIZE);
        chart.setMaxSize(CHART_SIZE, CHART_SIZE);
        chart.setMinSize(CHART_SIZE, CHART_SIZE);
        
        // Add data
        for (PieChart.Data slice : data) {
            chart.getData().add(slice);
        }
        
        // Create chart title
        Label chartTitle = new Label(title);
        chartTitle.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );
        
        // Container for the chart and its external labels
        StackPane chartWithLabels = new StackPane();
        int totalSize = CHART_SIZE + (LABEL_SPACE * 2); // Extra space for labels on both sides
        chartWithLabels.setPrefSize(totalSize, totalSize);
        chartWithLabels.setMaxSize(totalSize, totalSize);
        chartWithLabels.setMinSize(totalSize, totalSize);
        chartWithLabels.getChildren().add(chart);
        
        // Apply custom colors and add external labels
        int colorIndex = 0;
        final int sliceCount = chart.getData().size();
        
        for (PieChart.Data slice : chart.getData()) {
            String color = CHART_COLORS[colorIndex % CHART_COLORS.length];
            
            final int index = colorIndex;
            Platform.runLater(() -> {
                Node sliceNode = slice.getNode();
                sliceNode.setStyle("-fx-pie-color: " + color + ";");
                
                // Add hover effect
                sliceNode.setOnMouseEntered(e -> {
                    sliceNode.setStyle("-fx-pie-color: " + color + "; -fx-scale-x: 1.05; -fx-scale-y: 1.05;");
                    sliceNode.setEffect(new DropShadow(10, Color.valueOf(color).darker()));
                });
                sliceNode.setOnMouseExited(e -> {
                    sliceNode.setStyle("-fx-pie-color: " + color + "; -fx-scale-x: 1; -fx-scale-y: 1;");
                    sliceNode.setEffect(null);
                });
                
                // Add tooltip showing percentage and value
                Tooltip tooltip = new Tooltip(
                    slice.getName() + ": " + 
                    String.format("%.1f%%", (slice.getPieValue() / getTotalValue(chart.getData()) * 100))
                );
                tooltip.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
                Tooltip.install(sliceNode, tooltip);
                
                // Add click animation
                sliceNode.setOnMouseClicked(e -> pulseAnimation(sliceNode));
                
                // Create external label
                addExternalLabelWithVaryingLines(chartWithLabels, sliceNode, slice, index, color, sliceCount);
            });
            
            colorIndex++;
        }
        
        VBox chartBox = new VBox(10);
        chartBox.setAlignment(Pos.CENTER);
        chartBox.getChildren().addAll(chartTitle, chartWithLabels);
        chartBox.setPadding(new Insets(5));
        
        // Add shadow effect
        DropShadow glow = new DropShadow();
        glow.setColor(Color.valueOf("#E0E7FF"));
        glow.setRadius(15);
        glow.setSpread(0.4);
        
        // Create a stack pane for the final result
        StackPane chartStack = new StackPane();
        chartStack.getChildren().add(chartBox);
        chartStack.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 15px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);"
        );
        
        return chartStack;
    }
    
    /**
     * Adds an external label pointing to the pie slice with varying line lengths to prevent overlap
     */
    private static void addExternalLabelWithVaryingLines(StackPane container, Node sliceNode, 
                                                        PieChart.Data slice, int index, 
                                                        String color, int sliceCount) {
        try {
            // We need to wait for the chart to render to get the center of each slice
            Platform.runLater(() -> {
                // Calculate the angle for the label based on slice position
                double total = getTotalValue(((PieChart)sliceNode.getParent().getParent()).getData());
                double sliceAngle = slice.getPieValue() / total * 360.0;
                
                // Find the start angle by adding up all previous slice angles
                double startAngle = 90; // Starting from the top (90 degrees)
                int i = 0;
                for (PieChart.Data d : ((PieChart)sliceNode.getParent().getParent()).getData()) {
                    if (i == index) break;
                    startAngle += (d.getPieValue() / total) * 360.0;
                    i++;
                }
                
                // Calculate the midpoint angle of this slice
                double midAngle = startAngle + (sliceAngle / 2);
                double radians = Math.toRadians(midAngle);
                
                // Calculate label position (slightly outside the pie)
                double innerRadius = CHART_SIZE / 2.0 * 0.7; // Starting point on slice
                
                // Vary the outer line length based on position to prevent overlap
                // Organize lines in a more spaced-out pattern
                // Even indices get shorter lines, odd indices get longer lines
                double lineVariation = (index % 2 == 0) ? 1.0 : 1.3;
                double outerRadius = CHART_SIZE / 2.0 * (1.3 * lineVariation);
                
                // Calculate label radius with even more variation to separate them
                double labelRadius = outerRadius + 10 + (index % 3) * 10;
                
                // Calculate coordinates for the line
                double sliceCenterX = Math.cos(radians) * innerRadius;
                double sliceCenterY = -Math.sin(radians) * innerRadius;
                double outerX = Math.cos(radians) * outerRadius;
                double outerY = -Math.sin(radians) * outerRadius;
                double labelX = Math.cos(radians) * labelRadius;
                double labelY = -Math.sin(radians) * labelRadius;
                
                // Create a two-segment line for better visibility
                Line innerLine = new Line(sliceCenterX, sliceCenterY, outerX, outerY);
                innerLine.setStroke(Color.valueOf(color));
                innerLine.setStrokeWidth(2);
                
                // Second line segment - horizontal or vertical depending on position
                Line outerLine = new Line();
                outerLine.setStartX(outerX);
                outerLine.setStartY(outerY);
                
                // Determine which quadrant the label is in
                boolean isRight = Math.cos(radians) > 0;
                boolean isTop = Math.sin(radians) < 0;
                
                // Set the end point and position of label
                double endX;
                double labelOffsetX = 0;
                if (isRight) {
                    endX = outerX + 30 + (index % 3) * 15;
                    labelOffsetX = 5;
                } else {
                    endX = outerX - 30 - (index % 3) * 15;
                    labelOffsetX = -5;
                }
                
                outerLine.setEndX(endX);
                outerLine.setEndY(outerY);
                outerLine.setStroke(Color.valueOf(color));
                outerLine.setStrokeWidth(1.5);
                
                // Create label with percentage
                double percentage = slice.getPieValue() / total * 100;
                String labelText = slice.getName() + "\n" + String.format("%.1f%%", percentage);
                Label label = new Label(labelText);
                label.setTextFill(Color.valueOf(DARK_TEXT));
                label.setFont(Font.font("System", FontWeight.BOLD, 10));
                label.setTextAlignment(isRight ? TextAlignment.LEFT : TextAlignment.RIGHT);
                
                // Position the label at the end of the second line segment
                double finalLabelX = endX + labelOffsetX;
                label.setTranslateX(finalLabelX);
                label.setTranslateY(outerY);
                
                // Adjust label position based on which quadrant it's in
                if (!isRight) {
                    label.setTranslateX(finalLabelX - label.prefWidth(-1));
                }
                
                // Add a small white background to the label for better readability
                label.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.7);" +
                    "-fx-padding: 2 4;" +
                    "-fx-background-radius: 2;"
                );
                
                // Add lines and label to the container
                container.getChildren().addAll(innerLine, outerLine, label);
            });
        } catch (Exception e) {
            System.err.println("Error adding external label: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to get the total value of all pie slices
     */
    private static double getTotalValue(javafx.collections.ObservableList<PieChart.Data> data) {
        double total = 0;
        for (PieChart.Data slice : data) {
            total += slice.getPieValue();
        }
        return total;
    }
    
    /**
     * Creates a pulse animation when a pie slice is clicked
     */
    private static void pulseAnimation(Node node) {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(node.scaleXProperty(), 1),
                new KeyValue(node.scaleYProperty(), 1)
            ),
            new KeyFrame(Duration.millis(150), 
                new KeyValue(node.scaleXProperty(), 1.1),
                new KeyValue(node.scaleYProperty(), 1.1)
            ),
            new KeyFrame(Duration.millis(300), 
                new KeyValue(node.scaleXProperty(), 1),
                new KeyValue(node.scaleYProperty(), 1)
            )
        );
        timeline.play();
    }
    
    /**
     * Creates a panel with multiple pie charts in a single row
     */
    public static VBox createPieChartsPanel(PieChart.Data[][] chartsData, String[] titles) {
        // Create a single row for all charts
        HBox row = new HBox(50); // Increased spacing between charts
        row.setAlignment(Pos.CENTER);
        
        // Add charts to the row
        for (int i = 0; i < titles.length; i++) {
            StackPane chart = createStyledPieChart(titles[i], chartsData[i]);
            HBox.setHgrow(chart, Priority.ALWAYS); // Allow the chart to grow
            row.getChildren().add(chart);
        }
        
        // Create the panel with just one row
        VBox panel = new VBox(20);
        panel.getChildren().add(row);
        panel.setAlignment(Pos.CENTER);
        
        return stylePieChartsPanel(panel);
    }
    
    /**
     * Styles the pie charts panel
     */
    public static VBox stylePieChartsPanel(VBox original) {
        // Apply styling to the panel
        VBox styledPanel = new VBox(15);
        styledPanel.getChildren().addAll(original.getChildren());
        styledPanel.setPadding(new Insets(20));
        styledPanel.setAlignment(Pos.CENTER); // Center everything
        styledPanel.setStyle(
            "-fx-background-color: " + BACKGROUND + ";" +
            "-fx-background-radius: 10px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );
        
        // Add section title
        Label sectionTitle = new Label("KEY METRICS DISTRIBUTION");
        sectionTitle.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + DARK_TEXT + ";" +
            "-fx-padding: 0 0 10 0;"
        );
        
        styledPanel.getChildren().add(0, sectionTitle);
        
        return styledPanel;
    }
    
    // Example usage method
    public static VBox createExamplePieChartsPanel() {
        // Sample data for 3 charts in a single row
        PieChart.Data[][] chartsData = new PieChart.Data[][] {
            { // Revenue Sources
                new PieChart.Data("Product A", 35),
                new PieChart.Data("Product B", 25),
                new PieChart.Data("Services", 40)
            },
            { // Customer Types
                new PieChart.Data("Enterprise", 45),
                new PieChart.Data("SMB", 30),
                new PieChart.Data("Consumer", 25)
            },
            { // Geographic Distribution
                new PieChart.Data("North America", 40),
                new PieChart.Data("Europe", 35),
                new PieChart.Data("Asia", 20),
                new PieChart.Data("Other", 5)
            }
        };
        
        String[] titles = {
            "Revenue Sources", 
            "Customer Types", 
            "Geographic Distribution"
        };
        
        return createPieChartsPanel(chartsData, titles);
    }
}