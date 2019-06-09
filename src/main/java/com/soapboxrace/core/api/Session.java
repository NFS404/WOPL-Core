package com.soapboxrace.core.api;

import java.net.URI;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.soapboxrace.core.api.util.Secured;
import com.soapboxrace.core.bo.ParameterBO;
import com.soapboxrace.core.bo.SessionBO;
import com.soapboxrace.core.bo.TokenSessionBO;
import com.soapboxrace.jaxb.http.ChatServer;

import com.soapboxrace.core.jpa.PersonaEntity;
import com.soapboxrace.core.dao.PersonaDAO;

@Path("/Session")
public class Session {

	@Context
	UriInfo uri;

	@EJB
	private SessionBO bo;

	@EJB
	private ParameterBO parameterBO;

	@EJB
    private PersonaDAO personaDAO;

    @EJB
	private TokenSessionBO tokenSessionBO;

	@GET
	@Secured
	@Path("/GetChatInfo")
	@Produces(MediaType.APPLICATION_XML)
	public ChatServer getChatInfo(@HeaderParam("securityToken") String securityToken) {
		ChatServer chatServer = new ChatServer();
		String xmppIp = parameterBO.getStrParam("XMPP_IP");
		if ("127.0.0.1".equals(parameterBO.getStrParam("XMPP_IP"))) {
			URI myUri = uri.getBaseUri();
			xmppIp = myUri.getHost();
		}
		chatServer.setIp(xmppIp);


		PersonaEntity persona = personaDAO.findById(tokenSessionBO.getActivePersonaId(securityToken));
		if(persona.getShadowBanned() == true) {
			chatServer.setPort(5322);
		} else {
			chatServer.setPort(parameterBO.getIntParam("XMPP_PORT"));	
		}

		chatServer.setPrefix("sbrw");
		chatServer.setRooms(bo.getAllChatRoom());
		return chatServer;
	}
}
