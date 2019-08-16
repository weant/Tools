package com.hcop.otn.common.internal.xos.request;

public class XosConstants
{
    public static final String NAMESSAGE_TAG = "namessage";

    public static class Request
    {
        public static final int HOW_MANY = 100;
    }

    public static class NE
    {
        public static final String NE_ID = "NEID";

        public static final String NE_GROUP_ID = "NEGROUPID";

        public static final String USER_LABEL = "userLabel";

        public static final String COMMUNICATION_STATE = "communicationState";

        public static final String MIB_ALIGNMENT_STATE = "mibAlignmentState";

        public static final String SUPERVISION_STATE = "supervisionState";

        public static final String ALARM_STATUS = "alarmStatus";

        public static final String LOCATION_NAME = "locationName";

        public static class CommunicationState
        {
            public static final String DISABLED = "CS_DEACTIVATED";

            public static final String ENABLED = "CS_UP";
        }

        public static class MibAlignmentState
        {
            public static final String ALIGNING_UP = "ALIGNINGUP";

            public static final String ALIGNING_DOWN = "ALIGNINGDOWN";

            public static final String MIS_ALIGNED = "MISALIGNED";

            public static final String IN_CONFIGURATION = "INCONFIGURATION";

            public static final String ALIGNED = "ALIGNED";

            public static final String AUDITING = "AUDITING";
        }

        public static class SupervisionState
        {
            public static final String DECLARED = "DECLARED";

            public static final String ACTIVATING = "ACTIVATING";

            public static final String SUPERVISED = "SUPERVISED";

            public static final String DEACTIVATING = "DEACTIVATING";
        }

        public static class AlarmStatus
        {
            public static final String CLEARED = "CLEARED";

            public static final String INDETERMINATE = "INDETERMINATE";

            public static final String WARNING = "WARNING";

            public static final String MINOR = "MINOR";

            public static final String MAJOR = "MAJOR";

            public static final String CRITICAL = "CRITICAL";
        }
    }

    public static class PMP
    {
        public static final String PMTP_NATIVE_NAME = "PMTPnativeName";
    }

    public static class TP
    {
        public static final String ASAP = "asap";

        public static final String TCA = "tca";

        public static final String ALARM_REPORTING = "alarmReporting";

        public static final String TCA_DEFAULT = "DEFAULT";

        public static final String TCA_NULL = "NULL";

        public static final String AINSTIMER = "ainstimer";

        public static final String AINSTH = "ainsth";

        public static final String ALARMREPORT = "alarmReport";

        public static final String PRIMARYSTATE = "primarystate";

        public static final String TNPORT_ETHER_DUPLEX = "tnPortEtherDuplex";

        public static final String TNPORT_ETHER_MTU = "tnPortEtherMTU";

        public static final String TNPORT_ETHER_AUTO_NEGOTIATE = "tnPortEtherAutoNegotiate";

        public static final String TNPORT_UPLINK_MODE = "tnPortUplinkMode";

        public static final String TNPORT_ETHER_EGRESS_RATE = "tnPortEtherEgressRate";
        
        public static final String TNPORT_ETHER_OPER_SPEED = "tnPortEtherOperSpeed";

        public static final String TNPORT_ETHER_SPEED = "tnPortEtherSpeed";

        public static final String SECONDARYSTATE = "secondarystate";

        public static final String LASERSTATE = "LaserState";

        public static final String FREQUENCY = "Frequency";

        public static final String TRAIL_TRACE_EXPECTED_RX = "TrailTraceExpectedRx";

        public static final String TRAIL_TRACE_ACTUAL_TX = "TrailTraceActualTx";

        public static final String EG_TRAIL_TRACE_EXPECTED_RX = "EgTrailTraceExpectedRx";

        public static final String SIGNALTYPE = "signalType";
        
        public static final String CONTAINER = "container";
        
        public static final String PAYLOAD_TYPE = "payloadType";

        public static final String PAYLOAD_TYPE_LIST = "pldtypecap";

        public static final String TN_AMPLIFIER_PORT_VOASET = "tnAmplifierPortVoaSet";

        public static final String TN_AMPLIFIER_MESHCARD_VOASET = "tnAmplifierMeshCardVoaSet";

        public static final String TN_DWDMCMN_CLIENT_PORTLOS_PROP = "tnDwdmCmnClientPortLosProp";

        public static final String CSF = "Csf";

        public static final String SSF = "Ssf";

        public static final String TRAIL_TRACE_EXPECTED_DEST_RX = "TrailTraceExpectedDestRx";

        public static final String TRAIL_TRACE_ACTUAL_DEST_TX = "TrailTraceActualDestTx";

        public static final String EG_TRAIL_TRACE_EXPECTED_DEST_RX = "EgTrailTraceExpectedDestRx";

        public static final String TRAIL_TRACE_MONITOR = "TrailTraceMonitor";

        public static final String EG_TRAIL_TRACE_MONITOR = "EgTrailTraceMonitor";

        public static final String TRAIL_TRACE_ACTUAL_RX = "TrailTraceActualRx";

        public static final String TRAIL_TRACE_ACTUAL_RX_PHN = "TrailTraceActualRX";

        public static final String TRAIL_TRACE_ACTUAL_DEST_RX = "TrailTraceActualDestRx";

        public static final String GAIN = "gain";

        public static final String TN_AMPLIFIER_PORTPOWER_GAIN = "tnAmplifierPortPowerGain";

        public static final String MAXGAIN = "maxGain";

        public static final String MINGAIN = "minGain";

        public static final String VOASET = "voaSet";

        public static final String TOTAL_OUTPUT_POWER = "totalOutputPower";

        public static final String TOTAL_INPUT_POWER = "totalInputPower";

        public static final String RECEIVE_POWER = "receivePower";

        public static final String TRANSMIT_POWER = "transmitPower";

        public static final String LaserState = "LaserState";

        public static final String PAYLOAD_STRUCT = "payloadStruct";

        public static final String CSFPROP = "csfprop";

        public static final String POM = "Pom";
        
        public static final String EGPOM = "egPom";
        
        public static final String Encapsulation_Mode = "EncapsulationMode";

        public static final String TN_PORT_ETHER_MTU = "tnPortEtherMTU";

        public static final String TN_PORT_ETHER_AUTO_NEGOTIATE = "tnPortEtherAutoNegotiate";

        public static final String TN_PORT_UPLINK_MODE = "tnPortUplinkMode";

        public static final String TN_PORT_ETHER_SPEED = "tnPortEtherSpeed";
    }

    public static class TCA
    {
        public static final String MONTYPE = "montype";

        public static final String LOCATION = "location";

        public static final String DIRECTION = "direction";

        public static final String GRANULARITY = "granularity";

        public static final String THRESHOLD_LEVEL = "thresholdLevel";

        public static final String THRESHOLD_LEVEL_CLEAR = "thresholdLevelClear";
    }
}
