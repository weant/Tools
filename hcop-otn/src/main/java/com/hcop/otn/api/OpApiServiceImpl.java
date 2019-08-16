package com.hcop.otn.api;

import com.hcop.otn.common.exception.ServiceException;
import com.hcop.otn.restful.model.ConnOp;
import com.hcop.otn.restful.model.NeTp;
import com.hcop.otn.restful.model.TpOp;
import com.hcop.otn.restful.server.api.ApiResponseMessage;
import com.hcop.otn.restful.server.api.NotFoundException;
import com.hcop.otn.restful.server.api.OpApiService;
import com.hcop.otn.service.OpService;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2019-01-19T16:43:05.504+08:00")
public class OpApiServiceImpl extends OpApiService {
    private OpService opService = OpService.getInstance();
    @Override
    public Response getConnOps(List<String> connIds, SecurityContext securityContext) throws NotFoundException {
        try{
            List <ConnOp> ops = opService.getConnectionOps(connIds);
            return Response.ok().entity(ops).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }
    @Override
    public Response getTpOps(List<NeTp> neTps, SecurityContext securityContext) throws NotFoundException {
        try{
            List <TpOp> ops = opService.getTpOps(neTps);
            return Response.ok().entity(ops).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }
}
