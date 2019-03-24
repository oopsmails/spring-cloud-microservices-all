package com.oopsmails.spring.cloud.microservices.employeeservice.filter;

public enum FilterOrder {
    FILTER_OAUTH2_VALIDATION(Constants.FILTER_OAUTH2_VALIDATION_VALUE),
    FILTER_HTTP_LOGGING(Constants.FILTER_HTTP_LOGGING_VALUE);

    private int order;

    FilterOrder(int order) {
        this.order = order;
    }

    public static FilterOrder fromOrder(int order) {
        for (FilterOrder v : values()) {
            if (order == v.getOrder()) {
                return v;
            }
        }
        return null;
    }

    public int getOrder() {
        return order;
    }

    public static class Constants {
        public static final int FILTER_OAUTH2_VALIDATION_VALUE = 1;
        public static final int FILTER_HTTP_LOGGING_VALUE = 2;
    }
}
