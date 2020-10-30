import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.file.Files;
import java.util.Properties;
import java.sql.*;
import java.util.UUID;


public class producerDB extends ProducerSample {
    private Connection conn;
    private int limit = 0; // in case of large amount of records, limit the # of output records
    private KafkaProducer<String, GenericRecord> producer;
    private Schema schema;

    public producerDB(int limit) {
        Properties props = new Properties();
        props.put("user", "postgres");
        props.put("password", "Erichka1");

        producer = ProducerProperties();

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

                    final ProducerRecord<String, GenericRecord> record = new ProducerRecord<String, GenericRecord>(
                            KafkaTopic, UUID.randomUUID().toString().replace("-", ""),avroRecord);



                    producer.send(record, (metadata, exception) -> {
                        if(metadata != null){
                            //System.out.println("CsvData: "+ record.key()+" "+ record.value());
                        }
                        else{
                            System.out.println("Error Sending Csv Record "+ record.value());
                        }
                    });

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
