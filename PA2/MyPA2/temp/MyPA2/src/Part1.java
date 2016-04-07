import java.sql.*;
import edu.brandeis.cs127b.pa2.gnuplot.*;
public class Part1 {
	static final String JDBC_DRIVER = "com.postgresql.jdbc.Driver";
	static final String DB_TYPE = "postgresql";
	static final String DB_DRIVER = "jdbc";
	static final String DB_NAME = System.getenv("PGDATABASE");
	static final String DB_HOST = System.getenv("PGHOST");
	static final String DB_URL = String.format("%s:%s://%s/%s",DB_DRIVER, DB_TYPE, DB_HOST, DB_NAME);
	static final String DB_USER = System.getenv("PGUSER");
	static final String DB_PASSWORD = System.getenv("PGPASSWORD");
	static Connection conn;


    static final String QUERY = "SELECT  num,sin(num) FROM generate_series(1, 200) as num";

	public static void main(String[] args) throws SQLException {
		conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
        final String title = "Plot Title";
        final String xlabel = "X Axis";
        final String ylabel = "Y Axis";
		LinePlot plot = new LinePlot(title, xlabel, ylabel);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(QUERY);
        Line<Point> l = new Line<Point>("Example");
		while ( rs.next() ) {
            l.add(new Point(rs.getDouble(1), rs.getDouble(2)));
		}
		plot.add(l);
		System.out.println(plot);

		

	}

}
