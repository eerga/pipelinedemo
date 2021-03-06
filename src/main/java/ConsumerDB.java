import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.sql.*;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;


public class ConsumerDB extends ConsumerSample {
    private Connection conn;
    private PreparedStatement insertStatement = null;
    private KafkaConsumer<String, GenericRecord> consumer;


    public ConsumerDB() {
        Properties props = new Properties(); // Postgres properties for connecting with DB
        props.put("user", "postgres");
        props.put("password", "Erichka1");

        Properties pros = new Properties(); // Kafka Consumer Properties pros
        pros.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Main.BROKER);
        pros.put("group.id", "group8");
        pros.put("enable.auto.commit", "false"); // is that valid?
        pros.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        pros.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        pros.put("schema.registry.url", Main.SCHEMA_REGISTRY);
        pros.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        String topic = "capitalbikeshare"; // Kafka Topic

        // create consumer with Properties props
        consumer = new KafkaConsumer<>(pros);
        // subscribe consumer to a kafka topic
        consumer.subscribe(Collections.singleton(topic));

        // creating record insertion query
        try {
            conn = DriverManager.getConnection(Main.DB_CONNECTION, props);
            String query = "INSERT INTO Output(Duration, Start_date,End_date," +
                    "Start_station_number,Start_station,End_station_number," +
                    "End_station,Bike_number,Member_type) VALUES (?,?,?,?,?,?,?,?,?);";
            insertStatement = conn.prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void start() {

        //* records are consumer from a kafka stream and line-by-line and inserted into Output table in Postgres
        // by calling the onMessage method*//
        try {
            if (consumer != null) {
                while (true) {
                    ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, GenericRecord> record : records) {
                        onMessage(record.offset(), record.key(), record.value());


                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (consumer != null) {
                consumer.close();
            }
        }
    }

    public void onMessage(long offset, String key, GenericRecord value) {
        if (conn != null) {
            try {
                insertStatement.setInt(1, (int) value.get("Duration"));
                insertStatement.setString(2, value.get("Start_date").toString());
                insertStatement.setString(3, value.get("End_date").toString());
                insertStatement.setInt(4, (int) value.get("Start_station_number"));
                insertStatement.setString(5, value.get("Start_station").toString());
                insertStatement.setInt(6, (int) value.get("End_station_number"));
                insertStatement.setString(7, value.get("End_station").toString());
                insertStatement.setString(8, value.get("Bike_number").toString());
                insertStatement.setString(9, value.get("Member_type").toString());

                insertStatement.executeUpdate();

                System.out.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                        value.get("Duration").toString(),
                        value.get("Start_date").toString(),
                        value.get("End_date").toString(),
                        value.get("Start_station_number").toString(),
                        value.get("Start_station").toString(),
                        value.get("End_station_number").toString(),
                        value.get("End_station").toString(),
                        value.get("Bike_number").toString(),
                        value.get("Member_type").toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

