package com.portfolio.aips.project.archive.service.UrlValidator.enums;

import lombok.Getter;

@Getter
public enum URLValidatorServiceImplName {
    CHATGPT("ChatGPTValidator"),
    GEMINI("PlayWrightValidator"),
    GROK("PlayWrightValidator"),
    CLAUDE("ClaudeValidator");

    private final String beanName;

    URLValidatorServiceImplName(String beanName) {
        this.beanName = beanName;
    }

    // URL 기반으로 enum 찾기 (예: value.contains(url))
    public static URLValidatorServiceImplName findByUrl(String url) {
        if (url.contains("chatgpt.com")) return CHATGPT;
        if (url.contains("claude.ai")) return CLAUDE;
        if (url.contains("gemini.google")) return GEMINI;
        if (url.contains("grok.com")) return GROK;
        return null; // 매칭 없으면 null
    }
}
