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

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.AbstractCreateResource;
import org.openflexo.foundation.fml.editionaction.EditionAction;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.kafka.KafkaModelSlot;
import org.openflexo.technologyadapter.kafka.KafkaTechnologyAdapter;
import org.openflexo.technologyadapter.kafka.model.KafkaServer;
import org.openflexo.technologyadapter.kafka.rm.KafkaResource;
import org.openflexo.technologyadapter.kafka.rm.KafkaResourceFactory;

/**
 * {@link EditionAction} used to create an empty JDBC resource
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(CreateKafkaResource.CreateKafkaResourceImpl.class)
@XMLElement
@FML("CreateKafkaResource")
public interface CreateKafkaResource extends AbstractCreateResource<KafkaModelSlot, KafkaServer, KafkaTechnologyAdapter> {

	String SERVER = "server";
	String ZOOKEEPER = "zookeeper";

	@Getter(SERVER)
	@XMLAttribute
	DataBinding<String> getServer();

	@Setter(SERVER)
	void setServer(DataBinding<String> zookeeper);

	@Getter(ZOOKEEPER)
	@XMLAttribute
	DataBinding<String> getZookeeper();

	@Setter(ZOOKEEPER)
	void setZookeeper(DataBinding<String> zookeeper);

	abstract class CreateKafkaResourceImpl extends AbstractCreateResourceImpl<KafkaModelSlot, KafkaServer, KafkaTechnologyAdapter>
			implements CreateKafkaResource {

		private static final Logger logger = Logger.getLogger(CreateKafkaResourceImpl.class.getPackage().getName());

		private DataBinding<String> server;
		private DataBinding<String> zookeeper;

		@Override
		public Type getAssignableType() {
			return KafkaServer.class;
		}

		@Override
		public DataBinding<String> getServer() {
			if (server == null) {
				server = new DataBinding<>(this, String.class, DataBinding.BindingDefinitionType.GET);
				server.setBindingName(SERVER);
			}
			return server;
		}

		@Override
		public void setServer(DataBinding<String> server) {
			if (server != null) {
				server.setOwner(this);
				server.setDeclaredType(String.class);
				server.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				server.setBindingName(SERVER);
			}
			this.server = server;
		}

		@Override
		public DataBinding<String> getZookeeper() {
			if (zookeeper == null) {
				zookeeper = new DataBinding<>(this, String.class, DataBinding.BindingDefinitionType.GET);
				zookeeper.setBindingName(ZOOKEEPER);
			}
			return zookeeper;
		}

		@Override
		public void setZookeeper(DataBinding<String> zookeeper) {
			if (zookeeper != null) {
				zookeeper.setOwner(this);
				zookeeper.setDeclaredType(String.class);
				zookeeper.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				zookeeper.setBindingName(SERVER);
			}
			this.zookeeper = zookeeper;
		}

		@Override
		public KafkaServer execute(RunTimeEvaluationContext evaluationContext) throws FlexoException {
			try {
				String resourceName = getResourceName(evaluationContext);
				String resourceURI = getResourceURI(evaluationContext);
				FlexoResourceCenter<?> rc = getResourceCenter(evaluationContext);
				KafkaTechnologyAdapter technologyAdapter = getServiceManager().getTechnologyAdapterService()
						.getTechnologyAdapter(KafkaTechnologyAdapter.class);

				KafkaResource newResource = createResource(technologyAdapter, KafkaResourceFactory.class, rc, resourceName, resourceURI,
						getRelativePath(), ".kafka", true);
				KafkaServer server = newResource.getResourceData();
				server.setServer(getServer().getBindingValue(evaluationContext));
				server.setZookeeper(getZookeeper().getBindingValue(evaluationContext));

				return server;
			} catch (ModelDefinitionException | FileNotFoundException | ResourceLoadingCancelledException | TypeMismatchException
					| InvocationTargetException | NullReferenceException e) {
				throw new FlexoException(e);
			}

		}
	}
}
