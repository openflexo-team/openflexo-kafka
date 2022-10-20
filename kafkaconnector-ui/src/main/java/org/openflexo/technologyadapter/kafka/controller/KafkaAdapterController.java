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

package org.openflexo.technologyadapter.kafka.controller;

import javax.swing.ImageIcon;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.gina.utils.InspectorGroup;
import org.openflexo.technologyadapter.kafka.KafkaTechnologyAdapter;
import org.openflexo.technologyadapter.kafka.controller.action.CreateKafkaServerInitializer;
import org.openflexo.technologyadapter.kafka.library.KafkaIconLibrary;
import org.openflexo.technologyadapter.kafka.model.KafkaServer;
import org.openflexo.technologyadapter.kafka.view.KafkaServerView;
import org.openflexo.view.EmptyPanel;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.TechnologyAdapterController;
import org.openflexo.view.controller.model.FlexoPerspective;

public class KafkaAdapterController extends TechnologyAdapterController<KafkaTechnologyAdapter> {
    
	private InspectorGroup kafkaInspectorGroup;

	@Override
	public Class<KafkaTechnologyAdapter> getTechnologyAdapterClass() {
		return KafkaTechnologyAdapter.class;
	}

	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {
		new CreateKafkaServerInitializer(actionInitializer);
	}

    @Override
    protected void initializeInspectors(FlexoController controller) {
		kafkaInspectorGroup = controller.loadInspectorGroup("Kafka", getTechnologyAdapter().getLocales(), getFMLTechnologyAdapterInspectorGroup());
	}

    @Override
    public InspectorGroup getTechnologyAdapterInspectorGroup() {
        return kafkaInspectorGroup;
    }

    @Override
	public ImageIcon getTechnologyBigIcon() {
		return KafkaIconLibrary.KAFKA_TECHNOLOGY_BIG_ICON;
	}

	@Override
	public ImageIcon getTechnologyIcon() {
		return KafkaIconLibrary.KAFKA_TECHNOLOGY_ICON;
	}

	@Override
	public ImageIcon getModelIcon() {
		return KafkaIconLibrary.KAFKA_FILE_ICON;
	}

	@Override
	public ImageIcon getMetaModelIcon() {
		return KafkaIconLibrary.KAFKA_FILE_ICON;
	}

	@Override
	public ImageIcon getIconForTechnologyObject(final Class<? extends TechnologyObject<?>> objectClass) {
		return KafkaIconLibrary.KAFKA_TECHNOLOGY_ICON;
	}

	@Override
	public ModuleView<?> createModuleViewForMasterObject(final TechnologyObject<KafkaTechnologyAdapter> object, final FlexoController controller, final FlexoPerspective perspective) {
		if (object instanceof KafkaServer){
			return new KafkaServerView((KafkaServer) object, controller, perspective);
		}
		return new EmptyPanel<>(controller, perspective, object);
	}

    @Override
    public ImageIcon getIconForFlexoRole(Class<? extends FlexoRole<?>> flexoRoleClass) {
		return KafkaIconLibrary.KAFKA_TECHNOLOGY_ICON;
    }

	@Override
	public String getWindowTitleforObject(TechnologyObject<KafkaTechnologyAdapter> obj, FlexoController controller) {
		if (obj instanceof KafkaServer) {
			return "Connection to " +  ((KafkaServer) obj).getServer();
		}
		return "Connection";
	}
	
	@Override
	public boolean isRepresentableInModuleView(TechnologyObject<KafkaTechnologyAdapter> object) {
		return object instanceof KafkaTechnologyAdapter;
	}
	
	@Override
	public FlexoObject getRepresentableMasterObject(TechnologyObject<KafkaTechnologyAdapter> object) {
		if (object instanceof KafkaTechnologyAdapter) {
			return object;
		}
		return null;
	}
}

