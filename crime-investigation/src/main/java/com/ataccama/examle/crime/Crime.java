package com.ataccama.examle.crime;

import java.time.LocalDate;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class Crime {
    private String id;
    private String caseNumber;
    private LocalDate date;
    private String block;
    private String iucr;
    private String primaryType;
    private String description;
    private String locationDescription;
    private Boolean arrest;
    private Boolean domestic;
    private String beat;
    private String district;
    private Integer ward;
    private Integer communityArea;
    private String fbiCode;
    private Integer xCoordinate;
    private Integer yCoordinate;
    private Integer year;
    private LocalDate updatedOn;
    private Double latitude;
    private Double longitude;
    private String location;

    public Crime(String[] args) {
        this.id = args[0].isEmpty() ? null : args[0];
        this.caseNumber = args[1].isEmpty() ? null : args[1];
        this.date = args[2].isEmpty() ? null : LocalDate.parse(args[2], Utils.AMERICAN_FORMAT);
        this.block = args[3].isEmpty() ? null : args[3];
        this.iucr = args[4].isEmpty() ? null : args[4];
        this.primaryType = args[5].isEmpty() ? null : args[5];
        this.description = args[6].isEmpty() ? null : args[6];
        this.locationDescription = args[7].isEmpty() ? null : args[7];
        this.arrest = args[8].isEmpty() ? null : args[8].startsWith("t");
        this.domestic = args[9].isEmpty() ? null : args[9].startsWith("t");
        this.beat = args[10].isEmpty() ? null : args[10];
        this.district = args[11].isEmpty() ? null : args[11];
        this.ward = args[12].isEmpty() ? null : Integer.valueOf(args[12]);
        this.communityArea = args[13].isEmpty() ? null : Integer.valueOf(args[13]);
        this.fbiCode = args[14].isEmpty() ? null : args[14];
        this.xCoordinate = args[15].isEmpty() ? null : Integer.valueOf(args[15]);
        this.yCoordinate = args[16].isEmpty() ? null : Integer.valueOf(args[16]);
        this.year = args[17].isEmpty() ? null : Integer.valueOf(args[17]);
        this.updatedOn = args[18].isEmpty() ? null : LocalDate.parse(args[18], Utils.AMERICAN_FORMAT);
        this.latitude = args[19].isEmpty() ? null : Double.parseDouble(args[19]);
        this.longitude = args[20].isEmpty() ? null : Double.parseDouble(args[20]);
        this.location = args[21].isEmpty() ? null : args[21];
    }
}
