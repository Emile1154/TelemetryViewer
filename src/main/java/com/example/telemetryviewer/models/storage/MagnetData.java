package com.example.telemetryviewer.models.storage;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MagnetData extends Data {
    private byte[] magnet;
    public final static int ADDRESSES_START_BYTE = 518400;
    public final static int ADDRESSES_END_BYTE = 604800;
}
