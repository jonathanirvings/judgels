package judgels.jerahmeel.chapter;

import static judgels.jerahmeel.JerahmeelCacheUtils.getShortDuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.ChapterCreateData;
import judgels.jerahmeel.api.chapter.ChapterInfo;
import judgels.jerahmeel.persistence.ChapterDao;
import judgels.jerahmeel.persistence.ChapterModel;
import judgels.jerahmeel.persistence.CourseChapterDao;
import judgels.jerahmeel.persistence.CourseChapterModel;
import judgels.jerahmeel.persistence.CourseDao;
import judgels.jerahmeel.persistence.CourseModel;

public class ChapterStore {
    private final ChapterDao chapterDao;
    private final CourseChapterDao courseChapterDao;
    private final CourseDao courseDao;

    private final LoadingCache<String, Chapter> chapterByJidCache;

    @Inject
    public ChapterStore(ChapterDao chapterDao, CourseChapterDao courseChapterDao, CourseDao courseDao) {
        this.chapterDao = chapterDao;
        this.courseChapterDao = courseChapterDao;
        this.courseDao = courseDao;

        this.chapterByJidCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(getShortDuration())
                .build(this::getChapterByJidUncached);
    }

    public Optional<Chapter> getChapterByJid(String chapterJid) {
        return Optional.ofNullable(chapterByJidCache.get(chapterJid));
    }

    private Chapter getChapterByJidUncached(String chapterJid) {
        return chapterDao.selectByJid(chapterJid).map(ChapterStore::fromModel).orElse(null);
    }

    public Map<String, ChapterInfo> getChapterInfosByJids(Set<String> chapterJids) {
        return chapterDao.selectByJids(chapterJids)
                .values()
                .stream()
                .collect(Collectors.toMap(
                        c -> c.jid,
                        c -> new ChapterInfo.Builder()
                                .name(c.name)
                                .build()));
    }

    public Map<String, String> getChapterNamesByJids(Set<String> chapterJids) {
        return chapterDao.selectByJids(chapterJids)
                .values()
                .stream()
                .collect(Collectors.toMap(
                        c -> c.jid,
                        c -> c.name));
    }

    public Map<String, List<String>> getChapterPathsByJids(Set<String> chapterJids) {
        List<CourseChapterModel> chapters = courseChapterDao.selectAllByChapterJids(chapterJids);
        Map<String, CourseChapterModel> chapterToCourseChapterMap = new HashMap<>();
        chapters.forEach(c -> chapterToCourseChapterMap.put(c.chapterJid, c));

        Set<String> courseJids = chapters.stream().map(c -> c.courseJid).collect(Collectors.toSet());
        Map<String, CourseModel> coursesMap = courseDao.selectByJids(courseJids);

        ImmutableMap.Builder<String, List<String>> paths = ImmutableMap.builder();
        for (String chapterJid : chapterJids) {
            CourseChapterModel m = chapterToCourseChapterMap.get(chapterJid);
            if (m != null) {
                CourseModel cm = coursesMap.get(m.courseJid);
                if (cm != null) {
                    paths.put(chapterJid, ImmutableList.of(cm.slug, m.alias));
                }
            }
        }
        return paths.build();
    }

    public Chapter createChapter(ChapterCreateData data) {
        ChapterModel model = new ChapterModel();
        model.name = data.getName();
        model.description = "";
        return fromModel(chapterDao.insert(model));
    }

    private static Chapter fromModel(ChapterModel model) {
        return new Chapter.Builder()
                .id(model.id)
                .jid(model.jid)
                .name(model.name)
                .description(model.description)
                .build();
    }
}
