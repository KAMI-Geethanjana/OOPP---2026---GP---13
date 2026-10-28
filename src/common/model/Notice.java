package common.model;

import java.sql.Date;

/**
 * Notice entity for faculty announcements.
 */
public class Notice {
    private int noticeId;
    private String title;
    private String content;
    private String targetAudience; // 'ALL', 'LECTURER', 'TECH_OFFICER', 'UNDERGRADUATE'
    private Date createdDate;
    private String postedBy;

    public Notice() {}

    public Notice(int noticeId, String title, String content, String targetAudience, Date createdDate, String postedBy) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.targetAudience = targetAudience;
        this.createdDate = createdDate;
        this.postedBy = postedBy;
    }

    public int getNoticeId() { return noticeId; }
    public void setNoticeId(int noticeId) { this.noticeId = noticeId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public String getPostedBy() { return postedBy; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }
}
