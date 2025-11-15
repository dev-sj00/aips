package com.portfolio.aips.project.utils.enums;

import lombok.Getter;

@Getter
public enum LLMValidBodyValue {
    CHATGPT("Can't load shared conversation"),
    GEMINI("<title>Gemini - direct access to Google AI</title>"),
    GROK("<meta name=\"description\" content=\"This conversation could not be found.\" />\n"),
    CLAUDE("<title>Claude | Claude</title>");

    final String value;

    LLMValidBodyValue(String value) {
        this.value = value;
    }
}
