
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;

import org.apache.avro.generic.GenericRecord;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;


//private long lastMessage;

public class ConsumerSample extends Thread {
    private KafkaConsumer<String, GenericRecord> consumer;

    public ConsumerSample() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Main.BROKER);
        props.put("group.id", "group1");
        props.put("enable.auto.commit", "false"); // is that valid?
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        props.put("schema.registry.url", "http://127.0.0.1:8081"); //localhost registry
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        String topic = "capitalbikeshare";
        // create consumer with properties props similar to kafka producer
        consumer = new KafkaConsumer<>(props);
        // subscribe consumer to a kafka topic
        consumer.subscribe(Collections.singleton(topic));

    }

    // records are consumer from a kafka stream and line-by-line and printed out to a user
    public void start() {
        try {
            if (consumer == null) currentThread().interrupt();
            else {
                while (true) {
                    ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, GenericRecord> record : records) {
                        System.out.println(record.value());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (consumer == null) {
                currentThread().interrupt();
            } else consumer.close();
        }
    }
}


