package SpringBoot.Policy_Module_Pro_Max.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Risk implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "boolean default false")
    private Boolean status;

    private String keyWords;
    private String regex;
    private String description;

    @Column(columnDefinition = "integer default 1")
    private Integer riskMatchCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToMany(
            mappedBy = "risksInvolved",
            fetch = FetchType.LAZY
    )
    private Set<Policy> policiesIncludedIn = new HashSet<>();


    public Risk() {

    }

    public Risk(String title, Boolean status, String keyWords, String regex, String description, Integer riskMatchCount) {
        this.title = title;
        this.status = status;
        this.keyWords = keyWords;
        this.regex = regex;
        this.description = description;
        this.riskMatchCount = riskMatchCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRiskMatchCount() {
        return riskMatchCount;
    }

    public void setRiskMatchCount(Integer riskMatchCount) {
        this.riskMatchCount = riskMatchCount;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Set<Policy> getPoliciesIncludedIn() {
        return policiesIncludedIn;
    }

    public void setPoliciesIncludedIn(Set<Policy> policiesIncludedIn) {
        this.policiesIncludedIn = policiesIncludedIn;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Risk)) return false;
        Risk other = (Risk) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }

    @Override
    public String toString() {
        return "Risk{" +
                "title='" + title + '\'' +
                ", status=" + status +
                ", keyWords='" + keyWords + '\'' +
                ", regex='" + regex + '\'' +
                ", description='" + description + '\'' +
                ", riskMatchCount=" + riskMatchCount +
                '}';
    }
}


