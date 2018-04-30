package judgels.uriel.contest.contestant;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestContestantModel;

public class ContestContestantStore {
    private final ContestContestantDao contestContestantDao;

    @Inject
    public ContestContestantStore(ContestContestantDao contestContestantDao) {
        this.contestContestantDao = contestContestantDao;
    }

    // temporary
    public void upsertContestant(String contestJid, String userJid) {
        ContestContestantModel model = new ContestContestantModel();
        model.contestJid = contestJid;
        model.userJid = userJid;
        contestContestantDao.insert(model);
    }

    public Page<String> getContestantJids(String contestJid, SelectionOptions options) {
        Page<ContestContestantModel> modelsPage = contestContestantDao.selectAllByContestJid(contestJid, options);
        return modelsPage.mapData(data -> Lists.transform(data, ContestContestantStore::fromModel));
    }

    public List<String> addContestants(String contestJid, List<String> contestantJids) {
        List<String> userJidsToBeInserted = filterOutExistingUserJids(contestJid, contestantJids);
        List<ContestContestantModel> contestantsToBeInserted = userJidsToBeInserted.stream()
                .map(userJid -> {
                    ContestContestantModel contestantModel = new ContestContestantModel();
                    contestantModel.contestJid = contestJid;
                    contestantModel.userJid = userJid;
                    return contestantModel;
                })
                .collect(Collectors.toList());

        List<ContestContestantModel> insertedContestantModels = contestContestantDao.insertAll(
                contestantsToBeInserted);

        return insertedContestantModels.stream()
                .map(ContestContestantStore::fromModel)
                .collect(Collectors.toList());
    }

    private List<String> filterOutExistingUserJids(String contestJid, List<String> userJids) {
        List<String> existingJids = contestContestantDao.selectAllByContestJidAndUserJids(contestJid, userJids).stream()
                .map(contestant -> contestant.userJid)
                .collect(Collectors.toList());

        Set<String> userJidsToBeInserted = new HashSet<>(userJids);
        userJidsToBeInserted.removeAll(existingJids);

        return ImmutableList.copyOf(userJidsToBeInserted);
    }

    private static String fromModel(ContestContestantModel model) {
        return model.userJid;
    }

}
