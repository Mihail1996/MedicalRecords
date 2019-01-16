package tecnico.ulisboa.sirs.service;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.DecisionType;
import org.ow2.authzforce.core.pdp.api.AttributeFqn;
import org.ow2.authzforce.core.pdp.api.AttributeFqns;
import org.ow2.authzforce.core.pdp.api.DecisionRequest;
import org.ow2.authzforce.core.pdp.api.DecisionRequestBuilder;
import org.ow2.authzforce.core.pdp.api.value.AttributeBag;
import org.ow2.authzforce.core.pdp.api.value.Bags;
import org.ow2.authzforce.core.pdp.api.value.StandardDatatypes;
import org.ow2.authzforce.core.pdp.api.value.StringValue;
import org.ow2.authzforce.core.pdp.impl.BasePdpEngine;
import org.ow2.authzforce.xacml.identifiers.XacmlAttributeId;
import tecnico.ulisboa.sirs.model.Role;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static org.ow2.authzforce.xacml.identifiers.XacmlAttributeCategory.XACML_1_0_ACCESS_SUBJECT;
import static org.ow2.authzforce.xacml.identifiers.XacmlAttributeCategory.XACML_3_0_RESOURCE;

public class ABAC {

    private static Map<String, Boolean> decisions;
    private static String refRole;

    public static boolean hasAccessPermission(String attribute, Role role, BasePdpEngine pdp) {
        final DecisionRequestBuilder<?> requestBuilder = pdp.newRequestBuilder(-1, -1);

        final AttributeFqn subjectRoleAttributeId = AttributeFqns.newInstance(XACML_1_0_ACCESS_SUBJECT.value(), Optional.empty(), XacmlAttributeId.XACML_2_0_SUBJECT_ROLE.value());
        final AttributeBag<?> roleAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue(role.getRole()));
        requestBuilder.putNamedAttributeIfAbsent(subjectRoleAttributeId, roleAttributeValues);

        final AttributeFqn resourceIdAttributeId = AttributeFqns.newInstance(XACML_3_0_RESOURCE.value(), Optional.empty(), XacmlAttributeId.XACML_1_0_RESOURCE_ID.value());
        final AttributeBag<?> resourceIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue(attribute));
        requestBuilder.putNamedAttributeIfAbsent(resourceIdAttributeId, resourceIdAttributeValues);

        DecisionRequest decisionRequest = requestBuilder.build(false);
        return pdp.evaluate(decisionRequest).getDecision() == DecisionType.PERMIT;
    }


    public static Map<String, Boolean> hasAccessPermission(Role role, BasePdpEngine pdp) {
        if (decisions != null && refRole.equals(role.getRole())) {
            return decisions;
        }
        refRole = role.getRole();
        Map<String, Boolean> decisionsTmp = new TreeMap<>();
        decisionsTmp.put("private/addRecord", hasAccessPermission("private/addRecord", role, pdp));
        decisionsTmp.put("private/registration", hasAccessPermission("private/registration", role, pdp));
        decisionsTmp.put("private/home", hasAccessPermission("private/home", role, pdp));
        decisionsTmp.put("private/myRecord", hasAccessPermission("private/myRecord", role, pdp));
        decisionsTmp.put("private/changeMyPassword", hasAccessPermission("private/changeMyPassword", role, pdp));
        decisionsTmp.put("private/watchRecord", hasAccessPermission("private/watchRecord", role, pdp));
        decisionsTmp.put("private/addHospital", hasAccessPermission("private/addHospital", role, pdp));
        decisions = decisionsTmp;
        return decisions;
    }

}
