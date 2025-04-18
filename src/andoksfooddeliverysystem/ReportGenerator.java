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
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.Image;
import com.itextpdf.kernel.geom.PageSize;
import java.awt.Desktop;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class ReportGenerator {

   public static void generateReport() {
    String dest = "Andoks_Performance_Report.pdf";
    try {
        // Create PDF writer
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setDefaultPageSize(new PageSize(612, 936)); // 8.5 x 13 inches in points

        Document document = new Document(pdf);

        // HEADER
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        Paragraph header = new Paragraph("Andoks Food Delivery System - Performance Report")
                .setFont(font)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(header);

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
                String todayStats = "Orders Today: " + rs.getInt("total_orders") + " (" +
                                 rs.getInt("completed") + " Completed, " +
                                 rs.getInt("pending") + " Pending, " +
                                 rs.getInt("cancelled") + " Cancelled)\n" +
                                 "Customers Today: " + rs.getInt("unique_customers") + "\n" +
                                 "Delivery Orders: " + rs.getInt("delivery_orders") + "\n" +
                                 "Pickup Orders: " + rs.getInt("pickup_orders");

                document.add(new Paragraph(todayStats)
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                        .setTextAlignment(TextAlignment.LEFT)
                        .setMarginBottom(10));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            document.add(new Paragraph("Error loading today's statistics")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)));
        }

        
    // 1. Today's Revenue (Total Only)
    String totalTodayRevenueQuery = """
        SELECT SUM(total_price) AS today_revenue
        FROM orders
        WHERE status = 'Completed' AND DATE(order_date) = CURDATE()
    """;
    try (Connection conn = Database.connect(); 
         PreparedStatement stmt = conn.prepareStatement(totalTodayRevenueQuery);
         ResultSet rs = stmt.executeQuery()) {

        if (rs.next()) {
            double todayRevenue = rs.getDouble("today_revenue");
            document.add(new Paragraph("📅 Today's Revenue: ₱" + String.format("%,.2f", todayRevenue))
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginBottom(10));
        }
    } catch (SQLException | IOException e) {
        e.printStackTrace();
        document.add(new Paragraph("Error loading today's revenue")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)));
    }

    // 2. Monthly Revenue
    String monthlyRevenueQuery = """
        SELECT SUM(total_price) AS monthly_revenue
        FROM orders
        WHERE status = 'Completed' AND MONTH(order_date) = MONTH(CURDATE()) AND YEAR(order_date) = YEAR(CURDATE())
    """;
    try (Connection conn = Database.connect(); 
         PreparedStatement stmt = conn.prepareStatement(monthlyRevenueQuery);
         ResultSet rs = stmt.executeQuery()) {

        if (rs.next()) {
            double monthlyRevenue = rs.getDouble("monthly_revenue");
            document.add(new Paragraph("📆 Monthly Revenue: ₱" + String.format("%,.2f", monthlyRevenue))
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginBottom(10));
        }
    } catch (SQLException | IOException e) {
        e.printStackTrace();
        document.add(new Paragraph("Error loading monthly revenue")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)));
    }

    // 3. Annual Revenue
    String annualRevenueQuery = """
        SELECT SUM(total_price) AS annual_revenue
        FROM orders
        WHERE status = 'Completed' AND YEAR(order_date) = YEAR(CURDATE())
    """;
    try (Connection conn = Database.connect(); 
         PreparedStatement stmt = conn.prepareStatement(annualRevenueQuery);
         ResultSet rs = stmt.executeQuery()) {

        if (rs.next()) {
            double annualRevenue = rs.getDouble("annual_revenue");
            document.add(new Paragraph("📈 Annual Revenue: ₱" + String.format("%,.2f", annualRevenue))
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginBottom(20));
        }
    } catch (SQLException | IOException e) {
        e.printStackTrace();
        document.add(new Paragraph("Error loading annual revenue")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)));
    }
    // Orders Today Table
String todayOrdersQuery = """
    SELECT order_id, customer_id, total_price, payment_method, status, order_date
    FROM orders
    WHERE DATE(order_date) = CURDATE()
""";

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(todayOrdersQuery);
         ResultSet rs = stmt.executeQuery()) {

        document.add(new Paragraph("📋 Orders Today:")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(12)
                .setMarginBottom(5));

        Table orderTable = new Table(UnitValue.createPercentArray(new float[]{10, 15, 15, 15, 15, 30}));
        orderTable.setWidth(UnitValue.createPercentValue(100));

        // Table headers
        orderTable.addHeaderCell(createHeaderCell("Order ID"));
        orderTable.addHeaderCell(createHeaderCell("Customer ID"));
        orderTable.addHeaderCell(createHeaderCell("Total Price"));
        orderTable.addHeaderCell(createHeaderCell("Payment Method"));
        orderTable.addHeaderCell(createHeaderCell("Status"));
        orderTable.addHeaderCell(createHeaderCell("Order Date"));

        while (rs.next()) {
            orderTable.addCell(createTableCell(String.valueOf(rs.getInt("order_id"))));
            orderTable.addCell(createTableCell(String.valueOf(rs.getInt("customer_id"))));
            orderTable.addCell(createTableCell(String.format("₱%,.2f", rs.getDouble("total_price"))));
            orderTable.addCell(createTableCell(rs.getString("payment_method")));
            orderTable.addCell(createTableCell(rs.getString("status")));
            orderTable.addCell(createTableCell(rs.getTimestamp("order_date").toString()));
        }

        document.add(orderTable.setMarginBottom(20));

    } catch (SQLException | IOException e) {
        e.printStackTrace();
        document.add(new Paragraph("Error loading today's orders")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)));
    }


        // Rider Performance Table
        Table table = new Table(UnitValue.createPercentArray(new float[]{20, 10, 10, 15, 15, 10}));
        table.setWidth(UnitValue.createPercentValue(100));
        
        // Table headers
        table.addHeaderCell(createHeaderCell("Rider Name"));
        table.addHeaderCell(createHeaderCell("Rating"));
        table.addHeaderCell(createHeaderCell("Reviews"));
        table.addHeaderCell(createHeaderCell("Orders"));
        table.addHeaderCell(createHeaderCell("Earnings"));
        table.addHeaderCell(createHeaderCell("Status"));

        String riderQuery = """
            SELECT r.rider_id, r.name, r.average_rating, r.total_reviews, 
                   COUNT(o.order_id) AS order_count, SUM(o.total_price) AS total_earnings, r.status
            FROM riders r
            LEFT JOIN orders o ON r.rider_id = o.rider_id AND o.status = 'Completed'
            GROUP BY r.rider_id, r.name, r.average_rating, r.total_reviews, r.status
            ORDER BY order_count DESC
        """;
        
        try (Connection conn = Database.connect(); 
             PreparedStatement stmt = conn.prepareStatement(riderQuery);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                table.addCell(createTableCell(rs.getString("name")));
                table.addCell(createTableCell(String.format("%.1f", rs.getDouble("average_rating"))));
                table.addCell(createTableCell(String.valueOf(rs.getInt("total_reviews"))));
                table.addCell(createTableCell(String.valueOf(rs.getInt("order_count"))));
                table.addCell(createTableCell(String.format("₱%,.2f", rs.getDouble("total_earnings"))));
                table.addCell(createStatusCell(rs.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            document.add(new Paragraph("Error loading rider performance data")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)));
        }
        
        document.add(table);

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

// Helper methods for table styling
private static Cell createHeaderCell(String text) throws IOException {
    try {
        // Try to create the font for header cell
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        return new Cell()
                .add(new Paragraph(text)
                    .setFont(boldFont)
                    .setFontSize(10))
                .setBackgroundColor(new DeviceGray(0.75f))
                .setPadding(5);
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Error loading header font: " + e.getMessage());
        // Fallback to default font in case of an error
        return new Cell()
                .add(new Paragraph(text)
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(10))
                .setBackgroundColor(new DeviceGray(0.75f))
                .setPadding(5);
    }
}

private static Cell createTableCell(String text) throws IOException {
    return new Cell()
            .add(new Paragraph(text)
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(10))
            .setPadding(5);
}

private static Cell createStatusCell(String status) throws IOException {
    DeviceGray bgColor = switch (status.toLowerCase()) {
        case "available" -> new DeviceGray(0.9f);
        case "on delivery" -> new DeviceGray(0.7f);
        default -> new DeviceGray(0.95f);
    };
    
    return new Cell()
            .add(new Paragraph(status)
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(10))
            .setBackgroundColor(bgColor)
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

