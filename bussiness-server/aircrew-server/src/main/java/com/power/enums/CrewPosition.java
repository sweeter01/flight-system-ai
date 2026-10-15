package com.power.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum CrewPosition {

    PILOT("机长"),
    COPILOT("副机长"),
    FLIGHT_ATTENDANT("乘务员"),
    FLIGHT_ATTENDANT_LEADER("乘务长"),
    FLIGHT_ENGINEER("飞行工程师"),
    OTHER("其他");

    private final String description;
}