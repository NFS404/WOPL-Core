package com.soapboxrace.core.api;

import java.net.URI;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.soapboxrace.core.api.util.Secured;
import com.soapboxrace.core.bo.ParameterBO;
import com.soapboxrace.core.bo.TokenSessionBO;
import com.soapboxrace.jaxb.http.ArrayOfUdpRelayInfo;
import com.soapboxrace.jaxb.http.UdpRelayInfo;

import com.soapboxrace.core.jpa.PersonaEntity;
import com.soapboxrace.core.dao.PersonaDAO;

@Path("/getrebroadcasters")
public class GetRebroadcasters {

	@Context
	UriInfo uri;

	@EJB
	private ParameterBO parameterBO;

	@EJB
	private TokenSessionBO tokenSessionBO;

	@EJB
    private PersonaDAO personaDAO;

	@GET
	@Secured
	@Produces(MediaType.APPLICATION_XML)
	public ArrayOfUdpRelayInfo getRebroadcasters(@HeaderParam("securityToken") String securityToken) {
		PersonaEntity persona = personaDAO.findById(tokenSessionBO.getActivePersonaId(securityToken));
		ArrayOfUdpRelayInfo arrayOfUdpRelayInfo = new ArrayOfUdpRelayInfo();
		UdpRelayInfo udpRelayInfo = new UdpRelayInfo();

		String freeroamIp = "127.0.0.1";

		if(persona.getShadowBanned() != true) {
			freeroamIp = parameterBO.getStrParam("UDP_FREEROAM_IP");
		}

		if ("127.0.0.1".equals(freeroamIp)) {
			URI myUri = uri.getBaseUri();
			freeroamIp = myUri.getHost();
		}

		udpRelayInfo.setHost(freeroamIp);
		udpRelayInfo.setPort(parameterBO.getIntParam("UDP_FREEROAM_PORT"));
		arrayOfUdpRelayInfo.getUdpRelayInfo().add(udpRelayInfo);
		return arrayOfUdpRelayInfo;
	}
}
