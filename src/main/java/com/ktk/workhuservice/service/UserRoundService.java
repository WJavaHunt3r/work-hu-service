package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Round;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.data.UserRound;
import com.ktk.workhuservice.repositories.UserRoundRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserRoundService {
    private UserRoundRepository userRoundRepository;

    public UserRoundService(UserRoundRepository userRoundRepository) {
        this.userRoundRepository = userRoundRepository;
    }

    public Optional<UserRound> findByUserAndRound(User u, Round r){
        return userRoundRepository.findByUserAndRound(u, r);
    }

    public UserRound save(UserRound userRound){
        return userRoundRepository.save(userRound);
    }

    public Optional<UserRound> findById(Long id){
        return userRoundRepository.findById(id);
    }

    public UserRound createUserRound(User user, Round round){
        UserRound newUserRound = new UserRound();
        newUserRound.setRound(round);
        newUserRound.setUser(user);
        return save(newUserRound);
    }
}
