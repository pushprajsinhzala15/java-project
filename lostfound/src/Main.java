import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        // Setup DB on startup
        DatabaseManager.setupDatabase();
        
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            
            System.out.println("=====================================");
            System.out.println("   WELCOME TO LOST & FOUND PORTAL    ");
            System.out.println("=====================================");
            
            while (running) {
                System.out.println("\n--- MAIN MENU ---");
                System.out.println("1. Report a Lost Item");
                System.out.println("2. Report a Found Item");
                System.out.println("3. View All Unclaimed Items");
                System.out.println("4. Claim an Item");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                
                String choice = scanner.nextLine();
                
                switch (choice) {
                    case "1":
                        reportItem(scanner, "Lost");
                        break;
                    case "2":
                        reportItem(scanner, "Found");
                        break;
                    case "3":
                        viewItems();
                        break;
                    case "4":
                        claimItem(scanner);
                        break;
                    case "5":
                        running = false;
                        System.out.println("Exiting system. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

    // Unit 5: JDBC PreparedStatement for Inserting Data
    private static void reportItem(Scanner scanner, String type) {
        System.out.println("\n--- Report a " + type + " Item ---");
        System.out.print("Enter Title (e.g., Black Wallet): ");
        String title = scanner.nextLine();
        
        System.out.print("Enter Description: ");
        String desc = scanner.nextLine();
        
        System.out.print("Enter Location (e.g., Library): ");
        String location = scanner.nextLine();
        
        System.out.print("Enter your Name (Reporter): ");
        String reporter = scanner.nextLine();

        String sql = "INSERT INTO items(type, title, description, location, reported_by) VALUES(?,?,?,?,?)";

        // Unit 3: Exception Handling
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, type);
            pstmt.setString(2, title);
            pstmt.setString(3, desc);
            pstmt.setString(4, location);
            pstmt.setString(5, reporter);
            
            pstmt.executeUpdate();
            System.out.println("✅ Item successfully reported!");
            
        } catch (SQLException e) {
            System.out.println("❌ Error reporting item: " + e.getMessage());
        }
    }

    // Unit 5: JDBC ResultSet for Displaying Data
    private static void viewItems() {
        System.out.println("\n--- UNCLAIMED ITEMS ---");
        String sql = "SELECT * FROM items WHERE is_claimed = 0";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            boolean found = false;
            System.out.printf("%-5s | %-5s | %-15s | %-15s | %-10s%n", "ID", "Type", "Title", "Location", "Reporter");
            System.out.println("-----------------------------------------------------------------------");
            
            while (rs.next()) {
                found = true;
                int id = rs.getInt("id");
                String type = rs.getString("type");
                String title = rs.getString("title");
                String loc = rs.getString("location");
                String reporter = rs.getString("reported_by");
                
                System.out.printf("%-5d | %-5s | %-15s | %-15s | %-10s%n", id, type, title, loc, reporter);
            }
            
            if (!found) {
                System.out.println("No unclaimed items found.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error viewing items: " + e.getMessage());
        }
    }

    // Unit 5: JDBC PreparedStatement for Updating Data
    private static void claimItem(Scanner scanner) {
        System.out.print("\nEnter the ID of the item you want to claim: ");
        String idStr = scanner.nextLine();
        
        // Unit 3: Exception Handling (handling bad input)
        try {
            int id = Integer.parseInt(idStr);
            String sql = "UPDATE items SET is_claimed = 1 WHERE id = ? AND is_claimed = 0";

            try (Connection conn = DatabaseManager.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("✅ Item claimed successfully! (Contact admin for physical pickup)");
                } else {
                    System.out.println("❌ Item not found or already claimed.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID. Please enter a number.");
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }
}