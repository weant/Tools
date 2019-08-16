package com.hcop.otn.common.internal.xos.response.extractors;

import com.alu.tools.basic.StringUtil;
import com.hcop.otn.common.internal.xos.request.GetPmDataRequest;
import com.hcop.otn.common.internal.xos.request.PmpCurrentData;
import com.lucent.oms.xml.naInterface.DsPMdata_PMDataList_T;
import com.lucent.oms.xml.naInterface.DsPMdata_PMData_T;
import com.lucent.oms.xml.naInterface.DsPMdata_PMMeasurement_T;
import com.lucent.oms.xml.naInterface.Message_T;

import java.util.LinkedList;
import java.util.List;

public class GetPmDataExtractor extends
		AbstractListDataExtractor<PmpCurrentData, DsPMdata_PMDataList_T> {

	@Override
	protected DsPMdata_PMDataList_T getList(Message_T response) {

		if (response.getHdr().getOpName()
				.equals(GetPmDataRequest.REQ_OP_NAME))
			return response.getPerfMgr_I_GetAllCurrentPMData_Siresp()
					.getPmDataList();

		if (response.getHdr().getOpName()
				.equals(GetPmDataRequest.NEXT_REQ_OP_NAME))
			return response.getPerfMgr_I_GetAllCurrentPMData_Sinext_Nresp()
					.getPmDataList();
		return null;
	}

	@Override
	protected PmpCurrentData[] getDataArray(DsPMdata_PMDataList_T list) {

		List<PmpCurrentData> dataList = new LinkedList<PmpCurrentData>();

		for (DsPMdata_PMData_T pmdata : list.getDsPMdata_PMDataList_Telem()) {
			for (DsPMdata_PMMeasurement_T temp : pmdata.getPmMeasurementList()
					.getDsPMdata_PMMeasurementList_Telem()) {

				//"Valid"暂时是固定值，本应为temp.getIntervalStatus()
				dataList.add(new PmpCurrentData(pmdata.getRetrievalTime(), temp
						.getPmpName(),
						temp.getUnit().indexOf(":") >= 0 ? StringUtil
								.findBefore(temp.getUnit(), ":") : temp
								.getUnit(), "Valid", Float.valueOf(temp
								.getValue())));
			}
		}

		return dataList.toArray(new PmpCurrentData[0]);

	}

}
