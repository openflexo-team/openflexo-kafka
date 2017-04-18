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
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.TechnologySpecificAction;
import org.openflexo.foundation.fml.rt.ModelSlotInstance;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.kafka.KafkaProducerModelSlot;
import org.openflexo.technologyadapter.kafka.fml.editionaction.SendRecordAction.SendRecordActionImpl;
import org.openflexo.technologyadapter.kafka.model.KafkaServer;

/**
 * Send record through Kafka producer model slot action.
 */
@ModelEntity
@XMLElement
@ImplementationClass(SendRecordActionImpl.class)
@FML("SendRecord")
public interface SendRecordAction extends TechnologySpecificAction<KafkaProducerModelSlot, KafkaServer, String> {

	@PropertyIdentifier(type = String.class)
	String TOPIC_KEY = "topic";

	@PropertyIdentifier(type = DataBinding.class)
	String RECORD_KEY = "record";

	@Getter(TOPIC_KEY)
	@XMLAttribute
	String getTopic();

	@Setter(TOPIC_KEY)
	void setTopic(String topic);

	@Getter(RECORD_KEY)
	@XMLAttribute
	DataBinding<String> getRecord();

	@Setter(RECORD_KEY)
	void setRecord(DataBinding<String> record);

	abstract class SendRecordActionImpl extends TechnologySpecificActionImpl<KafkaProducerModelSlot, KafkaServer, String> implements SendRecordAction {

		private static final Logger logger = Logger.getLogger(SendRecordAction.class.getPackage().getName());

		private DataBinding<String> record;

		@Override
		public String execute(RunTimeEvaluationContext evaluationContext) {

			ModelSlotInstance<KafkaProducerModelSlot, KafkaServer> modelSlotInstance = getModelSlotInstance(evaluationContext);
			if (modelSlotInstance == null) {
				logger.warning("Could not access model slot instance. Abort.");
				return null;
			}
			KafkaServer kafkaServer = modelSlotInstance.getAccessedResourceData();
			if (kafkaServer == null) {
				logger.warning("Could not access model addressed by model slot instance. Abort.");
				return null;
			}

			try {
				String valueToSend = getRecord().getBindingValue(evaluationContext);
				Future<RecordMetadata> sent = kafkaServer.getProducer().send(new ProducerRecord<>(getTopic(), valueToSend));
				// what to do with the result
				return null;

			} catch (TypeMismatchException | NullReferenceException | InvocationTargetException e) {
				logger.log(Level.WARNING, "Can't send record '" + getRecord() + "'", e);
				return null;
			}

		}


		@Override
		public DataBinding<String> getRecord() {
			if (record == null) {
				record = new DataBinding<>(this, String.class, DataBinding.BindingDefinitionType.GET);
				record.setBindingName("record");
			}
			return record;
		}

		@Override
		public void setRecord(DataBinding<String> record) {
			if (record != null) {
				record.setOwner(this);
				record.setDeclaredType(String.class);
				record.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				record.setBindingName("record");
			}
			this.record = record;
		}

		@Override
		public Type getAssignableType() {
			return String.class;
		}

	}
}
