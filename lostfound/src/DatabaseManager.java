import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    // SQLite database file will be created in the project folder
    private static final String URL = "jdbc:sqlite:lostfound.db";

    // Unit 5: JDBC Connection & Exception Handling (Unit 3)
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
        return conn;
    }

    // Unit 5: Working with Statements to create tables
    public static void setupDatabase() {
        String createItemsTable = "CREATE TABLE IF NOT EXISTS items ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "type TEXT NOT NULL, "
                + "title TEXT NOT NULL, "
                + "description TEXT, "
                + "location TEXT, "
                + "reported_by TEXT NOT NULL, "
                + "is_claimed INTEGER DEFAULT 0"
                + ");";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createItemsTable);
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }
}