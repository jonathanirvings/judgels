package org.iatoki.judgels.sandalphon.problem.bundle.item;

import play.data.validation.Constraints;

public final class ItemStatementConfForm {

    @Constraints.Required
    public String meta;

    @Constraints.Required
    public String statement;

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }
}
