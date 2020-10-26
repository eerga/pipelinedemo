public class Main {

    // Kafka
    public static final String BROKER = "broker:29092";
    public static final String SCHEMA_REGISTRY = "http://schema-registry:8081";
    public static final String TOPIC = "capitalbikeshare";

    //Data
    // static final String OUTFILE = "output_file.csv";
    public static final String INFILE = "capitalbikeshare.csv";
    public static final String DB_CONNECTION = "jdbc:postgresql://localhost:5432/postgres";

    //Parameters
    public static final String CONSUME_CSV = "consume-csv";
    public static final String CONSUME_DB = "consume-db";
    public static final String PRODUCE_CSV = "produce-csv";
    public static final String PRODUCE_DB = "produce-db";
    public static final int limit = 1000;

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
        else if (args[0].equalsIgnoreCase(PRODUCE_DB)){
            producerDB producer = new producerDB(limit);
            producer.run_query();

        }
        else if (args[0].equalsIgnoreCase(CONSUME_DB)){
            ConsumerDB consumer = new ConsumerDB(limit);
            consumer.start();
        }
        else {
            System.out.println("Invalid argument");
        }
    }
}
