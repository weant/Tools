package com.hcop.ptn.api;

import com.hcop.ptn.common.exception.ServiceException;
import com.hcop.ptn.common.utils.CollectionUtils;
import com.hcop.ptn.restful.model.NE;
import com.hcop.ptn.restful.server.api.NesApiService;
import com.hcop.ptn.restful.server.api.NotFoundException;
import com.hcop.ptn.service.NeService;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2019-01-19T20:54:50.995+08:00")
public class NesApiServiceImpl extends NesApiService {
    private NeService neService = NeService.getInstance();
    @Override
    public Response getAllNes(SecurityContext securityContext) throws NotFoundException {
        try {
            Collection <NE> nes = neService.getAllNes();
            return Response.ok().entity(nes).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }

    @Override
    public Response getNeById(String neId, SecurityContext securityContext) throws NotFoundException {
        try {
            Collection<NE> nes = neService.getNeById(neId);
            if(CollectionUtils.isEmpty(nes)) {
                return Response.serverError().status(400, "Unmatched ne id").build();
            }
            List<NE> neList = new ArrayList<>();
            neList.addAll(nes);
            return Response.ok().entity(neList.get(0)).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }

    @Override
    public Response getNeByName(String neName, SecurityContext securityContext) throws NotFoundException {
        try {
            Collection<NE> nes = neService.getNeByName(neName);
            if(CollectionUtils.isEmpty(nes)) {
                return Response.serverError().status(400, "Unmatched ne name").build();
            }
            return Response.ok().entity(nes).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }
}
