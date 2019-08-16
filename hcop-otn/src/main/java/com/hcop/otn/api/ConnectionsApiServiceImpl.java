package com.hcop.otn.api;

import com.hcop.otn.common.exception.ServiceException;
import com.hcop.otn.restful.model.Connection;
import com.hcop.otn.restful.model.ConnectionType;
import com.hcop.otn.restful.server.api.ConnectionsApiService;
import com.hcop.otn.restful.server.api.NotFoundException;
import com.hcop.otn.service.ConnectionService;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Collection;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2019-01-19T16:43:05.504+08:00")
public class ConnectionsApiServiceImpl extends ConnectionsApiService {
    private ConnectionService connectionService = ConnectionService.getInstance();
    @Override
    public Response getAllDSRs(SecurityContext securityContext) throws NotFoundException {
        try {
            Collection<Connection> dsrs = connectionService.getConnectionsByType(ConnectionType.PATH);
            return Response.ok().entity(dsrs).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }

    @Override
    public Response getAllOCHs(SecurityContext securityContext) throws NotFoundException {
        try {
            Collection<Connection> ochs = connectionService.getConnectionsByType(ConnectionType.OCH);
            return Response.ok().entity(ochs).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }

    @Override
    public Response getAllOTSs(SecurityContext securityContext) throws NotFoundException {
        try {
            Collection<Connection> otss = connectionService.getConnectionsByType(ConnectionType.OTS);
            return Response.ok().entity(otss).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }

    @Override
    public Response getDsrByOchId(String ochId, SecurityContext securityContext) throws NotFoundException {
        try {
            List<Connection> paths = connectionService.getDsrByOchId(ochId);
            return Response.ok().entity(paths).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }
}
