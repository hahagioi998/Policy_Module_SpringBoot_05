package SpringBoot.Policy_Module_Pro_Max.models;

import javax.persistence.*;

@Entity
public class ActivityDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Activity activity;

    private String policy;

    private String policyResult;

    private String risksCheckedAgainst;
    private String risksViolated;
    private String risksNotViolated;

    public ActivityDetail() {

    }

    public ActivityDetail(String policyResult) {
        this.policyResult = policyResult;
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

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getRisksCheckedAgainst() {
        return risksCheckedAgainst;
    }

    public void setRisksCheckedAgainst(String risksCheckedAgainst) {
        this.risksCheckedAgainst = risksCheckedAgainst;
    }

    public String getRisksViolated() {
        return risksViolated;
    }

    public void setRisksViolated(String risksViolated) {
        this.risksViolated = risksViolated;
    }

    public String getRisksNotViolated() {
        return risksNotViolated;
    }

    public void setRisksNotViolated(String risksNotViolated) {
        this.risksNotViolated = risksNotViolated;
    }

    public String getPolicyResult() {
        return policyResult;
    }

    public void setPolicyResult(String policyResult) {
        this.policyResult = policyResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActivityDetail)) return false;
        ActivityDetail other = (ActivityDetail) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ActivityDetail{" +
                ", policyResult='" + policyResult + '\'' +
                ", risksCheckedAgainst=" + risksCheckedAgainst +
                ", risksViolated=" + risksViolated +
                ", risksNotViolated=" + risksNotViolated +
                '}';
    }
}

