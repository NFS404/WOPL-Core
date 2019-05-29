package com.soapboxrace.core.api.util;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.soapboxrace.core.bo.AuthenticationBO;
import com.soapboxrace.core.bo.ParameterBO;
import com.soapboxrace.jaxb.login.LoginStatusVO;

import com.soapboxrace.core.dao.UserDAO;
import com.soapboxrace.core.jpa.UserEntity;

@LauncherChecks
@Provider
@Priority(Priorities.AUTHORIZATION)
public class LaunchFilter implements ContainerRequestFilter {

	@EJB
	private AuthenticationBO authenticationBO;

	@EJB
	private ParameterBO parameterBO;

	@Context
	private HttpServletRequest sr;

	@EJB
	private UserDAO userDao;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String hwid = requestContext.getHeaderString("X-HWID");
		String email = sr.getParameter("email");

		UserEntity userEntity = userDao.findByEmail(email);
		if(userEntity != null) {
			userEntity.setHwid(hwid);
			userDao.update(userEntity);
		}


		if (parameterBO.getBoolParam("ENABLE_METONATOR_LAUNCHER_PROTECTION")) {
			String userAgent = requestContext.getHeaderString("User-Agent");
			String gameLauncherHash = requestContext.getHeaderString("X-GameLauncherHash");

			System.out.println(userAgent);

			if ((userAgent == null || !userAgent.equals("GameLauncher (+https://github.com/SoapboxRaceWorld/GameLauncher_NFSW)"))
					|| (hwid == null || hwid.trim().isEmpty()) || (gameLauncherHash == null || gameLauncherHash.trim().isEmpty())) {
				LoginStatusVO loginStatusVO = new LoginStatusVO(0L, "", false);
				loginStatusVO.setDescription("Please use MeTonaTOR's launcher. Or, are you tampering?");

				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(loginStatusVO).build());

				return;
			}
		}
	}

}