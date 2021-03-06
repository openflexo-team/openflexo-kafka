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
 * (c) Copyright 2013- Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.technologyadapter.kafka.rm;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.openflexo.foundation.resource.PamelaResource;
import org.openflexo.foundation.resource.PamelaResourceImpl;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.kafka.KafkaTechnologyAdapter;
import org.openflexo.technologyadapter.kafka.KafkaTechnologyContextManager;
import org.openflexo.technologyadapter.kafka.model.KafkaFactory;
import org.openflexo.technologyadapter.kafka.model.KafkaListener;
import org.openflexo.technologyadapter.kafka.model.KafkaServer;
import org.openflexo.technologyadapter.kafka.rm.KafkaResource.KafkaResourceImpl;

@ModelEntity
@XMLElement
@ImplementationClass(KafkaResourceImpl.class)
public interface KafkaResource
		extends PamelaResource<KafkaServer, KafkaFactory<?>>, TechnologyAdapterResource<KafkaServer, KafkaTechnologyAdapter> {

	String TECHNOLOGY_CONTEXT_MANAGER = "technologyContextManager";

	@Override
	@Getter(value = TECHNOLOGY_CONTEXT_MANAGER, ignoreType = true)
	KafkaTechnologyContextManager getTechnologyContextManager();

	@Setter(TECHNOLOGY_CONTEXT_MANAGER)
	void setTechnologyContextManager(KafkaTechnologyContextManager contextManager);

	abstract class KafkaResourceImpl extends PamelaResourceImpl<KafkaServer, KafkaFactory<?>> implements KafkaResource {

		@Override
		public KafkaTechnologyAdapter getTechnologyAdapter() {
			if (getServiceManager() != null) {
				return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(KafkaTechnologyAdapter.class);
			}
			return null;
		}

		@Override
		public Class<KafkaServer> getResourceDataClass() {
			return KafkaServer.class;
		}

		@Override
		public void unloadResourceData(boolean deleteResourceData) {
			KafkaServer model = getLoadedResourceData();
			if (model != null) {
				try (KafkaProducer<String, String> producer = model.getProducer()) {
					for (KafkaListener listener : model.getListeners()) {
						listener.stop();
					}
				}
			}
			super.unloadResourceData(deleteResourceData);
		}
	}
}
