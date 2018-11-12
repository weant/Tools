package com.otn.tool.common.internal.xos.request;

import com.lucent.oms.xml.naInterface.*;

public class GetPmDataRequest extends MultiXosRequest {

	public static final String REQ_OP_NAME = "PerfMgr_I_getAllCurrentPMData_si";

	public static final String REQ_BODY_ROOT = "PerfMgr_I_getAllCurrentPMData_sirqst";

	public static final String NEXT_REQ_OP_NAME = "PerfMgr_I_getAllCurrentPMData_sinext_n";

	public static final String NEXT_REQ_BODY_ROOT = "PerfMgr_I_getAllCurrentPMData_Sinext_Nrqst";

	private PerfMgr_I_GetAllCurrentPMData_Sirqst_T body;

	public GetPmDataRequest(String ncName, String neName,
			String ptpName, String ftpName, String ctpName, String tcmpName,int rate,
			int pmLocation, int granularity, int direction) {

		body = buildReqBody(ncName, neName, ptpName, ftpName, ctpName,tcmpName, rate,
				pmLocation, granularity, direction);

		init(REQ_OP_NAME, REQ_BODY_ROOT, NEXT_REQ_OP_NAME, NEXT_REQ_BODY_ROOT,
				body);
	}
	

	/**
	 * 获取request body的方法
	 * 
	 * @param ncName 用于构建tpName
	 * @param neName 用于构建tpName
	 * @param ptpName 用于构建tpName
	 * @param ftpName 用于构建tpName
	 * @param ctpName 用于构建tpName
	 * @param tcmpName 
	 * @param rate 用于构建layerRateList
	 * @param pmLocation 用于构建pmLocationList
	 * @param granularity 用于构建granularityList
	 * @param direction 用于构建pmDirectionList
	 * @return PerfMgr_I_GetAllCurrentPMData_Sirqst_T的对象
	 */
	private static PerfMgr_I_GetAllCurrentPMData_Sirqst_T buildReqBody(
			String ncName, String neName, String ptpName, String ftpName,
			String ctpName, String tcmpName, int rate, int pmLocation, int granularity,
			int direction) {

		// 构建tpName
		DsCommon_TPname_T tpName = new DsCommon_TPname_T();
		tpName.setNcName(ncName);
		tpName.setNeName(neName);
		tpName.setPtpName(ptpName);
		tpName.setFtpName(ftpName);
		tpName.setCtpName(ctpName);
		tpName.setTcmName(tcmpName);

		// 构建layerRateList
		LayerDefs_LayerRateList_T layerRateList = new LayerDefs_LayerRateList_T();
		layerRateList.addLayerDefs_LayerRateList_Telem((short) rate);

		// 构建pmLocationList
		DsPMdata_PMLocationList_T pmLocationList = new DsPMdata_PMLocationList_T();
		pmLocationList.addDsPMdata_PMLocationList_Telem(PmpPropertyConvert
				.toXosLocation(pmLocation));

		// 构建granularityList
		DsPMdata_GranularityList_T granularityList = new DsPMdata_GranularityList_T();
		granularityList.addDsPMdata_GranularityList_Telem(PmpPropertyConvert
				.toXosGranularity(granularity));

		// 构建pmDirectionList
		DsPMdata_PMDirectionList_T pmDirectionList = new DsPMdata_PMDirectionList_T();
		pmDirectionList.addDsPMdata_PMDirectionList_Telem(PmpPropertyConvert
				.toXosDirectionList(direction));
		
		// 构建additionalInfo
		GlobalDefs_NVSList_T additionalInfo = new GlobalDefs_NVSList_T();

		// 构建dsPMdata_PMTPSelectList_Telem
		DsPMdata_PMTPselect_T dsPMdata_PMTPSelectList_Telem = new DsPMdata_PMTPselect_T();
		dsPMdata_PMTPSelectList_Telem.setName(tpName);
		dsPMdata_PMTPSelectList_Telem.setLayerRateList(layerRateList);
		dsPMdata_PMTPSelectList_Telem.setPmLocationList(pmLocationList);
		dsPMdata_PMTPSelectList_Telem.setGranularityList(granularityList);
		dsPMdata_PMTPSelectList_Telem.setPmDirectionList(pmDirectionList);
		dsPMdata_PMTPSelectList_Telem.setAdditionalInfo(additionalInfo);

		// 构建pmTPSelectList
		DsPMdata_PMTPSelectList_T pmTPSelectList = new DsPMdata_PMTPSelectList_T();
		pmTPSelectList
				.addDsPMdata_PMTPSelectList_Telem(dsPMdata_PMTPSelectList_Telem);

		// 构建req
		DsPerfMgnt_GetAllCurrentPMdataReq_T req = new DsPerfMgnt_GetAllCurrentPMdataReq_T();
		req.setPmParameters(new DsPMdata_PMParameterNameList_T());
		req.setPmTPSelectList(pmTPSelectList);

		// 构建siRqst
		PerfMgr_I_GetAllCurrentPMData_Sirqst_T siRqst = new PerfMgr_I_GetAllCurrentPMData_Sirqst_T();
		siRqst.setHow_Many(XosConstants.Request.HOW_MANY);
		siRqst.setReq(req);

		return siRqst;
	}
	
	
	@Override
	public String getNextReqBody() {

		return "<" + NEXT_REQ_BODY_ROOT + "><how_namy>"
				+ XosConstants.Request.HOW_MANY + "</how_namy></"
				+ NEXT_REQ_BODY_ROOT + ">";
	}

}
