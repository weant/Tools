package com.hcop.otn.service;

import com.hcop.otn.common.db.dao.TpDao;
import com.hcop.otn.common.exception.ServiceException;
import com.hcop.otn.restful.model.TP;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Collections;

public class TpService {
    private Log log = LogFactory.getLog(TpService.class);
    private static TpService ourInstance = new TpService();

    public static TpService getInstance() {
        return ourInstance;
    }

    private TpService() {
    }

    public Collection<TP> getTpByNeId(String neId) throws ServiceException {
        try {
            if (neId.trim().length() < 1) {
                throw new ServiceException( 400, "Parameter Error: empty ne id");
            }
            return TpDao.getInstance().getTpsByNeIds(Collections.singletonList(neId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(500, e.getMessage());
        }
    }

    public Collection<TP> getTpByNeIdAndTpId(String neId, String tpId) throws ServiceException {
        try {
            if (neId.trim().length() < 1) {
                throw new ServiceException( 400, "Parameter Error: empty ne id");
            }
            if (tpId.trim().length() < 1) {
                throw new ServiceException( 400, "Parameter Error: empty tp id");
            }
            return TpDao.getInstance().getTpByNeIdAndTpId(neId, tpId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(500, e.getMessage());
        }
    }
}
