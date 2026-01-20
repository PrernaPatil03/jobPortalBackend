package com.jobportal.dto;
public enum VerificationStatus {
    PENDING,          // verification email sent, waiting for company click
    APPROVED,         // company verified
    REJECTED ,
    NEW// company did not verify in time or manually rejected
}
