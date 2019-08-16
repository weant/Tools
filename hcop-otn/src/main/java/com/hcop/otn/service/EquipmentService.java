package com.hcop.otn.service;

import com.hcop.otn.common.beans.InternalNe;
import com.hcop.otn.common.db.dao.EquipmentDao;
import com.hcop.otn.common.db.dao.NeDao;
import com.hcop.otn.common.exception.ServiceException;
import com.hcop.otn.restful.model.Equipment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EquipmentService {
    private Log log = LogFactory.getLog(EquipmentService.class);
    private static EquipmentService ourInstance = new EquipmentService();

    public static EquipmentService getInstance() {
        return ourInstance;
    }

    private EquipmentService() {
    }

    public Collection<Equipment>  getEquipmentsByNeId(String neId) throws ServiceException {
        try {
            if (neId.trim().length() < 1) {
                throw new ServiceException( 400, "Parameter Error: empty ne id");
            }
            Collection<InternalNe> nes = NeDao.getInstance().getInternalNesById(neId);
            if(nes.size() < 1) {
                throw new ServiceException( 400, "Unmatched ne id");
            }

            List<InternalNe> neList = new ArrayList <>();
            neList.addAll(nes);

            Collection<Equipment> equipments = EquipmentDao.getInstance().getEquipmentsByNeId(neList.get(0).getEmlId(), neList.get(0).getGroupId());

            equipments.forEach(equipment -> {
                equipment.setNeId(neId);
                equipment.setNeName(neList.get(0).getNeName());
            });

            return equipments;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(500, e.getMessage());
        }
    }
}
