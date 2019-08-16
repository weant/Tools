package com.hcop.ptn.service;

import com.hcop.ptn.common.db.dao.TpDao;
import com.hcop.ptn.common.exception.ServiceException;
import com.hcop.ptn.restful.model.TP;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;

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
            return TpDao.getInstance().getTps(neId, "");
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
            return TpDao.getInstance().getTps(neId, tpId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(500, e.getMessage());
        }
    }

    // TODO: 2019/2/16 设置端口所属板卡的类型
}
