
// import all of the needed packages
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.kafka.common.serialization.StringSerializer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Stream;


final class ProducerSample {

    // initialize values of Topic and CSV file
    private static String KafkaBroker = "localhost:9092";
    private static String KafkaTopic = "capitalbikeshare";

    // Should I change Generic record to an Object? IF so, how to?

    private static KafkaProducer<String, GenericRecord> ProducerProperties(){ // added static
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaBroker);
        props.put("group.id", "test");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaCsvProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // String?
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put("acks", "1");
        props.put("retries", "10");
        props.put("schema.registry.url", "http://127.0.0.1:8081"); //localhost registry

        KafkaProducer<String, GenericRecord> producer = new KafkaProducer<>(props);
        return producer;
    }

    // is this necessary?
    // in case broker, topic, and file were not declared
    public static void start() {
        // Avro Schema

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

        // Do the schema parsing
        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(schemaString);

        // establish path to the file

        Path path = Paths.get("C://Users//eerga//Downloads//Fall 2020//Computing for Analytics//Week 4 code//capitalbikeshare.csv");

        Producer producer = ProducerProperties();

        try {
            Files.lines(path).skip(1).forEach(line -> {
                GenericRecord avroRecord = new GenericData.Record(schema);
                String[] fields = line.split(",");
                avroRecord.put("Duration", Integer.parseInt(fields[0])); // do this for every field
                avroRecord.put("Start_date", fields[1]); // do this for every single record
                avroRecord.put("End_date", fields[2]); // do this for every single record
                avroRecord.put("Start_station_number", Integer.parseInt(fields[3])); // do this for every single record
                avroRecord.put("Start_station", fields[4]); // do this for every single record
                avroRecord.put("End_station_number", Integer.parseInt(fields[5])); // do this for every single record
                avroRecord.put("End_station", fields[6]); // do this for every single record
                avroRecord.put("Bike_number", fields[7]); // do this for every single record
                avroRecord.put("Member_type", fields[8]); // do this for every single record

                final ProducerRecord<String, GenericRecord> record = new ProducerRecord<String, GenericRecord>(
                        KafkaTopic, UUID.randomUUID().toString().replace("-", ""),avroRecord);



                producer.send(record, (metadata, exception) -> {
                    if(metadata != null){
                        System.out.println("CsvData: "+ record.key()+" "+ record.value());
                    }
                    else{
                        System.out.println("Error Sending Csv Record "+ record.value());
                    }
                });

            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}