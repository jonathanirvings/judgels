package org.iatoki.judgels.jerahmeel.chapter.problem;

import play.data.validation.Constraints;

public class ChapterProblemEditForm {

    @Constraints.Required
    public String alias;

    @Constraints.Required
    public String status;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
