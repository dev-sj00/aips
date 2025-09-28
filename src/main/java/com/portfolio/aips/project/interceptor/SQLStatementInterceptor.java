package com.portfolio.aips.project.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.EmptyInterceptor;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SQLStatementInterceptor implements StatementInspector {
    @Override
    public String inspect(String sql) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        for (StackTraceElement element : stack) {
            // interceptor 자신 제외
            if (element.getClassName().startsWith("com.portfolio") &&
                    !element.getClassName().equals(this.getClass().getName())) {


                log.info("SQL called from: " + element);

                String formattedSql = sql
                        .replaceAll("(?i)SELECT ", "\nSELECT ")
                        .replaceAll("(?i)INSERT ", "\nINSERT ")
                        .replaceAll("(?i)UPDATE ", "\nUPDATE ")
                        .replaceAll("(?i)DELETE ", "\nDELETE ")
                        .replaceAll("(?i)FROM ", "\nFROM ")
                        .replaceAll("(?i)WHERE ", "\nWHERE ")
                        .replaceAll("(?i)JOIN ", "\nJOIN ")
                        .replaceAll("(?i)AND ", "\nAND ")
                        .replaceAll("(?i)ORDER BY ", "\nORDER BY ")
                        .replaceAll("(?i)GROUP BY ", "\nGROUP BY ");
                log.info("SQL: {}", formattedSql);
                break;
            }
        }

        return sql;
    }
}
