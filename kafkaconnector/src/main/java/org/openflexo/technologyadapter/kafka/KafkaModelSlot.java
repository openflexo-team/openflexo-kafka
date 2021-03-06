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

package org.openflexo.technologyadapter.kafka;

import java.lang.reflect.Type;

import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.annotations.DeclareActorReferences;
import org.openflexo.foundation.fml.annotations.DeclareEditionActions;
import org.openflexo.foundation.fml.annotations.DeclareFlexoBehaviours;
import org.openflexo.foundation.fml.annotations.DeclareFlexoRoles;
import org.openflexo.foundation.technologyadapter.FreeModelSlot;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.kafka.KafkaModelSlot.KafkaModelSlotImpl;
import org.openflexo.technologyadapter.kafka.fml.KafkaListenerActorReference;
import org.openflexo.technologyadapter.kafka.fml.KafkaListenerRole;
import org.openflexo.technologyadapter.kafka.fml.editionaction.CreateConsumerAction;
import org.openflexo.technologyadapter.kafka.fml.editionaction.CreateKafkaResource;
import org.openflexo.technologyadapter.kafka.fml.editionaction.SendRecordAction;
import org.openflexo.technologyadapter.kafka.fml.editionaction.StartConsumerAction;
import org.openflexo.technologyadapter.kafka.fml.editionaction.StopConsumerAction;
import org.openflexo.technologyadapter.kafka.model.KafkaServer;

/**
 * Kafka consumer model slot.
 */
@ModelEntity
@XMLElement
@ImplementationClass(KafkaModelSlotImpl.class)
@DeclareEditionActions({ CreateKafkaResource.class, SendRecordAction.class, CreateConsumerAction.class, StartConsumerAction.class,
		StopConsumerAction.class })
@DeclareFlexoBehaviours({})
@DeclareActorReferences({ KafkaListenerActorReference.class })
@DeclareFlexoRoles({ KafkaListenerRole.class })
public interface KafkaModelSlot extends FreeModelSlot<KafkaServer> {

	@PropertyIdentifier(type = String.class)
	String SERVER_KEY = "server";

	@PropertyIdentifier(type = String.class)
	String ZOOKEEPER_KEY = "zookeeper";

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

	@Override
	KafkaTechnologyAdapter getModelSlotTechnologyAdapter();

	abstract class KafkaModelSlotImpl extends FreeModelSlotImpl<KafkaServer> implements KafkaModelSlot {

		@Override
		public Class<KafkaTechnologyAdapter> getTechnologyAdapterClass() {
			return KafkaTechnologyAdapter.class;
		}

		@Override
		public <PR extends FlexoRole<?>> String defaultFlexoRoleName(Class<PR> patternRoleClass) {
			return "";
		}

		@Override
		public Type getType() {
			return KafkaServer.class;
		}

		@Override
		public KafkaTechnologyAdapter getModelSlotTechnologyAdapter() {
			return (KafkaTechnologyAdapter) super.getModelSlotTechnologyAdapter();
		}

	}
}
