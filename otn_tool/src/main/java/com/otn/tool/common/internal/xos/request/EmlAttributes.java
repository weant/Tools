package com.otn.tool.common.internal.xos.request;

public class EmlAttributes
{
    public static class NE
    {
        public static enum CommunicationState {
            available, partiallyUnavailable, unavailable, notProvided
        }

        public static enum MibAlignmentState {
            aligned, misaligned, notProvided
        }

        public static enum SupervisionState {
            declared, activating, supervised, deactivating, notProvided
        }

        public static enum AlarmStatus {
            cleared, indeterminate, warning, minor, major, critical, activePending, notProvided
        }
    }

    public static class Equipment
    {
        public static enum HolderType {
            rack, subrack, slot, subslot, notProvided
        }

        public static enum HolderState {
            EMPTY, INSTALLED_AND_EXPECTED, EXPECTED_AND_NOT_INSTALLED, INSTALLED_AND_NOT_EXPECTED, MISMATCH_OF_INSTALLED_AND_EXPECTED, UNAVAILABLE, UNKNOWN
        }

        public static enum ServiceState {
            inService, outOfService, outOfServiceByMaintenance, notProvided
        }
        
        public static String RACK_NAME = "/rack=1";
    }

    public static class ASAP
    {
        public static enum AssignedSeverity {
            indeterminate, critical, major, minor, warning, nonalarmed, freeChoice
        }
    }

    public static class PMP
    {
        public static enum PmLocation {
            NEAR_END_Rx, FAR_END_Rx, NEAR_END_Tx, FAR_END_Tx, BIDIRECTIONAL, CONTRA_NEAR_END_Rx, CONTRA_FAR_END_Rx, NA
        }

        public static enum PmGranularity {
            _15min, _1h, _24h, NA
        }

        public static enum SnmpPmDomain {
            EthPort, Flow
        }
    }

    public static class EPG
    {
        public static enum ProtectionGroupType {
            plus, colon, NA
        }

        public static enum ReversionMode {
            unknown, nonRevertive, revertive
        }

        public static enum SwitchReason {
            manualSwitch, autoSwitch, forcedSwitch, lockout, unknown
        }
    }
}
