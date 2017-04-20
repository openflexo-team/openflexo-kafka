package org.openflexo.technologyadapter.kafka.fml;

import java.lang.reflect.Type;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.rt.ActorReference;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.VirtualModelInstanceModelFactory;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.kafka.KafkaTechnologyAdapter;
import org.openflexo.technologyadapter.kafka.fml.KafkaListenerRole.KafkaListenerRoleImpl;
import org.openflexo.technologyadapter.kafka.model.KafkaListener;

@ModelEntity @XMLElement
@ImplementationClass(KafkaListenerRoleImpl.class)
public interface KafkaListenerRole extends FlexoRole<KafkaListener> {

    abstract class KafkaListenerRoleImpl extends FlexoRoleImpl<KafkaListener> implements KafkaListenerRole {

        @Override
        public FlexoRole.RoleCloningStrategy defaultCloningStrategy() {
            return FlexoRole.RoleCloningStrategy.Reference;
        }

        @Override
        public boolean defaultBehaviourIsToBeDeleted() {
            return false;
        }

        @Override
        public ActorReference<KafkaListener> makeActorReference(KafkaListener object, FlexoConceptInstance fci) {
            final VirtualModelInstanceModelFactory factory = (VirtualModelInstanceModelFactory) fci.getFactory();
            final KafkaListenerActorReference actor = factory.newInstance(KafkaListenerActorReference.class);
            actor.setTopics(object.getTopics());
            actor.setStarted(object.isStarted());
            actor.setFlexoRole(this);
            actor.setFlexoConceptInstance(fci);
            actor.setModellingElement(object);
            return actor;
        }

        @Override
        public Class<? extends TechnologyAdapter> getRoleTechnologyAdapterClass() {
            return KafkaTechnologyAdapter.class;
        }

        @Override
        public Type getType() {
            return KafkaListener.class;
        }

    }
}
