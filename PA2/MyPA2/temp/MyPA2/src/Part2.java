import java.sql.*;
import edu.brandeis.cs127b.pa2.graphviz.*;
public class Part2 {
	static final String JDBC_DRIVER = "com.postgresql.jdbc.Driver";
	static final String DB_TYPE = "postgresql";
	static final String DB_DRIVER = "jdbc";
	static final String DB_NAME = System.getenv("PGDATABASE");
	static final String DB_HOST = System.getenv("PGHOST");
	static final String DB_URL = String.format("%s:%s://%s/%s",DB_DRIVER, DB_TYPE, DB_HOST, DB_NAME);
	static final String DB_USER = System.getenv("PGUSER");
	static final String DB_PASSWORD = System.getenv("PGPASSWORD");

	static final String QUERY = "SELECT num1, num2, random() FROM generate_series(1, 5) as num1, generate_series(5,10) as num2";
    
	public static void main(String[] args) throws SQLException{
		DirectedGraph g = new DirectedGraph();
		try {
			Connection conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
			Statement st = conn.createStatement();
     		ResultSet rs = st.executeQuery(QUERY);
			String fromLabel;
			String toLabel;
			String weight;
			while ( rs.next() ) {
				fromLabel = rs.getString(1).trim();
				toLabel = rs.getString(2).trim();
				weight = rs.getString(3).trim();
				Node from = new Node(fromLabel);
				Node to = new Node(toLabel);
				DirectedEdge e = new DirectedEdge(from, to);
				e.addLabel(weight);
				g.add(e);
			}
			System.out.println(g);
		} catch (SQLException s) {
			throw s;
		}

		

	}

}
