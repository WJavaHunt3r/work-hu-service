package com.ktk.workhuservice.service;

import com.ktk.workhuservice.config.TelegramConfig;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import org.springframework.stereotype.Service;

@Service
public class TelegramService {

    private TelegramConfig telegramConfig;
    private TelegramBot bot;

    public TelegramService(TelegramConfig telegramConfig) {
        this.telegramConfig = telegramConfig;
        createBot();
    }

    private void createBot() {
        bot = new TelegramBot(telegramConfig.getToken());
        bot.setUpdatesListener(updates -> {
            updates.forEach((u) -> u.message().chat().id());
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            if (e.response() != null) {
                e.response().errorCode();
                e.response().description();
            } else {
                e.printStackTrace();
            }
        });

    }
}
