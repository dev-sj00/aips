package com.portfolio.aips.project.interaction.report.app.admin.service.report_management.result;


public record FindAllReportUsersResult(
        Long userPk,
        String nickName,
        Long reportCounts
) {




    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long userPk;
        private String nickName;
        private Long reportCounts;

        public Builder userPk(Long userPk) {
            this.userPk = userPk;
            return this;
        }

        public Builder nickName(String nickName) {
            this.nickName = nickName;
            return this;
        }

        public Builder reportCounts(Long reportCounts) {
            this.reportCounts = reportCounts;
            return this;
        }

        public FindAllReportUsersResult build() {
            return new FindAllReportUsersResult(
                    userPk,
                    nickName,
                    reportCounts
            );
        }
    }
}
