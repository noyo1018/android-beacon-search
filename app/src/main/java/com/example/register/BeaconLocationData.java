package com.example.register;

import java.util.HashMap;
import java.util.Map;

public class BeaconLocationData {
    public  Map<String, String> locations =new HashMap<>();

    public BeaconLocationData() {
        initLocationData();
    }
    private void initLocationData() {

        locations.put("51A907AD-D117-2FA1-C44B-F573F4390AE2", "某某餐廳");
    }

    public String getLocationMsg(String UUID) {
        String location;
//        location = locations.get(major).get(minor);

        location = locations.get(UUID);
        if (location == null || location.equals("")) {
            return "暫無位置信息";
        }
        return location;
    }

}
