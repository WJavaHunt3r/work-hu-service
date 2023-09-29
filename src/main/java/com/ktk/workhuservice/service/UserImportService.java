package com.ktk.workhuservice.service;

import com.ktk.workhuservice.data.Season;
import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.enums.Role;
import com.ktk.workhuservice.enums.TeamColor;
import com.ktk.workhuservice.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserImportService {
    private static final String USER_DATA_URL = "C:\\workspace\\private\\work-hu-service\\src\\main\\resources\\imports\\myshare\\Pmo_lista.csv";
    private static final Map<String, String> ENGLISH_CHARS = new HashMap();

    private UserService userService;
    private TeamService teamService;
    private SeasonService seasonService;

    public UserImportService(UserService userService, TeamService teamService, SeasonService seasonService) {
        this.userService = userService;
        this.teamService = teamService;
        this.seasonService = seasonService;
    }

    static {
        ENGLISH_CHARS.put("á", "a");
        ENGLISH_CHARS.put("é", "e");
        ENGLISH_CHARS.put("í", "i");
        ENGLISH_CHARS.put("ó", "o");
        ENGLISH_CHARS.put("ö", "o");
        ENGLISH_CHARS.put("ő", "o");
        ENGLISH_CHARS.put("ú", "u");
        ENGLISH_CHARS.put("ü", "u");
        ENGLISH_CHARS.put("ű", "u");
        ENGLISH_CHARS.put(" ", "");
        ENGLISH_CHARS.put("(", "");
        ENGLISH_CHARS.put(")", "");
        ENGLISH_CHARS.put("\\.", "");
    }

    public void importUsersFromCsv() {

        long count = 0;
        try {
            count = userService.count();
        } catch (Exception e) {

        }

        if(count > 0) return;

        try (FileInputStream fis = new FileInputStream(USER_DATA_URL);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.ISO_8859_1);
             BufferedReader br = new BufferedReader(isr)) {
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                String[] attr = line.split(";");
                User u = createUser(attr);
                Optional<User> existingUser = userService.findByUsername(u.getUsername());
                if (existingUser.isEmpty()) {
                    userService.save(u);
                } else {
                    copyUser(existingUser.get(), u);
                    userService.save(existingUser.get());
                }
            }

            if (seasonService.countAll() == 0) {
                createSeason1();
            }

            for (User u : userService.findAllByRole(Role.TEAM_LEADER)) {
                teamService.findByColor(u.getTeam().getColor()).ifPresent(t -> {
                    t.setTeamLeaderMyShareId(u.getMyShareID());
                    teamService.save(t);
                });
            }

            teamService.recalculateAllTeamPoints();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSeason1() {
        Season season1 = new Season();
        season1.setMyShareGoal(70);
        season1.setSamvirkGoal(12000);
        season1.setSeasonNumber(1);
        season1.setStartDateTime(LocalDateTime.parse("2023-09-15 00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        season1.setEndDateTime(LocalDateTime.parse("2023-10-06 23:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        seasonService.save(season1);
    }

    private void copyUser(User user, User newUser) {
        user.setPoints(newUser.getPoints());
        user.setFirstname(newUser.getFirstname());
        user.setLastname(newUser.getLastname());
        user.setCurrentMyShareCredit(newUser.getCurrentMyShareCredit());
        user.setBaseMyShareCredit(newUser.getBaseMyShareCredit());
        user.setBaseSamvirkCredit(newUser.getBaseSamvirkCredit());
        user.setBirthDate(newUser.getBirthDate());
        user.setRole(newUser.getRole());
        user.setGoal(newUser.getGoal());
        user.setTeam(newUser.getTeam());
        user.setMyShareID(newUser.getMyShareID());
        user.setPassword(newUser.getPassword());
    }

    private User createUser(String[] data) {
        User user = new User();
        user.setMyShareID(Integer.parseInt(data[0]));
        user.setFirstname(data[2]);
        user.setLastname(data[1]);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        user.setBirthDate(LocalDate.parse(data[6], formatter));
        user.setRole(data[7].isEmpty() ? Role.USER : Role.valueOf(data[7]));
        String username = createUserName(data[1] + data[2]);
        user.setUsername(username);
        user.setPassword(SecurityUtils.encryptSecret(username));
        if (!data[8].isEmpty()) {
            TeamColor tc = TeamColor.valueOf(data[8]);
            Team t = teamService.findByColor(tc).orElse(new Team());
            if (t.getColor() == null) {
                t.setColor(tc);
                teamService.save(t);
            }

            user.setTeam(t);

        }
        user.setBaseMyShareCredit(Integer.parseInt(data[9]));
        user.setCurrentMyShareCredit(Integer.parseInt(data[9]));
        user.setGoal(Integer.parseInt(data[10]));
        user.setPoints(0F);
        return user;
    }

    private String createUserName(String data) {
        for (String s : ENGLISH_CHARS.keySet()) {
            data = data.toLowerCase().replace(s, ENGLISH_CHARS.get(s));
        }
        return data;
    }
}
