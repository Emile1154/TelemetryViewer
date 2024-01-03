package com.example.telemetryviewer.models.storage;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepthData extends Data {
    private float[] depth;

    public final static int ADDRESSES_START_BYTE = 0;
    public final static int ADDRESSES_END_BYTE = 345600;
}
