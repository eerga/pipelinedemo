public class Main {

    // Kafka
    public static final String BROKER = "broker:29092";
    public static final String SCHEMA_REGISTRY = "http://schema-registry:8081";
    public static final String TOPIC = "capitalbikeshare";

    //Data IO
    public static final String OUTFILE = "";
    public static final String INFILE = "";
    public static final String DB_CONNECTION = "jdbc:postgresql://postgres:5432/postgres";

    //Parameter
    public static final String CONSUME_CSV = "consume-csv";
    public static final String CONSUME_DB = "consume-db";
    public static final String PRODUCE_CSV = "produce-csv";
    public static final String PRODUCE_DB = "produce-db";

    static void runConsumer() {
        
    }

    public static void main(String[] args) {

        if (args[0].equalsIgnoreCase(CONSUME_CSV)) {
            ConsumerSample consumer = new ConsumerSample ();
            consumer.start();
        }
        else if (args[0].equalsIgnoreCase(PRODUCE_CSV)){
            ProducerSample producer = new ProducerSample ();
            producer.start();
        }
        else {
            System.out.println("Invalid argument");
        }
    }
}
