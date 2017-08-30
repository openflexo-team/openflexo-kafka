/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Openflexo-technology-adapters-ui, a component of the software infrastructure 
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

import java.util.EventObject;
import java.util.logging.Logger;
import javax.swing.*;
import org.openflexo.components.wizard.Wizard;
import org.openflexo.components.wizard.WizardDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.technologyadapter.kafka.library.KafkaIconLibrary;
import org.openflexo.technologyadapter.kafka.model.action.CreateKafkaServer;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class CreateKafkaServerInitializer extends ActionInitializer<CreateKafkaServer, RepositoryFolder, FlexoObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public CreateKafkaServerInitializer(ControllerActionInitializer actionInitializer) {
		super(CreateKafkaServer.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<CreateKafkaServer> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateKafkaServer>() {
			@Override
			public boolean run(EventObject e, CreateKafkaServer action) {
				Wizard wizard = new CreateKafkaServerWizard(action, getController());
				WizardDialog dialog = new WizardDialog(wizard, getController());
				dialog.showDialog();
				if (dialog.getStatus() != FIBController.Status.VALIDATED) {
					// Operation cancelled
					return false;
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateKafkaServer> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateKafkaServer>() {
			@Override
			public boolean run(EventObject e, CreateKafkaServer action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<CreateKafkaServer> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<CreateKafkaServer>() {
			@Override
			public boolean handleException(FlexoException exception, CreateKafkaServer action) {
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon(FlexoActionFactory actionType) {
		return KafkaIconLibrary.KAFKA_FILE_ICON;
	}

}
