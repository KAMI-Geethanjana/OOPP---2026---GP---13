package common.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Notice entity for faculty announcements.
 * Supports both Date and Timestamp createdDate / postedDate representation.
 */
public class Notice {
    private int noticeId;
    private String title;
    private String content;
    private String targetAudience; // 'ALL', 'LECTURER', 'TECH_OFFICER', 'UNDERGRADUATE'
    private Date createdDate;
    private Timestamp postedDate;
    private String postedBy;
    private int postedById;
    private String postedByName;

    public Notice() {}

    public Notice(int noticeId, String title, String content, String targetAudience, Date createdDate, String postedBy) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.targetAudience = targetAudience;
        this.createdDate = createdDate;
        this.postedBy = postedBy;
        if (createdDate != null) {
            this.postedDate = new Timestamp(createdDate.getTime());
        }
        this.postedByName = postedBy;
    }

    public Notice(int noticeId, String title, String content, String targetAudience, int postedById, Timestamp postedDate) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.targetAudience = targetAudience;
        this.postedById = postedById;
        this.postedDate = postedDate;
        if (postedDate != null) {
            this.createdDate = new Date(postedDate.getTime());
        }
        this.postedBy = String.valueOf(postedById);
    }

    public Notice(int noticeId, String title, String content, String targetAudience, int postedById, String postedByName, Timestamp postedDate) {
        this(noticeId, title, content, targetAudience, postedById, postedDate);
        this.postedByName = postedByName;
        this.postedBy = postedByName;
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
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
        if (createdDate != null) {
            this.postedDate = new Timestamp(createdDate.getTime());
        }
    }

    public Timestamp getPostedDate() { return postedDate; }
    public void setPostedDate(Timestamp postedDate) {
        this.postedDate = postedDate;
        if (postedDate != null) {
            this.createdDate = new Date(postedDate.getTime());
        }
    }

    public String getPostedBy() { return postedBy != null ? postedBy : postedByName; }
    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
        this.postedByName = postedBy;
    }

    public int getPostedById() { return postedById; }
    public void setPostedById(int postedById) { this.postedById = postedById; }

    public String getPostedByName() { return postedByName != null ? postedByName : postedBy; }
    public void setPostedByName(String postedByName) {
        this.postedByName = postedByName;
        this.postedBy = postedByName;
    }
}
