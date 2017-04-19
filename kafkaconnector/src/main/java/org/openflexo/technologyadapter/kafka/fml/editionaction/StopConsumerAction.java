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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.TechnologySpecificAction;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.kafka.KafkaModelSlot;
import org.openflexo.technologyadapter.kafka.model.KafkaListener;

/**
 * Stops a Kafka consumer action
 */
@ModelEntity
@XMLElement
@ImplementationClass(StopConsumerAction.StopConsumerActionImpl.class)
@FML("StopConsumer")
public interface StopConsumerAction extends TechnologySpecificAction<KafkaModelSlot, KafkaListener, Boolean> {

	abstract class StopConsumerActionImpl extends TechnologySpecificActionImpl<KafkaModelSlot, KafkaListener, Boolean> implements
			StopConsumerAction {

		private static final Logger logger = Logger.getLogger(StopConsumerAction.class.getPackage().getName());

		@Override
		public Boolean execute(RunTimeEvaluationContext evaluationContext) {

			DataBinding<KafkaListener> receiver = getReceiver();
			if (receiver == null) {
				logger.warning("Receiver expression is null.");
				return false;
			}

			try {
				KafkaListener listener = receiver.getBindingValue(evaluationContext);
				if (listener == null) {
					logger.log(Level.WARNING, "Receiver expression '" + getReceiver() + "'");
					return false;
				}
				listener.stop();
				return true;

			} catch (TypeMismatchException | NullReferenceException | InvocationTargetException e) {
				logger.log(Level.WARNING, "Can't stop consumer '" + getReceiver() + "'", e);
				return false;
			}

		}


		@Override
		public Type getAssignableType() {
			return KafkaListener.class;
		}

	}
}
