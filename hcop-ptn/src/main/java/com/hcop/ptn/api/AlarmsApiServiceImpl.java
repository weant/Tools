package com.hcop.ptn.api;

import com.hcop.ptn.common.exception.ServiceException;
import com.hcop.ptn.restful.model.ConnectionType;
import com.hcop.ptn.restful.server.api.*;

import com.hcop.ptn.restful.model.Alarm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.hcop.ptn.restful.server.api.NotFoundException;
import com.hcop.ptn.service.AlarmService;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2019-01-19T20:54:50.995+08:00")
public class AlarmsApiServiceImpl extends AlarmsApiService {
    private AlarmService alarmService = AlarmService.getInstance();
    @Override
    public Response getAllActiveAlarms(SecurityContext securityContext) throws NotFoundException {
        try {
            Collection<Alarm> alarms = alarmService.getAllAlarms();
            return Response.ok().entity(alarms).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }
    @Override
    public Response getAllRootcauseAlarms(SecurityContext securityContext) throws NotFoundException {
        try {
            Collection<Alarm> rcs = alarmService.getAllRootCauses();
            return Response.ok().entity(rcs).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }

    @Override
    public Response getRCAlarmsByAlarmIdAndServer(String alarmId, String server, SecurityContext securityContext) throws NotFoundException {
        try {
            Collection<Alarm> rcs = alarmService.getRCAlarmsByAlarmIdAndServer(alarmId, server);
            return Response.ok().entity(rcs).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }

    @Override
    public Response getSolutionByPcId(String pcId, SecurityContext securityContext) throws NotFoundException {
        try {
            Collection<String> solutions = alarmService.getSolutionByPcId(pcId);
            return Response.ok().entity(solutions).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }
}
