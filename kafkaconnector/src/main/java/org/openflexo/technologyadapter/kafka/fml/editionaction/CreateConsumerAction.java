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

package org.openflexo.technologyadapter.kafka.fml.editionaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.fml.ActionScheme;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.TechnologySpecificActionDefiningReceiver;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Finder;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.technologyadapter.kafka.KafkaModelSlot;
import org.openflexo.technologyadapter.kafka.model.KafkaListener;
import org.openflexo.technologyadapter.kafka.model.KafkaServer;
import org.openflexo.technologyadapter.kafka.model.KafkaTopic;

/**
 * Creates a Kafka consumer action
 */
@ModelEntity
@XMLElement
@ImplementationClass(CreateConsumerAction.CreateConsumerActionImpl.class)
@FML("CreateConsumer")
public interface CreateConsumerAction extends TechnologySpecificActionDefiningReceiver<KafkaModelSlot, KafkaServer, KafkaListener> {

	@PropertyIdentifier(type = String.class)
	String TOPICS = "topics";

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

	@Getter(ACTION_NAME)
	@XMLAttribute
	String getActionName();

	@Setter(ACTION_NAME)
	void setActionName(String action);

	void addNewTopic();

	ActionScheme getAction();

	void setAction(ActionScheme action);

	abstract class CreateConsumerActionImpl extends TechnologySpecificActionDefiningReceiverImpl<KafkaModelSlot, KafkaServer, KafkaListener>
			implements CreateConsumerAction {

		private static final Logger logger = Logger.getLogger(CreateConsumerAction.class.getPackage().getName());

		private ActionScheme action;

		@Override
		public void addNewTopic() {
			KafkaTopic kafkaTopic = getFMLModelFactory().newInstance(KafkaTopic.class);
			kafkaTopic.setName("topic");
			addTopic(kafkaTopic);
		}

		@Override
		public ActionScheme getAction() {
			String actionName = getActionName();
			if (action == null && actionName != null) {
				for (ActionScheme actionScheme : getFlexoConcept().getAccessibleActionSchemes()) {
					if (Objects.equals(actionName, actionScheme.getName())) {
						action = actionScheme;
					}
				}
			}
			return action;
		}

		@Override
		public void setAction(ActionScheme action) {
			ActionScheme oldValue = getAction();
			if (action != oldValue) {
				this.action = action;
				setActionName(action != null ? action.getName() : null);
				getPropertyChangeSupport().firePropertyChange("action", oldValue, action);
			}
		}

		@Override
		public KafkaListener execute(RunTimeEvaluationContext evaluationContext) {
			DataBinding<KafkaServer> receiver = getReceiver();
			if (receiver == null) {
				logger.warning("Receiver is null.");
				return null;
			}

			KafkaServer kafkaServer;
			try {
				kafkaServer = receiver.getBindingValue(evaluationContext);
				if (kafkaServer == null) {
					logger.log(Level.WARNING, "Kafka expression '" + getReceiver() + "' is null");
					return null;
				}

				KafkaListener listener = kafkaServer.getResource().getFactory().makeNewListener(kafkaServer);
				listener.setServer(kafkaServer);
				listener.setTopics(getTopics());
				listener.setActionName(getActionName());
				return listener;

			} catch (TypeMismatchException | NullReferenceException | InvocationTargetException e) {
				logger.log(Level.WARNING, "Can't create  on '" + getTopics() + "'", e);
				return null;
			}

		}

		@Override
		public Type getAssignableType() {
			return KafkaListener.class;
		}

	}
}
