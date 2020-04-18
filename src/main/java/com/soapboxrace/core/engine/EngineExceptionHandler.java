package com.soapboxrace.core.engine;

import com.soapboxrace.core.bo.ErrorReportingBO;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.ejb.EJB;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EngineExceptionHandler implements ExceptionMapper<Exception> {

    @EJB
    private ErrorReportingBO errorReportingBO;

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof EngineException) {
            errorReportingBO.sendException(exception);
            EngineException engineException = (EngineException) exception;
            EngineExceptionTrans engineExceptionTrans = new EngineExceptionTrans();
            String stackTrace = ExceptionUtils.getStackTrace(exception);

            engineExceptionTrans.setErrorCode(engineException.getCode().getErrorCode());
            engineExceptionTrans.setStackTrace(stackTrace);
            engineExceptionTrans.setInnerException(new EngineInnerExceptionTrans());
            engineExceptionTrans.getInnerException().setErrorCode(engineExceptionTrans.getErrorCode());
            engineExceptionTrans.getInnerException().setStackTrace(stackTrace);

            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .entity(engineExceptionTrans).build();
        } else if (exception instanceof NotAuthorizedException) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            errorReportingBO.sendException(exception);
            EngineExceptionTrans engineExceptionTrans = new EngineExceptionTrans();
            String stackTrace = ExceptionUtils.getStackTrace(exception);

            engineExceptionTrans.setErrorCode(EngineExceptionCode.UnspecifiedError.getErrorCode());
            engineExceptionTrans.setStackTrace(stackTrace);
            engineExceptionTrans.setInnerException(new EngineInnerExceptionTrans());
            engineExceptionTrans.getInnerException().setErrorCode(engineExceptionTrans.getErrorCode());
            engineExceptionTrans.getInnerException().setStackTrace(stackTrace);

            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .entity(engineExceptionTrans).build();
        }
    }
}
