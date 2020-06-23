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

import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.kafka.KafkaTechnologyAdapter;
import org.openflexo.technologyadapter.kafka.model.KafkaServer.KafkaServerImpl;
import org.openflexo.technologyadapter.kafka.rm.KafkaResource;

/**
 * Configuration to a Kafka service
 */
@ModelEntity
@XMLElement
@ImplementationClass(KafkaServerImpl.class)
@Imports({ @Import(KafkaListener.class) })
public interface KafkaServer extends TechnologyObject<KafkaTechnologyAdapter>, ResourceData<KafkaServer> {

	String SERVER_KEY = "server";
	String ZOOKEEPER_KEY = "zookeeper";
	String LISTENERS_KEY = "listener";

	@Getter(SERVER_KEY)
	@XMLAttribute
	String getServer();

	@Setter(SERVER_KEY)
	void setServer(String server);

	@Getter(ZOOKEEPER_KEY)
	@XMLAttribute
	String getZookeeper();

	@Setter(ZOOKEEPER_KEY)
	void setZookeeper(String zookeeper);

	KafkaProducer<String, String> getProducer();

	@Getter(value = LISTENERS_KEY, cardinality = Getter.Cardinality.LIST)
	List<KafkaListener> getListeners();

	@Adder(LISTENERS_KEY)
	void addListener(KafkaListener listener);

	@Remover(LISTENERS_KEY)
	void removeListener(KafkaListener listener);

	Properties getConsumerProperties();

	@Override
	KafkaResource getResource();

	abstract class KafkaServerImpl extends FlexoObjectImpl implements KafkaServer {

		KafkaProducer producer = null;

		private void fillCommonProperties(Properties properties) {
			properties.put("bootstrap.servers", getServer());
		}

		@Override
		public Properties getConsumerProperties() {
			Properties properties = new Properties();
			fillCommonProperties(properties);
			// TODO what to do here ?
			properties.put("group.id", "test");
			properties.put("enable.auto.commit", "true");
			properties.put("auto.commit.interval.ms", "1000");
			properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
			properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
			return properties;
		}

		public Properties getProducerProperties() {
			Properties properties = new Properties();
			fillCommonProperties(properties);
			properties.put("acks", "all");
			properties.put("retries", 0);
			properties.put("batch.size", 16384);
			properties.put("linger.ms", 1);
			properties.put("buffer.memory", 33554432);
			properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			return properties;
		}

		@Override
		public KafkaProducer getProducer() {
			if (producer == null) {
				synchronized (this) {
					if (producer == null) {
						producer = new KafkaProducer(getProducerProperties());
					}
				}
			}
			return producer;
		}

		@Override
		public KafkaTechnologyAdapter getTechnologyAdapter() {
			FlexoServiceManager serviceManager = getServiceManager();
			if (serviceManager != null) {
				return serviceManager.getService(TechnologyAdapterService.class).getTechnologyAdapter(KafkaTechnologyAdapter.class);
			}
			return null;
		}
	}
}
