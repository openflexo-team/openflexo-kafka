/*
 * Copyright (c) 2013-2017, Openflexo
 *
 * This file is part of Flexo-foundation, a component of the software infrastructure
 * developed at Openflexo.
 *
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either
 * version 1.1 of the License, or any later version ), which is available at
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 *
 * You can redistribute it and/or modify under the terms of either of these licenses
 *
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *           Additional permission under GNU GPL version 3 section 7
 *           If you modify this Program, or any covered work, by linking or
 *           combining it with software containing parts covered by the terms
 *           of EPL 1.0, the licensors of this Program grant you additional permission
 *           to convey the resulting work.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See http://www.openflexo.org/license.html for details.
 *
 *
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 *
 */
package org.openflexo.technologyadapter.kafka.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.Test;

/**
 * The Kafka clients accept the properties that the adapter gives them. No broker is needed: a client is created without connecting, and
 * a broker that does not answer only makes a call time out. This protects the move to a new version of kafka-clients (BUILD-F-7).
 */
public class TestKafkaClientProperties {

	// Nothing listens on this port
	private static final String NO_BROKER = "localhost:1";

	@Test
	public void producerIsCreatedFromTheProperties() {
		Properties properties = KafkaClientProperties.producerProperties(NO_BROKER);
		assertEquals(NO_BROKER, properties.get("bootstrap.servers"));
		try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
			assertNotNull(producer);
		}
	}

	@Test
	public void consumerIsCreatedFromTheProperties() {
		Properties properties = KafkaClientProperties.consumerProperties(NO_BROKER);
		assertEquals(NO_BROKER, properties.get("bootstrap.servers"));
		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties)) {
			assertNotNull(consumer);
			consumer.subscribe(Collections.singletonList("topic"));
			// What the listener does in a loop: it must come back without a broker, with no record
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(20));
			assertTrue(records.isEmpty());
		}
	}
}
