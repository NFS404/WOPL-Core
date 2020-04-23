package com.soapboxrace.core.api;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.soapboxrace.core.api.util.Secured;
import com.soapboxrace.jaxb.http.FraudConfig;

@Path("/security")
public class Security {

	@GET
	@Secured
	@Path("/fraudConfig")
	@Produces(MediaType.APPLICATION_XML)
	public FraudConfig fraudConfig(@HeaderParam("userId") Long userId) {
        FraudConfig fraudConfig = new FraudConfig();
        fraudConfig.setEnabledBitField(0);
        fraudConfig.setGameFileFreq(2147483647);
        fraudConfig.setModuleFreq(2147483647);
        fraudConfig.setStartUpFreq(2147483647);
        fraudConfig.setUserID(userId);
        return fraudConfig;
	}

	@POST
	@Secured
	@Path("/generateWebToken")
	@Produces(MediaType.APPLICATION_XML)
	public String generateWebToken(@HeaderParam("securityToken") String securityToken) {
		return "<token>" + securityToken + "</token><success code=\"SUCCESS\"/>";
	}

}
