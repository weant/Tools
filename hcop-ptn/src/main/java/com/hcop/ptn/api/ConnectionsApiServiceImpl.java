package com.hcop.ptn.api;

import com.hcop.ptn.common.exception.ServiceException;
import com.hcop.ptn.restful.model.Connection;
import com.hcop.ptn.restful.model.ConnectionType;
import com.hcop.ptn.restful.server.api.ConnectionsApiService;
import com.hcop.ptn.restful.server.api.NotFoundException;
import com.hcop.ptn.service.ConnectionService;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Collection;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2019-01-19T20:54:50.995+08:00")
public class ConnectionsApiServiceImpl extends ConnectionsApiService {
    private ConnectionService connectionService = ConnectionService.getInstance();
    @Override
    public Response getAllCESs(SecurityContext securityContext) throws NotFoundException {
        try {
            Collection<Connection> otss = connectionService.getConnectionsByType(ConnectionType.CES, "");
            return Response.ok().entity(otss).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }

    @Override
    public Response getAllEVCs(SecurityContext securityContext) throws NotFoundException {
        try {
            Collection<Connection> otss = connectionService.getConnectionsByType(ConnectionType.EVC,"");
            return Response.ok().entity(otss).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }

    @Override
    public Response getAllPWs(SecurityContext securityContext) throws NotFoundException {
        try {
            Collection<Connection> otss = connectionService.getConnectionsByType(ConnectionType.PW, "");
            return Response.ok().entity(otss).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }

    @Override
    public Response getAllSections(SecurityContext securityContext) throws NotFoundException {
        try {
            Collection<Connection> otss = connectionService.getConnectionsByType(ConnectionType.SECTION, "");
            return Response.ok().entity(otss).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }

    @Override
    public Response getAllTunnels(SecurityContext securityContext) throws NotFoundException {
        try {
            Collection<Connection> otss = connectionService.getConnectionsByType(ConnectionType.TUNNEL, "");
            return Response.ok().entity(otss).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }
}
