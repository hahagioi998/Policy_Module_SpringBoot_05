package SpringBoot.Policy_Module_Pro_Max.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Policy implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String policyName;
    private String remedyType;
    private Integer remedyTime;

    @Column(columnDefinition = "boolean default false")
    private Boolean notifyUser;

    @Column(columnDefinition = "boolean default false")
    private Boolean notifyAdmin;

    private String adminEmail;
    private String adminEmailSubject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "policy_risks",
            joinColumns = @JoinColumn(name = "policy_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "risk_id", referencedColumnName = "id")
    )
    private Set<Risk> risksInvolved = new HashSet<>();

    public Policy() {

    }

    public Policy(String policyName, String remedyType, Integer remedyTime, Boolean notifyUser, Boolean notifyAdmin, String adminEmail, String adminEmailSubject) {
        this.policyName = policyName;
        this.remedyType = remedyType;
        this.remedyTime = remedyTime;
        this.notifyUser = notifyUser;
        this.notifyAdmin = notifyAdmin;
        this.adminEmail = adminEmail;
        this.adminEmailSubject = adminEmailSubject;
    }

    public void addRisk(Risk risk) {
        risksInvolved.add(risk);
        risk.getPoliciesIncludedIn().add(this);
    }

    public void addRisks(Set<Risk> risks) {
        risksInvolved.addAll(risks);
        for(Risk risk : risks) {
            risk.getPoliciesIncludedIn().add(this);
        }
    }

    public void removeRisk(Risk risk) {
        risksInvolved.remove(risk);
        risk.getPoliciesIncludedIn().remove(this);
    }

    public void removeRisks(Set<Risk> risks) {
        risksInvolved.removeAll(risks);
        for(Risk risk : risks) {
            risk.getPoliciesIncludedIn().remove(this);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getRemedyType() {
        return remedyType;
    }

    public void setRemedyType(String remedyType) {
        this.remedyType = remedyType;
    }

    public Integer getRemedyTime() {
        return remedyTime;
    }

    public void setRemedyTime(Integer remedyTime) {
        this.remedyTime = remedyTime;
    }

    public Boolean getNotifyUser() {
        return notifyUser;
    }

    public void setNotifyUser(Boolean notifyUser) {
        this.notifyUser = notifyUser;
    }

    public Boolean getNotifyAdmin() {
        return notifyAdmin;
    }

    public void setNotifyAdmin(Boolean notifyAdmin) {
        this.notifyAdmin = notifyAdmin;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminEmailSubject() {
        return adminEmailSubject;
    }

    public void setAdminEmailSubject(String adminEmailSubject) {
        this.adminEmailSubject = adminEmailSubject;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Set<Risk> getRisksInvolved() {
        return risksInvolved;
    }

    public void setRisksInvolved(Set<Risk> risksInvolved) {
        this.risksInvolved = risksInvolved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Policy)) return false;
        Policy other = (Policy) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Policy{" +
                "policyName='" + policyName + '\'' +
                ", remedyType='" + remedyType + '\'' +
                ", remedyTime=" + remedyTime +
                ", notifyUser=" + notifyUser +
                ", notifyAdmin=" + notifyAdmin +
                ", adminEmail='" + adminEmail + '\'' +
                ", adminEmailSubject='" + adminEmailSubject + '\'' +
                '}';
    }
}
