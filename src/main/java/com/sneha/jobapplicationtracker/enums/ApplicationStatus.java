
package com.sneha.jobapplicationtracker.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ApplicationStatus {

    APPLIED("Applied"),
    INTERVIEW("Interview"),
    OFFERED("Offered"),
    REJECTED("Rejected"),
    WITHDRAWN("Withdrawn");

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static ApplicationStatus fromValue(String value) {

        if (value == null) {
            return null;
        }

        for (ApplicationStatus status : values()) {

            if (status.name().equalsIgnoreCase(value.trim())
                    || status.displayName.equalsIgnoreCase(value.trim())) {

                return status;
            }
        }

        throw new IllegalArgumentException(
                "Invalid status. Allowed values: Applied, Interview, Offered, Rejected, Withdrawn"
        );
    }
}
