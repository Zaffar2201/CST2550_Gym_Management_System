import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DbConnection {
    
    /*Database connection*/
    public Connection getConnection(){
		
		try {
			 Class.forName("com.mysql.cj.jdbc.Driver");
                         /*Set database location*/
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/GYM2","root","");
			return connection;			

		} catch(ClassNotFoundException | SQLException ex) {
			
			System.out.println("Error:" + ex.getMessage());

		}

	return null;

	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
