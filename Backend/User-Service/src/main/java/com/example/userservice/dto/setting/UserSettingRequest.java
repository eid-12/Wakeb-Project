package com.example.userservice.dto.setting;

import lombok.Data;

@Data
public class UserSettingRequest {

    private String  language;
    private String  theme;
    private String  fontSize;
    private Boolean savedPlacesNotifications;
    private Boolean locationTracking;
    private Boolean autoDeleteHistory;
    private Boolean exportToCsv;



}
