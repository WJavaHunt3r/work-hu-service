package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.MentorMentee;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.repositories.MentorMenteeRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MentorMenteeService extends BaseService<MentorMentee, Long> {
    private MentorMenteeRepository repository;

    public MentorMenteeService(MentorMenteeRepository repository) {
        this.repository = repository;
    }

    public List<MentorMentee> findByMentor(User mentor) {
        return repository.findByMentor(mentor);
    }

    @Override
    protected JpaRepository<MentorMentee, Long> getRepository() {
        return repository;
    }

    @Override
    public Class<MentorMentee> getEntityClass() {
        return MentorMentee.class;
    }

    @Override
    public MentorMentee createEntity() {
        return new MentorMentee();
    }
}
