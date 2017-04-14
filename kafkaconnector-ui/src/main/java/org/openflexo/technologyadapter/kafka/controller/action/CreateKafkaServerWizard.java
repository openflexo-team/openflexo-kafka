/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Freemodellingeditor, a component of the software infrastructure 
 * developed at Openflexo.
 * 
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
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
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

package org.openflexo.technologyadapter.kafka.controller.action;

import java.awt.*;
import java.util.logging.Logger;
import org.openflexo.ApplicationContext;
import org.openflexo.components.wizard.FlexoWizard;
import org.openflexo.components.wizard.WizardStep;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.gina.annotation.FIBPanel;
import org.openflexo.icon.FMLIconLibrary;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.technologyadapter.kafka.model.action.CreateKafkaServer;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.view.controller.FlexoController;

public class CreateKafkaServerWizard extends FlexoWizard {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CreateKafkaServerWizard.class.getPackage().getName());

	private final CreateKafkaServer action;

	private final ConfigureKafkaServer configureKafkaServer;

	public CreateKafkaServerWizard(CreateKafkaServer action, FlexoController controller) {
		super(controller);
		this.action = action;
		addStep(configureKafkaServer = new ConfigureKafkaServer());
	}

	@Override
	public String getWizardTitle() {
		return action.getLocales().localizedForKey("create_kafka_server");
	}

	@Override
	public Image getDefaultPageImage() {
		// TODO change icon
		return IconFactory.getImageIcon(FMLIconLibrary.FLEXO_CONCEPT_BIG_ICON, IconLibrary.NEW_32_32).getImage();
	}

	public ConfigureKafkaServer getConfigureKafkaServer() {
		return configureKafkaServer;
	}

	/**
	 * This step is used to set {@link VirtualModel} to be used, as well as name and title of the {@link VirtualModelInstance}
	 * 
	 * @author sylvain
	 *
	 */
	@FIBPanel("Fib/Wizard/ConfigureKafkaServer.fib")
	public class ConfigureKafkaServer extends WizardStep {

		public ApplicationContext getServiceManager() {
			return getController().getApplicationContext();
		}

		public CreateKafkaServer getAction() {
			return action;
		}

		@Override
		public String getTitle() {
			return action.getLocales().localizedForKey("configure_kafka_server");
		}

		@Override
		public boolean isValid() {

			if (StringUtils.isEmpty(getAddress())) {
				setIssueMessage(action.getLocales().localizedForKey("no_address_defined"), IssueMessageType.ERROR);
				return false;
			}

			return true;

		}

		public String getResourceName() {
			return action.getResourceName();
		}

		public void setResourceName(String newResourceName) {
			if (!newResourceName.equals(getResourceName())) {
				String oldValue = getResourceName();
				action.setResourceName(newResourceName);
				getPropertyChangeSupport().firePropertyChange("resourceName", oldValue, newResourceName);
				checkValidity();
			}
		}

		public String getAddress() {
			return action.getAddress();
		}

		public void setAddress(String newAddress) {
			if (!newAddress.equals(getAddress())) {
				String oldValue = getAddress();
				action.setAddress(newAddress);
				getPropertyChangeSupport().firePropertyChange("address", oldValue, newAddress);
				checkValidity();
			}
		}
	}
}
