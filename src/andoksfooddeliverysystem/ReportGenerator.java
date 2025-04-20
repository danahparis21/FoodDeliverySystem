package andoksfooddeliverysystem;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import java.awt.Desktop;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReportGenerator {
    // Define Andok's brand colors
    private static final DeviceRgb ANDOKS_RED = new DeviceRgb(200, 16, 46);  // Deep red color
    private static final DeviceRgb ANDOKS_YELLOW = new DeviceRgb(255, 204, 0);  // Bright yellow
    private static final DeviceRgb LIGHT_YELLOW = new DeviceRgb(255, 245, 210);  // Light yellow for backgrounds
    private static final DeviceRgb LIGHT_RED = new DeviceRgb(255, 230, 230);  // Light red for highlights
    private static final DeviceRgb DARK_TEXT = new DeviceRgb(68, 68, 68);  // Dark gray for text
    private static final DeviceRgb ACCENT_COLOR = new DeviceRgb(50, 50, 50);  // Nearly black for accents

    public static void generateReport() {
        String dest = "Andoks_Performance_Report.pdf";
        try {
            // Create PDF writer
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            pdf.setDefaultPageSize(new PageSize(612, 936)); // 8.5 x 13 inches in points

            Document document = new Document(pdf);
            document.setMargins(36, 36, 36, 36); // Set margins for better readability

            // Main fonts
            PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            
            // HEADER SECTION WITH LOGO
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{20, 80}));
            headerTable.setWidth(UnitValue.createPercentValue(100));
            
            // Try to add an image logo (assuming you have one)
            try {
                // Replace with the actual logo path
                String logoPath = "src/icons/andoksLogo.png";  // Relative path
                File logoFile = new File(logoPath);
                
                if (logoFile.exists()) {
                    ImageData imageData = ImageDataFactory.create(logoPath);
                    Image logoImage = new Image(imageData).setWidth(70).setHeight(70);
                    Cell logoCell = new Cell().add(logoImage).setBorder(null).setVerticalAlignment(VerticalAlignment.MIDDLE);
                    headerTable.addCell(logoCell);
                } else {
                    // Create a placeholder colored cell if no logo is available
                    Cell logoCell = new Cell()
                            .setBackgroundColor(ANDOKS_RED)
                            .setHeight(70)
                            .add(new Paragraph("A").setFontColor(ANDOKS_YELLOW).setFontSize(36)
                                .setTextAlignment(TextAlignment.CENTER))
                            .setBorder(null);
                    headerTable.addCell(logoCell);
                }
            } catch (Exception e) {
                Cell logoCell = new Cell().add(new Paragraph("ANDOK'S")
                        .setFont(headerFont)
                        .setFontSize(24)
                        .setFontColor(ANDOKS_RED))
                        .setBorder(null);
                headerTable.addCell(logoCell);
            }
            
            // Title section
            Cell titleCell = new Cell();
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            String formattedDate = today.format(formatter);
            
            Paragraph title = new Paragraph("PERFORMANCE REPORT")
                    .setFont(headerFont)
                    .setFontSize(24)
                    .setFontColor(ANDOKS_RED)
                    .setTextAlignment(TextAlignment.RIGHT);
            
            Paragraph subtitle = new Paragraph(formattedDate)
                    .setFont(regularFont)
                    .setFontSize(12)
                    .setFontColor(ACCENT_COLOR)
                    .setTextAlignment(TextAlignment.RIGHT);
            
            titleCell.add(title).add(subtitle).setBorder(null);
            headerTable.addCell(titleCell);
            document.add(headerTable);
            
            // Add a divider
            SolidBorder divider = new SolidBorder(ANDOKS_RED, 2);
            document.add(new Paragraph("").setBorderBottom(divider).setMarginBottom(15));

            // EXECUTIVE SUMMARY SECTION
            document.add(createSectionHeader("EXECUTIVE SUMMARY", headerFont));
            
            // Add a colored box for key metrics
            Table statsTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
            statsTable.setWidth(UnitValue.createPercentValue(100));
            statsTable.setBackgroundColor(LIGHT_YELLOW);
            statsTable.setMarginBottom(15);
            
            // Today's stats
            String todayQuery = """
                SELECT 
                    COUNT(*) AS total_orders,
                    SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) AS completed,
                    SUM(CASE WHEN status = 'Pending' THEN 1 ELSE 0 END) AS pending,
                    SUM(CASE WHEN status = 'Cancelled' THEN 1 ELSE 0 END) AS cancelled,
                    COUNT(DISTINCT customer_id) AS unique_customers,
                    SUM(CASE WHEN order_type = 'Delivery' THEN 1 ELSE 0 END) AS delivery_orders,
                    SUM(CASE WHEN order_type = 'Pick Up' THEN 1 ELSE 0 END) AS pickup_orders
                FROM orders
                WHERE DATE(order_date) = CURDATE()
            """;
            
            try (Connection conn = Database.connect(); 
                 PreparedStatement stmt = conn.prepareStatement(todayQuery);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    Cell overviewCell = new Cell(1, 2)
                            .add(new Paragraph("TODAY'S OVERVIEW")
                                    .setFont(boldFont)
                                    .setFontSize(14)
                                    .setFontColor(ANDOKS_RED)
                                    .setTextAlignment(TextAlignment.CENTER))
                            .setBorder(null)
                            .setPadding(5);
                    statsTable.addCell(overviewCell);
                    
                    // Left column with order metrics
                    Cell ordersCell = new Cell()
                            .add(createMetricParagraph("Total Orders", rs.getInt("total_orders"), boldFont, regularFont))
                            .add(createMetricParagraph("Completed", rs.getInt("completed"), boldFont, regularFont))
                            .add(createMetricParagraph("Pending", rs.getInt("pending"), boldFont, regularFont))
                            .add(createMetricParagraph("Cancelled", rs.getInt("cancelled"), boldFont, regularFont))
                            .setBorder(null)
                            .setPadding(10);
                    statsTable.addCell(ordersCell);
                    
                    // Right column with customer metrics
                    Cell customersCell = new Cell()
                            .add(createMetricParagraph("Unique Customers", rs.getInt("unique_customers"), boldFont, regularFont))
                            .add(createMetricParagraph("Delivery Orders", rs.getInt("delivery_orders"), boldFont, regularFont))
                            .add(createMetricParagraph("Pickup Orders", rs.getInt("pickup_orders"), boldFont, regularFont))
                            .setBorder(null)
                            .setPadding(10);
                    statsTable.addCell(customersCell);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                statsTable.addCell(new Cell().add(new Paragraph("Error loading today's statistics")
                        .setFont(regularFont)).setBorder(null));
            }
            document.add(statsTable);

            // REVENUE METRICS
            Table revenueTable = new Table(UnitValue.createPercentArray(new float[]{100}));
            revenueTable.setWidth(UnitValue.createPercentValue(100));
            revenueTable.setBackgroundColor(LIGHT_RED);
            revenueTable.setMarginBottom(20);
            
            Cell revenueHeaderCell = new Cell()
                    .add(new Paragraph("REVENUE METRICS")
                            .setFont(boldFont)
                            .setFontSize(14)
                            .setFontColor(ANDOKS_RED)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBorder(null)
                    .setPadding(5);
            revenueTable.addCell(revenueHeaderCell);
            
            // Revenue data in a visually appealing format
            Table revenueDataTable = new Table(UnitValue.createPercentArray(new float[]{33.33f, 33.33f, 33.33f}));
            revenueDataTable.setWidth(UnitValue.createPercentValue(100));
            
            // Add revenue data
            try {
                // Today's Revenue
                try (Connection conn = Database.connect(); 
                     PreparedStatement stmt = conn.prepareStatement(
                             "SELECT SUM(total_price) AS today_revenue FROM orders WHERE status = 'Completed' AND DATE(order_date) = CURDATE()");
                     ResultSet rs = stmt.executeQuery()) {
                    
                    if (rs.next()) {
                        double todayRevenue = rs.getDouble("today_revenue");
                        Cell cell = new Cell()
                                .add(createRevenueParagraph("TODAY", todayRevenue, boldFont, regularFont))
                                .setBorder(null)
                                .setPadding(10);
                        revenueDataTable.addCell(cell);
                    }
                }
                
                // Monthly Revenue
                try (Connection conn = Database.connect(); 
                     PreparedStatement stmt = conn.prepareStatement(
                             "SELECT SUM(total_price) AS monthly_revenue FROM orders WHERE status = 'Completed' AND MONTH(order_date) = MONTH(CURDATE()) AND YEAR(order_date) = YEAR(CURDATE())");
                     ResultSet rs = stmt.executeQuery()) {
                    
                    if (rs.next()) {
                        double monthlyRevenue = rs.getDouble("monthly_revenue");
                        Cell cell = new Cell()
                                .add(createRevenueParagraph("THIS MONTH", monthlyRevenue, boldFont, regularFont))
                                .setBorder(null)
                                .setPadding(10);
                        revenueDataTable.addCell(cell);
                    }
                }
                
                // Annual Revenue
                try (Connection conn = Database.connect(); 
                     PreparedStatement stmt = conn.prepareStatement(
                             "SELECT SUM(total_price) AS annual_revenue FROM orders WHERE status = 'Completed' AND YEAR(order_date) = YEAR(CURDATE())");
                     ResultSet rs = stmt.executeQuery()) {
                    
                    if (rs.next()) {
                        double annualRevenue = rs.getDouble("annual_revenue");
                        Cell cell = new Cell()
                                .add(createRevenueParagraph("THIS YEAR", annualRevenue, boldFont, regularFont))
                                .setBorder(null)
                                .setPadding(10);
                        revenueDataTable.addCell(cell);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                revenueDataTable.addCell(new Cell().add(new Paragraph("Error loading revenue data")
                        .setFont(regularFont)).setBorder(null));
            }
            
            Cell revenueDataCell = new Cell().add(revenueDataTable).setBorder(null);
            revenueTable.addCell(revenueDataCell);
            document.add(revenueTable);

            // TODAY'S ORDERS SECTION
            document.add(createSectionHeader("TODAY'S ORDERS", headerFont));
            
            String todayOrdersQuery = """
                SELECT order_id, customer_id, total_price, payment_method, status, order_date
                FROM orders
                WHERE DATE(order_date) = CURDATE()
            """;

            try (Connection conn = Database.connect();
                 PreparedStatement stmt = conn.prepareStatement(todayOrdersQuery);
                 ResultSet rs = stmt.executeQuery()) {

                Table orderTable = new Table(UnitValue.createPercentArray(new float[]{10, 15, 15, 15, 15, 30}));
                orderTable.setWidth(UnitValue.createPercentValue(100));

                // Table headers
                orderTable.addHeaderCell(createStyledHeaderCell("Order ID", boldFont));
                orderTable.addHeaderCell(createStyledHeaderCell("Customer ID", boldFont));
                orderTable.addHeaderCell(createStyledHeaderCell("Total Price", boldFont));
                orderTable.addHeaderCell(createStyledHeaderCell("Payment Method", boldFont));
                orderTable.addHeaderCell(createStyledHeaderCell("Status", boldFont));
                orderTable.addHeaderCell(createStyledHeaderCell("Order Date", boldFont));
                
                boolean alternateRow = false;
                while (rs.next()) {
                     // Define colors as DeviceRgb
                        DeviceRgb LIGHT_YELLOW = new DeviceRgb(255, 255, 204); // Define a light yellow color (you can adjust RGB values as needed)
                        DeviceRgb WHITE = new DeviceRgb(255, 255, 255); // Define white color

                        // Alternate row color logic
                        DeviceRgb rowColor = alternateRow ? LIGHT_YELLOW : WHITE;
                    orderTable.addCell(createStyledTableCell(String.valueOf(rs.getInt("order_id")), regularFont, rowColor));
                    orderTable.addCell(createStyledTableCell(String.valueOf(rs.getInt("customer_id")), regularFont, rowColor));
                    orderTable.addCell(createStyledTableCell(String.format("₱%,.2f", rs.getDouble("total_price")), regularFont, rowColor));
                    orderTable.addCell(createStyledTableCell(rs.getString("payment_method"), regularFont, rowColor));
                    
                    // Style the status cell based on the status value
                    String status = rs.getString("status");
                    DeviceRgb statusColor = switch (status.toLowerCase()) {
                        case "completed" -> new DeviceRgb(100, 200, 100); // Green for completed
                        case "pending" -> ANDOKS_YELLOW; // Yellow for pending
                        case "cancelled" -> ANDOKS_RED; // Red for cancelled
                        default -> rowColor;
                    };
                    
                    orderTable.addCell(createStatusTableCell(status, regularFont, statusColor));
                    orderTable.addCell(createStyledTableCell(rs.getTimestamp("order_date").toString(), regularFont, rowColor));
                    
                    alternateRow = !alternateRow;
                }

                document.add(orderTable.setMarginBottom(20));

            } catch (SQLException e) {
                e.printStackTrace();
                document.add(new Paragraph("Error loading today's orders")
                        .setFont(regularFont));
            }

            // POPULAR MENU ITEMS SECTION
            document.add(createSectionHeader("TOP 10 MOST POPULAR MENU ITEMS", headerFont));
            
            Table menuTable = new Table(UnitValue.createPercentArray(new float[]{40, 15, 15, 30}));
            menuTable.setWidth(UnitValue.createPercentValue(100));

            // Table headers
            menuTable.addHeaderCell(createStyledHeaderCell("Menu Item", boldFont));
            menuTable.addHeaderCell(createStyledHeaderCell("Qty Sold", boldFont));
            menuTable.addHeaderCell(createStyledHeaderCell("Orders", boldFont));
            menuTable.addHeaderCell(createStyledHeaderCell("Total Sales", boldFont));

            String itemQuery = """
               SELECT *  FROM item_performance_view
            """;

            try (Connection conn = Database.connect(); 
                 PreparedStatement stmt = conn.prepareStatement(itemQuery);
                 ResultSet rs = stmt.executeQuery()) {

                boolean alternateRow = false;
                while (rs.next()) {
                   // Define colors as DeviceRgb
                    DeviceRgb LIGHT_YELLOW = new DeviceRgb(255, 255, 204); // Define a light yellow color (you can adjust RGB values as needed)
                    DeviceRgb WHITE = new DeviceRgb(255, 255, 255); // Define white color

                    // Alternate row color logic
                    DeviceRgb rowColor = alternateRow ? LIGHT_YELLOW : WHITE;
                    menuTable.addCell(createStyledTableCell(rs.getString("item_name"), regularFont, rowColor));
                    menuTable.addCell(createStyledTableCell(String.valueOf(rs.getInt("total_quantity")), regularFont, rowColor));
                    menuTable.addCell(createStyledTableCell(String.valueOf(rs.getInt("total_orders")), regularFont, rowColor));
                    menuTable.addCell(createStyledTableCell(String.format("₱%,.2f", rs.getDouble("total_sales")), regularFont, rowColor));
                    
                    alternateRow = !alternateRow;
                }
                
                document.add(menuTable.setMarginBottom(20));
                
            } catch (SQLException e) {
                e.printStackTrace();
                document.add(new Paragraph("Error loading popular menu items")
                        .setFont(regularFont));
            }

            // RIDER PERFORMANCE SECTION
            document.add(createSectionHeader("RIDER PERFORMANCE", headerFont));
            
            Table riderTable = new Table(UnitValue.createPercentArray(new float[]{20, 10, 10, 15, 15, 10}));
            riderTable.setWidth(UnitValue.createPercentValue(100));
            
            // Table headers
            riderTable.addHeaderCell(createStyledHeaderCell("Rider Name", boldFont));
            riderTable.addHeaderCell(createStyledHeaderCell("Rating", boldFont));
            riderTable.addHeaderCell(createStyledHeaderCell("Reviews", boldFont));
            riderTable.addHeaderCell(createStyledHeaderCell("Orders", boldFont));
            riderTable.addHeaderCell(createStyledHeaderCell("Earnings", boldFont));
            riderTable.addHeaderCell(createStyledHeaderCell("Status", boldFont));

            String riderQuery = """
                SELECT * FROM `rider_performance_view`
            """;
            
            try (Connection conn = Database.connect(); 
                 PreparedStatement stmt = conn.prepareStatement(riderQuery);
                 ResultSet rs = stmt.executeQuery()) {
                
                boolean alternateRow = false;
                while (rs.next()) {
                     // Define colors as DeviceRgb
                        DeviceRgb LIGHT_YELLOW = new DeviceRgb(255, 255, 204); // Define a light yellow color (you can adjust RGB values as needed)
                        DeviceRgb WHITE = new DeviceRgb(255, 255, 255); // Define white color

                        // Alternate row color logic
                        DeviceRgb rowColor = alternateRow ? LIGHT_YELLOW : WHITE;
                    
                    riderTable.addCell(createStyledTableCell(rs.getString("name"), regularFont, rowColor));
                    
                    // Rating with color coding
                    double rating = rs.getDouble("average_rating");
                    DeviceRgb ratingColor = rating >= 4.5 ? new DeviceRgb(0, 150, 0) : // Dark green
                                     rating >= 4.0 ? new DeviceRgb(0, 200, 0) : // Green
                                     rating >= 3.5 ? ANDOKS_YELLOW : // Yellow
                                     ANDOKS_RED; // Red
                    
                    Cell ratingCell = createStyledTableCell(String.format("%.1f", rating), regularFont, rowColor);
                    ratingCell.setFontColor(ratingColor);
                    riderTable.addCell(ratingCell);
                    
                    riderTable.addCell(createStyledTableCell(String.valueOf(rs.getInt("total_reviews")), regularFont, rowColor));
                    riderTable.addCell(createStyledTableCell(String.valueOf(rs.getInt("order_count")), regularFont, rowColor));
                    riderTable.addCell(createStyledTableCell(String.format("₱%,.2f", rs.getDouble("total_earnings")), regularFont, rowColor));
                    
                    // Status with color coding
                    String status = rs.getString("online_status");
                    DeviceRgb statusColor = switch (status.toLowerCase()) {
                        case "online" -> new DeviceRgb(0, 180, 0); // Green for available
                        case "offline" -> ANDOKS_YELLOW; // Yellow for on delivery
                        default -> ANDOKS_RED; // Red for other statuses
                    };
                    
                    riderTable.addCell(createStatusTableCell(status, regularFont, statusColor));
                    
                    alternateRow = !alternateRow;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                document.add(new Paragraph("Error loading rider performance data")
                        .setFont(regularFont));
            }
            
            document.add(riderTable);
            
            // FOOTER
            document.add(new Paragraph("\n")); // Add some space before footer
            SolidBorder footerDivider = new SolidBorder(ANDOKS_RED, 1);
            document.add(new Paragraph("").setBorderTop(footerDivider).setMarginTop(15));
            
            Paragraph footer = new Paragraph("© " + LocalDate.now().getYear() + " Andok's Food Delivery System | Generated on " + formattedDate)
                    .setFont(regularFont)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(DARK_TEXT);
            document.add(footer);

            // Close the document - THIS IS CRUCIAL!
            document.close();

            System.out.println("PDF Created: " + dest);

            // Open the PDF after a short delay to ensure file is fully written
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        File pdfFile = new File(dest);
                        if (Desktop.isDesktopSupported() && pdfFile.exists()) {
                            Desktop.getDesktop().open(pdfFile);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 1000); // 1 second delay

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper methods for better styling
    private static Paragraph createSectionHeader(String text, PdfFont font) {
        return new Paragraph(text)
                .setFont(font)
                .setFontSize(16)
                .setFontColor(ANDOKS_RED)
                .setBold()
                .setMarginTop(10)
                .setMarginBottom(10);
    }
    
    private static Paragraph createMetricParagraph(String label, int value, PdfFont labelFont, PdfFont valueFont) {
        return new Paragraph()
                .add(new Text(label + ": ").setFont(labelFont).setFontColor(ACCENT_COLOR))
                .add(new Text(String.valueOf(value)).setFont(valueFont).setFontColor(ANDOKS_RED).setBold());
    }
    
    private static Paragraph createRevenueParagraph(String period, double value, PdfFont labelFont, PdfFont valueFont) {
        return new Paragraph()
                .add(new Text(period + "\n").setFont(labelFont).setFontSize(12).setFontColor(DARK_TEXT))
                .add(new Text("₱" + String.format("%,.2f", value)).setFont(valueFont).setFontSize(14).setFontColor(ANDOKS_RED).setBold())
                .setTextAlignment(TextAlignment.CENTER);
    }

    private static Cell createStyledHeaderCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text)
                    .setFont(font)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(ANDOKS_RED)
                .setPadding(5);
    }

    private static Cell createStyledTableCell(String text, PdfFont font, DeviceRgb bgColor) {
        return new Cell()
                .add(new Paragraph(text)
                    .setFont(font)
                    .setFontSize(10)
                    .setFontColor(DARK_TEXT))
                .setBackgroundColor(bgColor)
                .setPadding(5);
    }

    private static Cell createStatusTableCell(String status, PdfFont font, DeviceRgb statusColor) {
        return new Cell()
                .add(new Paragraph(status)
                    .setFont(font)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(statusColor)
                .setPadding(5);
    }

    public static String buildRevenueQuery(String range) {
        switch (range) {
            case "Today":
                return "SELECT * FROM today_hourly_revenue_view";
            case "Weekly":
                return "SELECT * FROM weekly_revenue_view";
            case "Monthly":
               return "SELECT * FROM monthly_revenue_view";
            case "Yearly":
                return "SELECT * FROM yearly_revenue_view";
            default:
                return buildRevenueQuery("Today");
        }
    }
}