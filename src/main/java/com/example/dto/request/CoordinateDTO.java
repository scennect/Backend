package com.example.dto.request;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CoordinateDTO {

    private long nodeId;
    private int x;
    private int y;
}
