package com.swd.backend.constance;

public class YardReportStatus {
    public static final String REPORT_PENDING = "PENDING";
    public static final String REPORT_HANDLED = "HANDLED";
    public static final String REPORT_REJECTED = "REJECTED";

    public static String[] getAllStatus() {
        return new String[]{"PENDING", "HANDLED", "REJECTED"};
    }
}
