package org.elsys.ip.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class Room {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    private String name;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "room")
    private Set<User> participants = new HashSet<>();

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private User admin;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "room")
    private Set<UserAnswer> userAnswers = new HashSet<>();

    private LocalDateTime startedTime;

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public Set<UserAnswer> getUserAnswers() {
        return userAnswers;
    }

    public void setUserAnswers(Set<UserAnswer> userAnswers) {
        this.userAnswers = userAnswers;
    }

    public LocalDateTime getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(LocalDateTime startedTime) {
        this.startedTime = startedTime;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", participants=" + participants +
                ", admin=" + admin +
                '}';
    }
}
