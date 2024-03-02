package com.ktk.workhuservice.repositories;

import com.ktk.workhuservice.data.MentorMentee;
import com.ktk.workhuservice.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorMenteeRepository extends JpaRepository<MentorMentee, Long> {

    List<MentorMentee> findByMentor(User mentor);
}
