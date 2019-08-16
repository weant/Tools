package com.hcop.otn.api;

import com.hcop.otn.common.exception.ServiceException;
import com.hcop.otn.common.utils.CollectionUtils;
import com.hcop.otn.restful.model.TP;
import com.hcop.otn.restful.server.api.NotFoundException;
import com.hcop.otn.restful.server.api.TpsApiService;
import com.hcop.otn.service.TpService;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2019-01-19T16:43:05.504+08:00")
public class TpsApiServiceImpl extends TpsApiService {
    TpService tpService = TpService.getInstance();
    @Override
    public Response getPTPsByNeId(String neId, SecurityContext securityContext) throws NotFoundException {
        try {
            Collection<TP> tps = tpService.getTpByNeId(neId);
            if(CollectionUtils.isEmpty(tps)) {
                return Response.serverError().status(400, "Unmatched ne id").build();
            }
            return Response.ok().entity(tps).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }

    @Override
    public Response getTpByNeIdAndTpId(String neId, String tpId, SecurityContext securityContext) throws NotFoundException {
        try {
            Collection<TP> tps = tpService.getTpByNeIdAndTpId(neId, tpId);
            if(CollectionUtils.isEmpty(tps)) {
                return Response.serverError().status(400, "Unmatched ne id or tp id").build();
            }
            List<TP> tpList = new ArrayList<>();
            tpList.addAll(tps);
            return Response.ok().entity(tpList.get(0)).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }
}
