package chat.woowa.woowachat.member;

import chat.woowa.woowachat.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;

import static chat.woowa.woowachat.member.Course.ANDROID;
import static chat.woowa.woowachat.member.Course.BACKEND;
import static chat.woowa.woowachat.member.Course.FRONTEND;
import static jakarta.persistence.EnumType.STRING;

@Entity
public class Member extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(STRING)
    @Column(nullable = false)
    private Course course;

    private Member(final String name, final Course course) {
        this.name = name;
        this.course = course;
    }

    protected Member() {
    }

    public static Member backend(final String name) {
        return new Member(name, BACKEND);
    }

    public static Member frontEnd(final String name) {
        return new Member(name, FRONTEND);
    }

    public static Member android(final String name) {
        return new Member(name, ANDROID);
    }

    public String name() {
        return name;
    }

    public Course course() {
        return course;
    }
}
