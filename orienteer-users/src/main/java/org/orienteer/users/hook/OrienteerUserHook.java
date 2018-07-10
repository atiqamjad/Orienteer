package org.orienteer.users.hook;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.metadata.security.ORestrictedOperation;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.PerspectivesModule;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.module.OrienteerUsersModule;
import org.orienteer.users.service.IOUsersDBService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class OrienteerUserHook extends ODocumentHookAbstract {

    public OrienteerUserHook(ODatabaseDocument database) {
        super(database);
        setIncludeClasses(OrienteerUser.CLASS_NAME);
    }

    @Override
    public RESULT onRecordBeforeCreate(ODocument doc) {
        IOUsersDBService dbService = OrienteerWebApplication.lookupApplication().getServiceInstance(IOUsersDBService.class);

        doc.field(OrienteerUser.PROP_ID, UUID.randomUUID().toString());

        if (doc.field(PerspectivesModule.PROP_PERSPECTIVE) == null) {
            doc.field(PerspectivesModule.PROP_PERSPECTIVE, dbService.getDefaultOrienteerUserPerspective());
        }

        List<ODocument> roles = doc.field("roles");
        if (roles == null || roles.isEmpty()) {
            ODocument roleDoc = dbService.getRoleByName(OrienteerUsersModule.ORIENTEER_USER_ROLE).getDocument();
            doc.field("roles", Collections.singleton(roleDoc));
        }

        doc.field(ORestrictedOperation.ALLOW_READ.getFieldName(), doc);
        doc.field(ORestrictedOperation.ALLOW_UPDATE.getFieldName(), doc);

        return super.onRecordBeforeCreate(doc);
    }

    @Override
    public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
        return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
    }

}
