package com.ktk.workhuservice.data.mentormentee;

import com.ktk.workhuservice.data.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorMenteeRepository extends JpaRepository<MentorMentee, Long> {

    List<MentorMentee> findByMentor(User mentor);
}
