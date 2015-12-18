package ru.vat78.fotimetracker.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vat on 27.11.2015.
 */
public class FOTT_Timeslot extends FOTT_Object {

    private Date start;
    private long duration;
    private long task_id;
    private long member_id;


    public FOTT_Timeslot(long tsId, String tsTitle){
        setId(tsId);
        setName(tsTitle);
    }

    public Date getStart() {
        return start;
    }

    public long getDuration() {
        return duration;
    }

    public String getDurationString() {
        String res = "";
        Date dur = new Date(duration);
        SimpleDateFormat df = new SimpleDateFormat();

        if (duration >= 24 * 3600 * 1000) res += "" + Math.round(duration / 24 / 3600 / 1000) + " d";
        if (dur.getHours() > 0) res += " " + dur.getHours() + " h";
        if (dur.getMinutes() > 0) res += " " + dur.getMinutes() + " m";

        return res;
    }

    public long getTaskId() {
        return task_id;
    }

    public long getMemberId() {
        return member_id;
    }


    public void setStart(Date start) {
        this.start = start;
    }

    public void setStart(long start) {
        this.start = new Date(start);

    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setTaskId(long task_id) {
        this.task_id = task_id;
    }

    public void setMemberId(long member_id) {
        this.member_id = member_id;
    }

}
