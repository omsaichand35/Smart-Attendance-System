package com.classroom.attendance.dto;

public class CreateSessionRequest {
    
    private Long courseId;

    private double latitude;
    private double longitude;
    private double radius;

    private String startTime;
    private String endTime;

    public Long getCourseId(){ return courseId; }
    public double getLatitude(){ return latitude; }
    public double getLongitude(){ return longitude; }
    public double getRadius(){ return radius; }
    public String getStartTime(){ return startTime; }
    public String getEndTime(){ return endTime; }

    public void setCourseId(Long courseId){ this.courseId = courseId; }
    public void setLatitude(double latitude){ this.latitude = latitude; }
    public void setLongitude(double longitude){ this.longitude = longitude; }
    public void setRadius(double radius){ this.radius = radius; }
    public void setStartTime(String startTime){ this.startTime = startTime; }
    public void setEndTime(String endTime){ this.endTime = endTime; }

}
