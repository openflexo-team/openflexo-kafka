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
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.TechnologySpecificAction;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.kafka.KafkaModelSlot;
import org.openflexo.technologyadapter.kafka.model.KafkaListener;

/**
 * Starts a Kafka consumer action
 */
@ModelEntity
@XMLElement
@ImplementationClass(StartConsumerAction.StartConsumerActionImpl.class)
@FML("StartConsumer")
public interface StartConsumerAction extends TechnologySpecificAction<KafkaModelSlot, KafkaListener, Boolean> {

	abstract class StartConsumerActionImpl extends TechnologySpecificActionImpl<KafkaModelSlot, KafkaListener, Boolean>
			implements StartConsumerAction {

		private static final Logger logger = Logger.getLogger(StartConsumerAction.class.getPackage().getName());

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
					logger.log(Level.WARNING, "Receiver expression '" + getReceiver() + "' is null");
					return false;
				}

				FlexoConceptInstance instance = evaluationContext.getFlexoConceptInstance();
				FlexoEditor editor = null;
				if (instance.getVirtualModelInstance().getResourceCenter() instanceof FlexoProject) {
					FlexoProject project = (FlexoProject) instance.getVirtualModelInstance().getResourceCenter();
					editor = getServiceManager().getProjectLoaderService().getEditorForProject(project);
				}
				listener.start(instance, editor);
				return true;

			} catch (TypeMismatchException | NullReferenceException | InvocationTargetException e) {
				logger.log(Level.WARNING, "Can't start consumer '" + getReceiver() + "'", e);
				return false;
			}
		}

		@Override
		public Type getAssignableType() {
			return Boolean.class;
		}

	}
}
