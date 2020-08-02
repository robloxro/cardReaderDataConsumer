package com.robloxro.cardReader.dataConsumer;

import com.robloxro.cardReader.dataConsumer.flyway.FlywayInitialiser;
import com.robloxro.cardReader.dataConsumer.kafka.KafkaConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

@SpringBootApplication
public class DataConsumerApplication {

	public static void main(String[] args) {

		SpringApplication.run(DataConsumerApplication.class, args);
		FlywayInitialiser.runMigrations();
		consumeMessages();

	}

	public static void consumeMessages() {

		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.KAFKA_BROKERS);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstants.GROUP_ID_CONFIG);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, KafkaConstants.MAX_POLL_RECORDS);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, KafkaConstants.OFFSET_RESET_EARLIER);
		Consumer<Long, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList(KafkaConstants.TOPIC_NAME));


		int noMessageFound = 0;
		while (true) {
			ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);
			// 1000 is the time in milliseconds consumer will wait if no record is found at broker.
			if (consumerRecords.count() == 0) {
				noMessageFound++;
				if (noMessageFound > KafkaConstants.MAX_NO_MESSAGE_FOUND_COUNT)
					// If no message found count is reached to threshold exit loop.
					break;
				else
					continue;
			}
			//print each record.
			consumerRecords.forEach(record -> {
				System.out.println("Record Key " + record.key());
				System.out.println("Record value " + record.value());
				System.out.println("Record partition " + record.partition());
				System.out.println("Record offset " + record.offset());
			});
			// commits the offset of record to broker.
			consumer.commitAsync();
		}
		consumer.close();
	}
}
