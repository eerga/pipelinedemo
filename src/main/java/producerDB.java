import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.nio.file.Files;
import java.util.Properties;
import java.sql.*;


public class producerDB extends ProducerSample {
    private Connection conn;
    private int limit = 0; // in case of large amount of records, limit the # of output records

    private Schema schema;

    public producerDB(int limit) {
        Properties props = new Properties();
        props.put("user", "postgres");
        props.put("password", "Erichka1");


        String schemaString = "{\"type\":\"record\"," +
                "\"name\":\"capitalbikeshare\"," +
                "\"fields\":[{\"name\":\"Duration\",\"type\":\"int\"},"+
                "{\"name\":\"Start_date\",\"type\":\"string\"},"+
                "{\"name\":\"End_date\",\"type\":\"string\"},"+
                "{\"name\":\"Start_station_number\",\"type\":\"int\"},"+
                "{\"name\":\"Start_station\",\"type\":\"string\"},"+
                "{\"name\":\"End_station_number\",\"type\":\"int\"},"+
                "{\"name\":\"End_station\",\"type\":\"string\"},"+
                "{\"name\":\"Bike_number\",\"type\":\"string\"},"+
                "{\"name\":\"Member_type\",\"type\":\"string\"}]}";

        Schema.Parser parser = new Schema.Parser();
        schema = parser.parse(schemaString);


        //Connection conn = DriverManager.getConnection(Main.DB_CONNECTION); // should handle SQLException Error
        try {
            conn = DriverManager.getConnection(Main.DB_CONNECTION, props);
        } catch(SQLException throwables) {
            throwables.printStackTrace();
        }
        this.limit = limit; // this is a better practice to declare limit constant
    }

    public void run_query() {
        if(conn != null){
            Statement stmt = null;
            String query = "SELECT * FROM capitalbikeshare";
            if (limit != -1) {
                query += String.format(" LIMIT(%d)", limit);
            }
            int count = 0;
            try{
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    GenericRecord avroRecord = new GenericData.Record(schema);
                    avroRecord.put("Duration", rs.getInt("Duration"));
                    avroRecord.put("Start_date", rs.getString("Start_date"));
                    avroRecord.put("End_date", rs.getString("End_date"));
                    avroRecord.put("Start_station_number", rs.getInt("Start_station_number"));
                    avroRecord.put("Start_station", rs.getString("Start_station"));
                    avroRecord.put("End_station_number", rs.getInt("End_station_number"));
                    avroRecord.put("End_station", rs.getString("End_station"));
                    avroRecord.put("Bike_number", rs.getString("Bike_number"));
                    avroRecord.put("Member_type", rs.getString("Member_type"));

                    count++;
                }
                System.out.printf("Produced %d records to topic %s\n", count, Main.TOPIC);

            }
            catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
    }
}
