package com.hcop.otn.common.internal.xos.request;

import com.lucent.oms.xml.naInterface.*;

public class GetCurrentPMPRequest extends SingleXosRequest {

	public static final String REQ_OP_NAME = "TPmgr_I_getTPWithAdditionalInfo";

	public static final String REQ_BODY_ROOT = REQ_OP_NAME + "rqst";

	private TPmgr_I_GetTPWithAdditionalInforqst_T body;
	
	public static final String REQ_OP_NAME_NE = "NEmgr_I_getNEParameters";

	public static final String REQ_BODY_ROOT_NE = REQ_OP_NAME_NE + "rqst";

	private NEmgr_I_GetNEParametersrqst_T body_NE;

	public GetCurrentPMPRequest(String ncName, String neName,
			String ptpName, String ftpName, String ctpName) {
		body = buildRequest(ncName, neName, ptpName, ftpName, ctpName);
		init(REQ_OP_NAME, REQ_BODY_ROOT, body);
	}
	
	public GetCurrentPMPRequest( String ncName, String neName, String equName ) {
        body_NE = buildRequest( ncName, neName, equName );
        init( REQ_OP_NAME_NE, REQ_BODY_ROOT_NE, body_NE );
	}
	
	private static TPmgr_I_GetTPWithAdditionalInforqst_T buildRequest(
			String ncName, String neName, String ptpName, String ftpName,
			String ctpName) {
		TPmgr_I_GetTPWithAdditionalInforqst_T req = new TPmgr_I_GetTPWithAdditionalInforqst_T();
		DsCommon_TPname_T tpName = new DsCommon_TPname_T();
		tpName.setNcName(ncName);
		tpName.setNeName(neName);
		tpName.setPtpName(ptpName);
		tpName.setFtpName(ftpName);
		tpName.setCtpName(ctpName);
		req.setTpName(tpName);
		GlobalDefs_NVSList_T additionalInfo = new GlobalDefs_NVSList_T();
		GlobalDefs_NameAndValueString_T vGlobalDefs_NVSList_Telem = new GlobalDefs_NameAndValueString_T();
		vGlobalDefs_NVSList_Telem.setName("retrieveParams");
		vGlobalDefs_NVSList_Telem
				.setValue("EQM&transmitPower&receivePower&totalInputPower&totalOutputPower");
		additionalInfo
				.setGlobalDefs_NVSList_Telem(new GlobalDefs_NameAndValueString_T[] { vGlobalDefs_NVSList_Telem });
		req.setAdditionalInfo( additionalInfo );
		return req;
	}
	
	private static NEmgr_I_GetNEParametersrqst_T buildRequest(String ncName,
                                                              String neName, String equName ) {
		NEmgr_I_GetNEParametersrqst_T req = new NEmgr_I_GetNEParametersrqst_T();
        DsNEmgnt_NEParametersInfo_T neInfo = new DsNEmgnt_NEParametersInfo_T();
        DsCommon_NEname_T neNameT = new DsCommon_NEname_T();
        neNameT.setNcName( ncName );
        neNameT.setNeName( neName );
        neInfo.setNeName( neNameT );
        GlobalDefs_NVSList_T parametersInfo = new GlobalDefs_NVSList_T();
        GlobalDefs_NameAndValueString_T component = new GlobalDefs_NameAndValueString_T();
        component.setName( "component" );
        component.setValue( "EQM-SNA" );
        parametersInfo.addGlobalDefs_NVSList_Telem( component );
        GlobalDefs_NameAndValueString_T entityType = new GlobalDefs_NameAndValueString_T();
        entityType.setName( "entityType" );
        entityType.setValue( "equipment" );
        parametersInfo.addGlobalDefs_NVSList_Telem( entityType );
        GlobalDefs_NameAndValueString_T attributeList = new GlobalDefs_NameAndValueString_T();
        attributeList.setName( "attributeList" );
        attributeList.setValue( "tnCardHighTemperatureThresh,tnCardLowTemperatureThresh,tnCardTemperatureTolerance,tnCardTemperature" );
        parametersInfo.addGlobalDefs_NVSList_Telem( attributeList );
        GlobalDefs_NameAndValueString_T entity = new GlobalDefs_NameAndValueString_T();
        entity.setName( "entity" );
        entity.setValue( equName );
        parametersInfo.addGlobalDefs_NVSList_Telem( entity );
        neInfo.setParametersInfo( parametersInfo );
        req.setNeInfoParams( neInfo );
        return req;
    }
}
