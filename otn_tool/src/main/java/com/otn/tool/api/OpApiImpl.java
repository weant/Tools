package com.otn.tool.api;

import com.otn.tool.model.ConnectionOp;
import com.otn.tool.model.NeTps;
import com.otn.tool.model.SuccessInfo;
import com.otn.tool.model.TpOp;
import org.apache.shiro.util.StringUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

public class OpApiImpl extends OpApiService {
    @Override
    public Response batchGetConnectionOp(List<String> connIds, SecurityContext securityContext) throws NotFoundException {
        try {
            ConnectionOp cop = new ConnectionOp();
            List<ConnectionOp> cops = new ArrayList<>();

            List<TpOp> tops = new ArrayList<>();
            TpOp op = new TpOp();
            op.setNeId("111");
            op.setTpId("222");
            op.setOpr("3.210");
            op.setOpt("1.230");
            tops.add(op);

            cop.setConnectionId("11112");
            cop.setTpOps(tops);

            cops.add(cop);
            return Response.ok().entity(cops).build();
            //return Response.ok().entity(new SuccessInfo().status(true)).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }

    @Override
    public Response batchObtainTpOp(List<NeTps> parameter, SecurityContext securityContext) throws NotFoundException {
        try {
            TpOp op = new TpOp();
            op.setNeId("111");
            op.setTpId("222");
            op.setOpr("3.21");
            op.setOpt("1.23");
            List<TpOp> ops = new ArrayList<>();
            ops.add(op);
            return Response.ok().entity(ops).build();
            //return Response.ok().entity(new SuccessInfo().status(true)).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }
}
