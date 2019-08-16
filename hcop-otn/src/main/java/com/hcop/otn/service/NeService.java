package com.hcop.otn.service;

import com.hcop.otn.common.db.dao.NeDao;
import com.hcop.otn.common.exception.ServiceException;
import com.hcop.otn.restful.model.NE;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Collections;

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
            return NeDao.getInstance().getAllNes();
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
            return NeDao.getInstance().getNeByIds(Collections.singletonList(neId));
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
            return NeDao.getInstance().getNeByName(neName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(500, e.getMessage());
        }
    }
}
