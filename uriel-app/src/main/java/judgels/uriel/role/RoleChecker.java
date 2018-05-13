package judgels.uriel.role;

import static judgels.uriel.api.contest.supervisor.SupervisorPermissionType.PROBLEM;
import static judgels.uriel.api.contest.supervisor.SupervisorPermissionType.SCOREBOARD;
import static judgels.uriel.api.contest.supervisor.SupervisorPermissionType.SUBMISSION;

import java.time.Clock;
import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.supervisor.ContestSupervisor;
import judgels.uriel.api.contest.supervisor.SupervisorPermissionType;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestRoleDao;

public class RoleChecker {
    private final Clock clock;
    private final AdminRoleDao adminRoleDao;
    private final ContestRoleDao contestRoleDao;
    private final ContestSupervisorStore supervisorStore;

    @Inject
    public RoleChecker(
            Clock clock,
            AdminRoleDao adminRoleDao,
            ContestRoleDao contestRoleDao,
            ContestSupervisorStore supervisorStore) {

        this.clock = clock;
        this.adminRoleDao = adminRoleDao;
        this.contestRoleDao = contestRoleDao;
        this.supervisorStore = supervisorStore;
    }

    public boolean canCreateContest(String userJid) {
        return adminRoleDao.isAdmin(userJid);
    }

    public boolean canViewContest(String userJid, Contest contest) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isViewerOrAbove(userJid, contest.getJid());
    }

    public boolean canViewAnnouncements(String userJid, Contest contest) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isViewerOrAbove(userJid, contest.getJid());
    }

    public boolean canViewProblems(String userJid, Contest contest) {
        if (canSuperviseProblems(userJid, contest)) {
            return true;
        }
        return contestRoleDao.isViewerOrAbove(userJid, contest.getJid()) && contest.hasStarted(clock);
    }

    public boolean canSuperviseProblems(String userJid, Contest contest) {
        return isSupervisorWithPermissionOrAbove(userJid, contest, PROBLEM);
    }

    public boolean canViewDefaultScoreboard(String userJid, Contest contest) {
        if (canSuperviseScoreboard(userJid, contest)) {
            return true;
        }
        return contestRoleDao.isViewerOrAbove(userJid, contest.getJid()) && contest.hasStarted(clock);
    }

    public boolean canSuperviseScoreboard(String userJid, Contest contest) {
        return isSupervisorWithPermissionOrAbove(userJid, contest, SCOREBOARD);
    }

    public boolean canViewSubmission(String userJid, Contest contest, String submissionUserJid) {
        return userJid.equals(submissionUserJid) || isSupervisorWithPermissionOrAbove(userJid, contest, SUBMISSION);
    }

    public boolean canViewOwnSubmissions(String userJid, Contest contest) {
        if (canSuperviseSubmissions(userJid, contest)) {
            return true;
        }
        return contestRoleDao.isContestantOrAbove(userJid, contest.getJid()) && contest.hasStarted(clock);
    }

    public boolean canSuperviseSubmissions(String userJid, Contest contest) {
        return isSupervisorWithPermissionOrAbove(userJid, contest, SUBMISSION);
    }

    public boolean canAddContestants(String userJid, Contest contest) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isManager(userJid, contest.getJid());
    }

    private boolean isSupervisorWithPermissionOrAbove(String userJid, Contest contest, SupervisorPermissionType type) {
        if (adminRoleDao.isAdmin(userJid) || contestRoleDao.isManager(userJid, contest.getJid())) {
            return true;
        }
        Optional<ContestSupervisor> supervisor = supervisorStore.findSupervisor(contest.getJid(), userJid);
        return supervisor.isPresent() && supervisor.get().getPermission().allows(type);
    }
}
