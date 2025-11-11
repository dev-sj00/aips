package com.portfolio.aips.project.config.annotation.archived;

import lombok.Getter;

@Getter
public enum ArchiveUrlPrefix {
    CHATGPT("chatgpt.com/share/"),
    GEMINI("gemini.google.com/share/"),
    GROK("grok.com/share/");


    private final String url;

    ArchiveUrlPrefix(String url) {
        this.url = url;
    }


    public static String getAllPrefix() {
        StringBuilder sb = new StringBuilder();
        for (ArchiveUrlPrefix prefix : ArchiveUrlPrefix.values()) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(prefix.name());
        }
        return sb.toString();
    }

}
