package SpringBoot.Policy_Module_Pro_Max.models;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime activityLogTime; // Time of Log Generation

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(
            mappedBy = "activity",
            cascade = {CascadeType.ALL}
    )
    private Set<ActivityDetail> activityDetails = new HashSet<>();

    private String fileName; // File Name Fetched From File Meta

    private LocalDateTime fileCreationTime; // Creation Time (From File Meta)

    private LocalDateTime fileLastModifiedTime; // Last Modified Time (From File Meta)

    private String policiesCheckedAgainst;
    private String policiesViolated;
    private String policiesNotViolated;

    private String overallResult;

    public Activity() {

    }

    public Activity(LocalDateTime activityLogTime, User user, String fileName, LocalDateTime fileCreationTime, LocalDateTime fileLastModifiedTime, String overallResult) {
        this.activityLogTime = activityLogTime;
        this.user = user;
        this.fileName = fileName;
        this.fileCreationTime = fileCreationTime;
        this.fileLastModifiedTime = fileLastModifiedTime;
        this.overallResult = overallResult;
    }

    public void addActivityDetail(ActivityDetail activityDetail) {
        this.activityDetails.add(activityDetail);
        activityDetail.setActivity(this);
    }

    public void removeActivityDetail(ActivityDetail activityDetail) {
        activityDetails.remove(activityDetail);
        activityDetail.setActivity(null);
    }

    public void addActivityDetails(List<ActivityDetail> activityDetails) {
        this.activityDetails.addAll(activityDetails);
        for(ActivityDetail actDetail : activityDetails) {
            actDetail.setActivity(this);
        }
    }

    public Set<ActivityDetail> getActivityDetails() {
        return activityDetails;
    }

    public void setActivityDetails(Set<ActivityDetail> activityDetails) {
        this.activityDetails = activityDetails;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getActivityLogTime() {
        return activityLogTime;
    }

    public void setActivityLogTime(LocalDateTime activityLogTime) {
        this.activityLogTime = activityLogTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getFileCreationTime() {
        return fileCreationTime;
    }

    public void setFileCreationTime(LocalDateTime fileCreationTime) {
        this.fileCreationTime = fileCreationTime;
    }

    public LocalDateTime getFileLastModifiedTime() {
        return fileLastModifiedTime;
    }

    public void setFileLastModifiedTime(LocalDateTime fileLastModifiedTime) {
        this.fileLastModifiedTime = fileLastModifiedTime;
    }

    public String getPoliciesCheckedAgainst() {
        return policiesCheckedAgainst;
    }

    public void setPoliciesCheckedAgainst(String policiesCheckedAgainst) {
        this.policiesCheckedAgainst = policiesCheckedAgainst;
    }

    public String getPoliciesViolated() {
        return policiesViolated;
    }

    public void setPoliciesViolated(String policiesViolated) {
        this.policiesViolated = policiesViolated;
    }

    public String getPoliciesNotViolated() {
        return policiesNotViolated;
    }

    public void setPoliciesNotViolated(String policiesNotViolated) {
        this.policiesNotViolated = policiesNotViolated;
    }

    public String getOverallResult() {
        return overallResult;
    }

    public void setOverallResult(String overallResult) {
        this.overallResult = overallResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Activity)) return false;
        Activity other = (Activity) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Activity{" +
                "activityLogTime=" + activityLogTime +
                ", fileName='" + fileName + '\'' +
                ", fileCreationTime=" + fileCreationTime +
                ", fileLastModifiedTime=" + fileLastModifiedTime +
                ", policiesCheckedAgainst=" + policiesCheckedAgainst +
                ", policiesViolated=" + policiesViolated +
                ", policiesNotViolated=" + policiesNotViolated +
                ", overallResult='" + overallResult + '\'' +
                '}';
    }
}
