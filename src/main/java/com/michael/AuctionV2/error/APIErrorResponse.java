package com.michael.AuctionV2.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class APIErrorResponse {
    private Integer status;
    private String message;
}
