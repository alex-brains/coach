package by.alex.coach.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "topics", uniqueConstraints = {
        @UniqueConstraint(name = "uq_topic_name_parent", columnNames = {"name", "parent_id"})
})
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Topic parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Topic> children = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Topic getParent() {
        return parent;
    }

    public void setParent(Topic parent) {
        this.parent = parent;
    }

    public List<Topic> getChildren() {
        return children;
    }

    public void setChildren(List<Topic> children) {
        this.children = children;
    }
}
