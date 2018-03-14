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

package org.openflexo.technologyadapter.kafka.model.action;

import java.io.FileNotFoundException;
import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.kafka.KafkaTechnologyAdapter;
import org.openflexo.technologyadapter.kafka.model.KafkaServer;
import org.openflexo.technologyadapter.kafka.rm.KafkaResource;
import org.openflexo.technologyadapter.kafka.rm.KafkaResourceFactory;

public class CreateKafkaServer extends FlexoAction<CreateKafkaServer, RepositoryFolder<KafkaResource, ?>, FlexoObject> {

	public static FlexoActionFactory<CreateKafkaServer, RepositoryFolder<KafkaResource, ?>, FlexoObject> actionType = new FlexoActionFactory<CreateKafkaServer, RepositoryFolder<KafkaResource, ?>, FlexoObject>(
			"create_kafka_model", FlexoActionFactory.newMenu, FlexoActionFactory.defaultGroup, FlexoActionFactory.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateKafkaServer makeNewAction(RepositoryFolder<KafkaResource, ?> focusedObject, Vector<FlexoObject> globalSelection,
				FlexoEditor editor) {
			return new CreateKafkaServer(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(RepositoryFolder<KafkaResource, ?> object, Vector<FlexoObject> globalSelection) {
			// TODO check what should be done
			return true;
		}

		@Override
		public boolean isEnabledForSelection(RepositoryFolder<KafkaResource, ?> object, Vector<FlexoObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(CreateKafkaServer.actionType, RepositoryFolder.class);
	}

	private String resourceName = "test.kafka";
	private String server = "localhost:9092";
	private String zookeeper = "localhost:2181";

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getZookeeper() {
		return zookeeper;
	}

	public void setZookeeper(String zookeeper) {
		this.zookeeper = zookeeper;
	}

	private CreateKafkaServer(RepositoryFolder<KafkaResource, ?> focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public LocalizedDelegate getLocales() {
		if (getServiceManager() != null) {
			return getTechnologyAdapter().getLocales();
		}
		return super.getLocales();
	}

	private KafkaTechnologyAdapter getTechnologyAdapter() {
		return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(KafkaTechnologyAdapter.class);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		try {
			KafkaTechnologyAdapter technologyAdapter = getTechnologyAdapter();
			KafkaResourceFactory resourceFactory = technologyAdapter.getResourceFactory(KafkaResourceFactory.class);
			TechnologyContextManager<KafkaTechnologyAdapter> technologyContextManager = (TechnologyContextManager<KafkaTechnologyAdapter>) technologyAdapter
					.getTechnologyContextManager();
			KafkaResource resource = resourceFactory.makeKafkaServerResource(resourceName, getFocusedObject());
			KafkaServer model = resource.getResourceData(null);
			model.setServer(getServer());
			model.setZookeeper(getZookeeper());

		} catch (ModelDefinitionException | FileNotFoundException | ResourceLoadingCancelledException e) {
			throw new FlexoException(e);
		}

	}

	@Override
	public boolean isValid() {
		return true;
	}
}
