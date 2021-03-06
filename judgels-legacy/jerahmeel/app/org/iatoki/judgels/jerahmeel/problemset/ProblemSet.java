package org.iatoki.judgels.jerahmeel.problemset;

import org.iatoki.judgels.jerahmeel.archive.Archive;

public final class ProblemSet {

    private final long id;
    private final String jid;
    private final Archive parentArchive;
    private final String slug;
    private final String name;
    private final String description;

    public ProblemSet(long id, String jid, String slug, Archive parentArchive, String name, String description) {
        this.id = id;
        this.jid = jid;
        this.slug = slug;
        this.parentArchive = parentArchive;
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getJid() {
        return jid;
    }

    public String getSlug() {
        return slug;
    }

    public Archive getParentArchive() {
        return parentArchive;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
