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

package org.openflexo.technologyadapter.kafka.fml;

import java.util.List;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.ActorReference;
import org.openflexo.foundation.fml.rt.ModelSlotInstance;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.kafka.fml.KafkaListenerActorReference.KafkaListenerActorReferenceImpl;
import org.openflexo.technologyadapter.kafka.model.KafkaListener;
import org.openflexo.technologyadapter.kafka.model.KafkaServer;
import org.openflexo.technologyadapter.kafka.model.KafkaServerFactory;
import org.openflexo.technologyadapter.kafka.model.KafkaTopic;

/**
 * Actor reference for a KafkaListener
 */
@ModelEntity
@XMLElement
@ImplementationClass(KafkaListenerActorReferenceImpl.class)
@FML("KafkaListenerActorReference")
public interface KafkaListenerActorReference extends ActorReference<KafkaListener> {

	@PropertyIdentifier(type = String.class)
	String TOPICS = "topics";

	@PropertyIdentifier(type = String.class)
	String STARTED = "started";

	@PropertyIdentifier(type = String.class)
	String ACTION_NAME = "actionName";

	@Getter(value = TOPICS, cardinality = Cardinality.LIST)
	@Embedded
	@XMLElement
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
	void setActionName(String action);

	@Getter(value = STARTED, defaultValue = "true")
	boolean isStarted();

	@Setter(STARTED)
	void setStarted(boolean started);

	abstract class KafkaListenerActorReferenceImpl extends ActorReferenceImpl<KafkaListener> implements KafkaListenerActorReference {

		private KafkaListener listener = null;

		private KafkaServer getServer() {
			ModelSlotInstance<?, ?> modelSlotInstance = getModelSlotInstance();
			if (modelSlotInstance != null) {
				ResourceData<?> resourceData = modelSlotInstance.getAccessedResourceData();
				if (resourceData instanceof KafkaServer) {
					return (KafkaServer) resourceData;
				}
			}
			return null;
		}

		@Override
		public KafkaListener getModellingElement() {
			if (listener == null) {
				KafkaServer server = getServer();
				if (server != null) {
					KafkaServerFactory factory = server.getResource().getFactory();
					KafkaListener listener = factory.makeNewListener();
					listener.setTopics(getTopics());
					listener.setActionName(getActionName());
					/* TODO Search linked flexo concept instance to start listening if needed
					if (isStarted()) {
						listener.start();
					}
					*/
					this.listener = listener;
				}
			}
			return listener;
		}

		@Override
		public void setModellingElement(KafkaListener object) {
			if (listener != object) {
				KafkaListener oldValue = listener;
				listener = object;
				getPropertyChangeSupport().firePropertyChange(MODELLING_ELEMENT_KEY, oldValue, object);
			}
		}
	}

}
