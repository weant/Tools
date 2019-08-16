package com.hcop.otn.api;

import com.hcop.otn.common.exception.ServiceException;
import com.hcop.otn.common.utils.CollectionUtils;
import com.hcop.otn.restful.model.Equipment;
import com.hcop.otn.restful.server.api.EquipmentsApiService;
import com.hcop.otn.restful.server.api.NotFoundException;
import com.hcop.otn.service.EquipmentService;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Collection;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2019-01-19T16:43:05.504+08:00")
public class EquipmentsApiServiceImpl extends EquipmentsApiService {
    private EquipmentService equipmentService = EquipmentService.getInstance();
    @Override
    public Response getEquipmentsByNeId(String neId, SecurityContext securityContext) throws NotFoundException {
        try {
            Collection<Equipment> equips = equipmentService.getEquipmentsByNeId(neId);
            if(CollectionUtils.isEmpty(equips)) {
                return Response.serverError().status(400, "Unmatched ne id").build();
            }
            return Response.ok().entity(equips).build();
        } catch (ServiceException e){
            return Response.serverError().status(e.getCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }
}
