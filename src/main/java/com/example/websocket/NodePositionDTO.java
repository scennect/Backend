package com.example.websocket;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NodePositionDTO {

    private Long NodeId;
    private int x;
    private int y;
}
