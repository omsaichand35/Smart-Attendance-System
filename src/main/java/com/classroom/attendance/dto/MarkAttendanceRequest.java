package com.classroom.attendance.dto;

public class MarkAttendanceRequest {

    private Long sessionId;

    private double latitude;
    private double longitude;

    public Long getSessionId(){ return sessionId; }
    public double getLatitude(){ return latitude; }
    public double getLongitude(){ return longitude; }

    public void setSessionId(Long sessionId){ this.sessionId = sessionId; }
    public void setLatitude(double latitude){ this.latitude = latitude; }
    public void setLongitude(double longitude){ this.longitude = longitude; }
}