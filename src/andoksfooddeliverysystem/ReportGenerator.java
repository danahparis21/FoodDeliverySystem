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
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.Image;
import com.itextpdf.kernel.geom.PageSize;
import java.awt.Desktop;
import java.sql.*;

public class ReportGenerator {

    public static void generateReport() {
        String dest = "Andoks_Performance_Report.pdf";
        try {
            // Create PDF writer
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            pdf.setDefaultPageSize(new PageSize(612, 936)); // 8.5 x 13 inches in points

            Document document = new Document(pdf);

            // HEADER (Add title or some header content)
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
        }
        

        // Revenue Stats (example: Today's revenue)
        String todayRevenueQuery = buildRevenueQuery("Today");
        try (Connection conn = Database.connect(); 
             PreparedStatement stmt = conn.prepareStatement(todayRevenueQuery);
             ResultSet rs = stmt.executeQuery()) {
            StringBuilder revenueStats = new StringBuilder("Today's Revenue:\n");
            while (rs.next()) {
                revenueStats.append("Hour: ").append(rs.getInt("hour")).append(" Revenue: ").append(rs.getDouble("revenue")).append("\n");
            }
            document.add(new Paragraph(revenueStats.toString())
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginBottom(10));
        }

       // Assuming 6 columns: Name, Rating, Reviews, Orders, Earnings, Status
         Table table = new Table(6);
        table.addHeaderCell("Rider Name");
        table.addHeaderCell("Rating");
        table.addHeaderCell("Reviews");
        table.addHeaderCell("Orders Completed");
        table.addHeaderCell("Total Earnings");
        table.addHeaderCell("Status");

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
                table.addCell(rs.getString("name"));
                table.addCell(String.valueOf(rs.getDouble("average_rating")));
                table.addCell(String.valueOf(rs.getInt("total_reviews")));
                table.addCell(String.valueOf(rs.getInt("order_count")));
                table.addCell(String.valueOf(rs.getDouble("total_earnings")));
                table.addCell(rs.getString("status"));
            }
        }
        document.add(table);

        // Close document

            System.out.println("PDF Created: " + dest);

            // Open the generated PDF file in the default browser (or any PDF viewer)
            File pdfFile = new File(dest);
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (pdfFile.exists()) {
                    desktop.open(pdfFile);  // This opens the file in the default viewer (browser, if set)
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     public static String buildRevenueQuery(String range) {
        switch (range) {
            case "Today":
                return "SELECT HOUR(order_date) AS hour, SUM(total_price) AS revenue " +
                       "FROM orders " +
                       "WHERE DATE(order_date) = CURDATE() " +
                       "GROUP BY HOUR(order_date) " +
                       "ORDER BY hour";
            case "Weekly":
                return "SELECT DATE_FORMAT(order_date, '%Y-%m-%d') AS day, SUM(total_price) AS revenue " +
                       "FROM orders " +
                       "WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                       "GROUP BY DATE(order_date) " +
                       "ORDER BY day";
            case "Monthly":
                return "SELECT DATE_FORMAT(order_date, '%Y-%m') AS month, SUM(total_price) AS revenue " +
                       "FROM orders " +
                       "WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
                       "GROUP BY DATE_FORMAT(order_date, '%Y-%m') " +
                       "ORDER BY month";
            case "Yearly":
                return "SELECT YEAR(order_date) AS year, SUM(total_price) AS revenue " +
                       "FROM orders " +
                       "WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL 5 YEAR) " +
                       "GROUP BY YEAR(order_date) " +
                       "ORDER BY year";
            default:
                return buildRevenueQuery("Today");
        }
    }
}

