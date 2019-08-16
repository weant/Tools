package com.hcop.otn.common.internal.xos.tl1.util;

public class TL1Constants
{
	public static final String EMPTY = "";

	public static final String ALL = "ALL";

	public static final String REPT_DBCHG = "REPT DBCHG";

	public static class MsgDelimiter
	{
		public static final String REQUEST_END = ";";

		public static final char BLANK = ' ';

		public static final String PART_DELIMITER = ":";

		public static final String PART_DELIMITER_REGEX = "(?<!\\\\):";

		public static final String COMMA = ",";

		public static final String AID_DELIMITER = COMMA;

		public static final String POSITIONAL_PARAM_DELIMITER = COMMA;

		public static final String KEYWORD_PARAM_DELIMITER = COMMA;

		public static final String STATE_PARAM_DELIMITER = COMMA;

		public static final String EQUAL = "=";

		public static final String AID_JOINT = "&";

		public static final String RESPONSE_START = "\r\nM" + BLANK + BLANK;

		public static final String NOTIFICATION_START = "\r\nA" + BLANK + BLANK;

		public static final String LINE_START = "\r\n" + BLANK + BLANK + BLANK;

		public static final String LINE_END = "\r\n";

		public static final String DATA_START = "\"";

		public static final String DATA_END = "\"";

		public static final String COMMENT_START = "/*";

		public static final String COMMENT_END = "*/";
	}

	public static class RtrvVerb
	{
		public static final String RTRV_EQPT = "RTRV-EQPT";

		public static final String RTRV_DX_EQPT = "RTRV-DX-EQPT";
		
		public static final String RTRV_HDR = "RTRV-HDR";
		
		public static final String RTRV_DDM = "RTRV-DDM";
		
		public static final String RTRV_LPBK_GBE = "RTRV-LPBK-GBE";
		
		public static final String RTRV_LPBK_GBE10 = "RTRV-LPBK-GBE10";

		public static final String RTRV_IP_ADDR = "RTRV-IP-ADDR";

		public static final String RTRV_LAN = "RTRV-LAN";
		
		public static final String RTRV_GBE10 = "RTRV-GBE10";
		
		public static final String RTRV_RI = "RTRV-RI";
		public static final String RTRV_SYNCN = "RTRV-SYNCN";

	}

	public static class ErrorCode
	{
		public static final String INPUT_INVALID_ACCESS_IDENTIFIER = "IIAC";

		public static final String EQUIPMENT_WRONG_TYPE = "EQWT";

		public static final String INPUT_DATA_NOT_CONSISTENT = "IDNC";

		public static final String INPUT_ENTITY_ALREADY_EXISTS = "IEAE";

		public static final String INPUT_PARAMETER_NOT_CONSISTENT = "IPNC";

		public static final String STATUS_NOT_IN_VALID_STATE = "SNVS";

		public static final String STATUS_REQUESTED_OPERATION_FAILED = "SROF";
	}

	public static class Keyword
	{
		public static final String PROVISIONED_TYPE = "PROVISIONEDTYPE";

		public static final String ACTUAL_TYPE = "ACTUALTYPE";
	}
}
