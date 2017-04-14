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

package org.openflexo.technologyadapter.kafka.library;

import javax.swing.*;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.icon.ImageIconResource;
import org.openflexo.rm.ResourceLocator;


public class KafkaIconLibrary {
	public static final ImageIcon KAFKA_TECHNOLOGY_BIG_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/kafka_big.png"));
	public static final ImageIcon KAFKA_TECHNOLOGY_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/kafka.png"));
	public static final ImageIcon KAFKA_FILE_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/kafka.png"));


	public static ImageIcon iconForObject(Class<? extends TechnologyObject> objectClass) {
		return KAFKA_TECHNOLOGY_ICON;
	}
}
