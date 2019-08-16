package com.hcop.ptn.service;

import com.hcop.ptn.common.db.dao.NeDao;
import com.hcop.ptn.common.enums.DummyNeType_PKT;
import com.hcop.ptn.common.exception.ServiceException;
import com.hcop.ptn.common.utils.CollectionUtils;
import com.hcop.ptn.restful.model.NE;
import com.hcop.ptn.restful.model.NECommunicationState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Map;

public class NeService {
    private Log log = LogFactory.getLog(NeService.class);
    private static NeService ourInstance = new NeService();

    public static NeService getInstance() {
        return ourInstance;
    }

    private NeService() {
    }

    public Collection<NE> getAllNes() throws ServiceException {
        try {
            Collection<NE> nes = NeDao.getInstance().getNeWith(null, null, null);
            setAttributes(nes);
            return nes;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(500, e.getMessage());
        }
    }

    public Collection<NE> getNeById(String neId) throws ServiceException {
        try {
            if (neId.trim().length() < 1) {
                throw new ServiceException( 400, "Parameter Error: empty ne id");
            }
            Collection<NE> nes = NeDao.getInstance().getNeWith(null, neId, null);
            setAttributes(nes);
            return nes;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(500, e.getMessage());
        }
    }

    public Collection<NE> getNeByName(String neName) throws ServiceException {
        try {
            if (neName.trim().length() < 1) {
                throw new ServiceException( 400, "Parameter Error: empty ne name");
            }
            Collection<NE> nes = NeDao.getInstance().getNeWith(neName, null, null);
            setAttributes(nes);
            return nes;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(500, e.getMessage());
        }
    }

    private void setAttributes(Collection<NE> nes) throws ServiceException {
        if(CollectionUtils.isEmpty(nes)) {
            return;
        }

        try {
            Map<String,String> map = NeDao.getInstance().queryNeCommunicationSate();
            for(NE ne : nes){
                ne.setCommunicationState(NECommunicationState.fromValue(map.get(ne.getNeName()).toLowerCase()));
                ne.setNeType(DummyNeType_PKT.fromInt(Integer.parseInt(ne.getNeType())).toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(500, e.getMessage());
        }
    }
}
