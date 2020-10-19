
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
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


public class ConsumerSample {
    public static Consumer<Long, String> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // do I need a group ID???
        //props.put("group.id", "test");

        props.put("enable.auto.commit", "false"); // is that valid?
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        props.put("schema.registry.url", "http//127.0.0.1:8081"); //localhost registry
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        String topic = "capitalbikeshare";
        createConsumer().subscribe(Collections.singleton(topic));
        final KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<String, GenericRecord>(props);

        // should we partition our messages? Right now, it's just in batched

        try {
        while (true) {
            ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, GenericRecord> record : records) {
                System.out.printf(String.valueOf(record.offset()), record.key(), record.value());
            }
        }
    } finally {
            consumer.close();
        }
        }
}