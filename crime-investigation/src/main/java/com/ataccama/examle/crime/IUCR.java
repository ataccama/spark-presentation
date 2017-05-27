package com.ataccama.examle.crime;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Builder
@RequiredArgsConstructor
public class IUCR {
    private String code;
    private String primary;
    private String secondary;
    private boolean index;

    public IUCR(String[] args) {
        this.code = Utils.padLeftZeros(args[0], 4);
        this.primary = args[1];
        this.secondary = args[2];
        this.index = args[3].startsWith("I");
    }
}
