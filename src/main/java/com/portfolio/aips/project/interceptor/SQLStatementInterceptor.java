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

                break;
            }
        }

        return sql;
    }
}
