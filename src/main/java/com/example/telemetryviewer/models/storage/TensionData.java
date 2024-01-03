package com.example.telemetryviewer.models.storage;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TensionData extends Data {
    private short[] tension;

    public final static int ADDRESSES_START_BYTE = 345600;
    public final static int ADDRESSES_END_BYTE = 518400;
}
