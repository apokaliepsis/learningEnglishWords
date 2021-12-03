package com.telegrambot.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParsedCommand {
    public Command getCommand() {
        return command;
    }

    Command command = Command.NONE;

    public String getText() {
        return text;
    }

    String text="";

    public void setCommand(Command command) {
        this.command = command;
    }

    public void setText(String text) {
        this.text = text;
    }
}

