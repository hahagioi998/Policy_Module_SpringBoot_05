package SpringBoot.Policy_Module_Pro_Max.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Remedy {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Activity activity;

    private LocalDateTime actionTime;
    private String actionType;

    // Have To Store These 4 As There Is No Mapping To Policies
    private String userEmail;
    private String adminEmails;
    private String adminEmailSubjects;
    private String filePath;

    @Column(columnDefinition = "boolean default false")
    private Boolean status;

    public Remedy() {

    }

    public Remedy(LocalDateTime actionTime, String actionType, Boolean status) {
        this.actionTime = actionTime;
        this.actionType = actionType;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public LocalDateTime getActionTime() {
        return actionTime;
    }

    public void setActionTime(LocalDateTime actionTime) {
        this.actionTime = actionTime;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAdminEmails() {
        return adminEmails;
    }

    public void setAdminEmails(String adminEmails) {
        this.adminEmails = adminEmails;
    }

    public String getAdminEmailSubjects() {
        return adminEmailSubjects;
    }

    public void setAdminEmailSubjects(String adminEmailSubjects) {
        this.adminEmailSubjects = adminEmailSubjects;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Remedy)) return false;
        Remedy other = (Remedy) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Action{" +
                ", actionTime=" + actionTime +
                ", actionType='" + actionType + '\'' +
                ", status=" + status +
                '}';
    }
}

