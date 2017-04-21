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
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.InnerResourceData;
import org.openflexo.foundation.fml.ActionScheme;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.action.ActionSchemeAction;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.kafka.KafkaTechnologyAdapter;

/**
 * An instance of this interface links a Kafka Consumer to a FlexoBehavior to execute with it
 */
@ModelEntity @XMLElement
@ImplementationClass(KafkaListener.KafkaListenerImpl.class)
public interface KafkaListener extends TechnologyObject<KafkaTechnologyAdapter>, InnerResourceData<KafkaServer> {

	String SERVER = "server";
	String TOPICS = "topics";
	String ACTION_NAME = "actionName";

	@Getter(SERVER) @XMLAttribute
	KafkaServer getServer();

	@Setter(SERVER)
	void setServer(KafkaServer server);

	@Getter(value = TOPICS, cardinality = Cardinality.LIST)
	@Embedded @XMLElement
	List<KafkaTopic> getTopics();

	@Finder(collection = TOPICS)
	KafkaTopic findTopic(String name);

	@Adder(TOPICS)
	void addTopic(KafkaTopic topic);

	@Remover(TOPICS)
	void removeTopic(KafkaTopic topic);

	@Setter(TOPICS)
	void setTopics(List<KafkaTopic> topics);

	@Getter(ACTION_NAME) @XMLAttribute
	String getActionName();

	@Setter(ACTION_NAME)
	void setActionName(String name);

	boolean isStarted();

	void start(FlexoConceptInstance instance);

	void stop();

	abstract class KafkaListenerImpl extends FlexoObjectImpl implements KafkaListener {

		private static final Logger logger = FlexoLogger.getLogger(KafkaListener.class.getPackage().toString());

		private ThreadPoolExecutor executor = new ThreadPoolExecutor(
				1, 1, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<>()
		);

		private KafkaConsumer consumer;
		private FlexoConceptInstance instance;
		private ActionScheme actionScheme;
		private ActionSchemeAction action;

		@Override
		public KafkaServer getResourceData() {
			return getServer();
		}

		@Override
		public KafkaTechnologyAdapter getTechnologyAdapter() {
			FlexoServiceManager serviceManager = getServiceManager();
			if (serviceManager != null) {
				return serviceManager.getService(TechnologyAdapterService.class).getTechnologyAdapter(KafkaTechnologyAdapter.class);
			}
			return null;
		}

		public synchronized boolean isStarted() {
			return consumer != null;
		}

		private ActionScheme getActionScheme(FlexoConceptInstance instance) {
			FlexoConcept flexoConcept = instance.getFlexoConcept();
			List<ActionScheme> actionSchemes = flexoConcept.getAccessibleActionSchemes();
			for (ActionScheme actionScheme : actionSchemes) {
				if (Objects.equals(getActionName(), actionScheme.getName())) {
					return actionScheme;
				}
			}
			return null;
		}

		@Override
		public synchronized void start(FlexoConceptInstance instance) {
			boolean emptyTopics = getTopics().isEmpty();
			if (!emptyTopics && instance != null) {
				ActionScheme actionScheme = getActionScheme(instance);
				if (actionScheme != null) {
					if (consumer == null) {
						this.instance = instance;
						this.actionScheme = actionScheme;
						// TODO Find a way to get the editor here to allow logging
						this.action = actionScheme.getActionFactory(instance).makeNewAction(instance, null, null);

						consumer = new KafkaConsumer(getServer().getConsumerProperties());
						List<String> topicNames = getTopics().stream().map((t) -> t.getName()).collect(Collectors.toList());
						consumer.subscribe(topicNames);
						executor.execute(this::pollRecords);
					}
				} else {
					logger.warning("Can't listen with no action scheme");
				}
			} else {
				if (emptyTopics) { logger.warning("Can't listen with no topic"); }
				if (instance == null) { logger.warning("Can't listen with no instance"); }
			}
		}

		private void pollRecords() {
			while (consumer != null ) {
				Iterable<ConsumerRecord<String, String>> records = poll();
				if (records == null) return;

				for (ConsumerRecord<String, String> record : records) {
					System.out.println("Record " + record.key() + " -> " + record.value());
					action.doAction();
				}
			}
		}

		private synchronized Iterable<ConsumerRecord<String, String>> poll() {
			return consumer != null ? consumer.poll(20) : null;
		}

		@Override
		public synchronized void stop() {
			if (consumer != null) {
				consumer.close();

				consumer = null;
				instance = null;
				actionScheme = null;
			}
		}
	}
}
