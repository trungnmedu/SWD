package com.swd.backend.constance;

public class ApiEndpointProperties {
    public static String[] publicEndpoint = {
            "/api/v1/admin/filter-account",
            "/api/v1/login",
            "/api/v1/register",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/api/v1/districts/**",
            "/api/v1/provinces/**",
            "/api/v1/forgot/send-mail",
            "/api/v1/forgot/confirm-otp",
            "/api/v1/yards/search",
            "/api/v1/yards",
            "/api/v1/yards/{yardId}",
            "/api/v1/slots/get-by-date",
            "/api/v1/sub-yards",
            "/api/v1/sub-yards/**",
            "/api/v1/vote/yards/**",
            "/api/v1/owners/{ownerId}/vouchers",
            "/api/v1/vouchers/{voucherCode}/calculate",
            "/api/v1/vote/yards/{yardId}"
    };
    public static String[] ownerOnlyEndpoint = {
            "/api/v1/owners/me/**"
    };

    public static String[] adminOnlyEndpoint = {
            "/api/v1/admin/owner-register",
            "/api/v1/admin/view-all-user",
            "/api/v1/admin/all-accounts",
            "/api/v1/admin/filter/all-accounts",
            "/api/v1/filter-accounts",
            "/api/v1/admin/accounts/{accountId}",
            "/api/v1/admin/reports",
            "/api/v1/admin/reports/{reportId}",
            "/api/v1/admin/reports/{reportId}/handle",
            "/api/v1/admin/reports/{reportId}/reject"
    };

    public static String[] userOnlyEndpoint = {
            "/api/v1/yards/{yardId}/booking",
            "/api/v1/me/incoming-matches",
            "/api/v1/me/history-booking",
            "/api/v1/me/bookings/{bookingId}",
            "/api/v1/me/report/yards/{yardId}"
    };
}
