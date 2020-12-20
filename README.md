# pipelinedemo

In the era of Big Data, real-time analysis became more prominent over the years, especially for activities such as monitoring consumer behavior, 
uncovering product insights, and anomaly detection. Performing data analysis on a classic SQL database is much easier than on Kafka streams. 
The goal of the final product is to build a translation engine from SQL to KSQL to enable analysts to analyze real-time streaming data by writing
SQL queries against a Kafka stream and creating real-time Kafka tables. 

Prior to trying to replicate the project, please unzip the csv file in the same directory.

The first part of the project was to familiarize ourselves with Apache Kafka technology by learning how to build two types of simple Pub/Sub pipeline:
1). First pipeline involved creating a producer that would read a csv file and serialize each message (row in a csv file table), using Avro serialization 
and push the serialized message onto Kafka Streams.Then, build a Kafka consumer that would consume records from Kafka stream by deserializing the message 
and outputting the cloned csv output file into a designated place. 

2). Second pipeline involved a task more relevant to reality, i.e. cloning a KSQL table into a RDBMS. The way this was accomplished is by copying a csv file
of interest into an input Postgres table, thus creating a relational DB. To connect the Postgres DB to the Main.java class, a JDBC connector was installed
and postgres reader was installed on the pom.xml dependency (org.postgresql). Then, producerDB.java class was run, by editing the configuration of the Main.java
file and setting the program argument to "produce-db". After the queried and serialized message was pushed onto Kafka streams, we create an empty output table 
in Postgres and run ConsumerDB.java class with a  similar setup as producer, changing the program argument to "consume-db". To compare the input and output tables, 
we count the records from each table to see if any records were lost or if there was any record replication. 



