package com.ktk.workhuservice.data.paceteam;

import com.ktk.workhuservice.service.BaseService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaceTeamService extends BaseService<PaceTeam, Long> {
    private PaceTeamRepository repository;

    public PaceTeamService(PaceTeamRepository repository) {
        this.repository = repository;
    }

    public Optional<PaceTeam> findByTeamName(String teamName){
        return repository.findByTeamName(teamName);
    }

    @Override
    protected JpaRepository<PaceTeam, Long> getRepository() {
        return repository;
    }

    @Override
    public Class<PaceTeam> getEntityClass() {
        return PaceTeam.class;
    }

    @Override
    public PaceTeam createEntity() {
        return new PaceTeam();
    }
}
