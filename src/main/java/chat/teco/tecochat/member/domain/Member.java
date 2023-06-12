package chat.teco.tecochat.member.domain;

import static jakarta.persistence.EnumType.STRING;

import chat.teco.tecochat.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;

@Entity
public final class Member extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(STRING)
    @Column(nullable = false)
    private Course course;

    protected Member() {
    }

    public Member(Long id, String name, Course course) {
        super(id);
        this.name = name;
        this.course = course;
    }

    public Member(String name, Course course) {
        this.name = name;
        this.course = course;
    }

    public String name() {
        return name;
    }

    public Course course() {
        return course;
    }

    public void changeCourse(final Course course) {
        this.course = course;
    }
}
