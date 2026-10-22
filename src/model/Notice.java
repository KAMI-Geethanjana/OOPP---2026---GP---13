package model;

import java.sql.Timestamp;

/**
 * Notice model representing administrative announcements.
 */
public class Notice {
    private int noticeId;
    private String title;
    private String content;
    private String targetAudience;
    private int postedBy;
    private String postedByName;
    private Timestamp postedDate;

    public Notice() {}

    public Notice(int noticeId, String title, String content, String targetAudience, int postedBy, Timestamp postedDate) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.targetAudience = targetAudience;
        this.postedBy = postedBy;
        this.postedDate = postedDate;
    }

    public Notice(int noticeId, String title, String content, String targetAudience, int postedBy, String postedByName, Timestamp postedDate) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.targetAudience = targetAudience;
        this.postedBy = postedBy;
        this.postedByName = postedByName;
        this.postedDate = postedDate;
    }

    // Getters and Setters
    public int getNoticeId() { return noticeId; }
    public void setNoticeId(int noticeId) { this.noticeId = noticeId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public int getPostedBy() { return postedBy; }
    public void setPostedBy(int postedBy) { this.postedBy = postedBy; }

    public String getPostedByName() { return postedByName; }
    public void setPostedByName(String postedByName) { this.postedByName = postedByName; }

    public Timestamp getPostedDate() { return postedDate; }
    public void setPostedDate(Timestamp postedDate) { this.postedDate = postedDate; }
}
