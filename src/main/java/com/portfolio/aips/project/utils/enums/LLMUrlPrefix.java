package com.portfolio.aips.project.utils.enums;

import lombok.Getter;

@Getter
public enum LLMUrlPrefix {
    CHATGPT("chatgpt.com/share/"),
    GEMINI("gemini.google.com/share/"),
    GROK("grok.com/share/"),
    CLAUDE("claude.ai/share/");


    private final String url;

    LLMUrlPrefix(String url) {
        this.url = url;
    }


    public static String getAllPrefix() {
        StringBuilder sb = new StringBuilder();
        for (LLMUrlPrefix prefix : LLMUrlPrefix.values()) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(prefix.name());
        }
        return sb.toString();
    }

    public static String findKeyByUrl(String url) {
        for (LLMUrlPrefix prefix : values()) {
            if (url.contains(prefix.getUrl())) {
                return prefix.name();  // KEY 반환
            }
        }
        return null; // 매칭되는 값 없으면 null
    }

}
