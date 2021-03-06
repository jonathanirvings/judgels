package org.iatoki.judgels.sandalphon.problem.base;

import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.statement.html.statementLanguageSelectionLayout;
import org.iatoki.judgels.sandalphon.problem.bundle.BundleProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemControllerUtils;
import play.i18n.Messages;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Set;

public abstract class AbstractProblemController extends AbstractBaseProblemController {
    protected Result renderTemplate(HtmlTemplate template, ProblemService problemService, Problem problem) {
        appendTabs(template, problemService, problem);
        appendVersionLocalChangesWarning(template, problemService, problem);
        appendTitle(template, problemService, problem);

        return super.renderTemplate(template);
    }

    protected void appendTitle(HtmlTemplate template, ProblemService problemService, Problem problem) {
        template.setMainTitle("#" + problem.getId() + ": " + problem.getSlug());

        if (ProblemControllerUtils.isAllowedToUpdateProblem(problemService, problem)) {
            template.addMainButton(Messages.get("problem.update"), routes.ProblemController.editProblem(problem.getId()));
        } else {
            template.addMainButton(Messages.get("problem.view"), routes.ProblemController.viewProblem(problem.getId()));
        }
    }

    protected void appendTabs(HtmlTemplate template, ProblemService problemService, Problem problem) {
        if (problem.getType().equals(ProblemType.PROGRAMMING)) {
            ProgrammingProblemControllerUtils.appendTabs(template, problemService, problem);
        } else if (problem.getType().equals(ProblemType.BUNDLE)) {
            BundleProblemControllerUtils.appendTabs(template, problemService, problem);
        }
    }

    protected Result renderStatementTemplate(HtmlTemplate template, ProblemService problemService, Problem problem) {
        template.markBreadcrumbLocation(Messages.get("problem.statement"), org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToStatement(problem.getId()));

        template.addSecondaryTab(Messages.get("commons.view"), org.iatoki.judgels.sandalphon.problem.base.statement.routes.ProblemStatementController.viewStatement(problem.getId()));
        if (ProblemControllerUtils.isAllowedToUpdateStatement(problemService, problem)) {
            template.addSecondaryTab(Messages.get("commons.update"), org.iatoki.judgels.sandalphon.problem.base.statement.routes.ProblemStatementController.editStatement(problem.getId()));
        }

        template.addSecondaryTab(Messages.get("problem.statement.media"), org.iatoki.judgels.sandalphon.problem.base.statement.routes.ProblemStatementController.listStatementMediaFiles(problem.getId()));

        if (ProblemControllerUtils.isAllowedToManageStatementLanguages(problemService, problem)) {
            template.addSecondaryTab(Messages.get("problem.statement.language"), org.iatoki.judgels.sandalphon.problem.base.statement.routes.ProblemStatementController.listStatementLanguages(problem.getId()));
        }

        return this.renderTemplate(template, problemService, problem);
    }

    protected void appendStatementLanguageSelection(HtmlTemplate template, String currentLanguage, Set<String> allowedLanguages, Call target) {
        template.transformContent(c -> statementLanguageSelectionLayout.render(target.absoluteURL(Controller.request(), Controller.request().secure()), allowedLanguages, currentLanguage, c));
    }

    protected Result renderPartnerTemplate(HtmlTemplate template, ProblemService problemService, Problem problem) {
        template.markBreadcrumbLocation(Messages.get("problem.partner"), org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToPartners(problem.getId()));

        return this.renderTemplate(template, problemService, problem);
    }
}
