import { Intent, Tag } from '@blueprintjs/core';
import * as React from 'react';

import { ContentCardLink } from '../../../../../../components/ContentCardLink/ContentCardLink';
import { Contest } from '../../../../../../modules/api/uriel/contest';
import { ContestProblem, ContestProblemStatus } from '../../../../../../modules/api/uriel/contestProblem';

import './ContestProblemCard.css';

export interface ContestProblemCardProps {
  contest: Contest;
  problem: ContestProblem;
  problemName: string;
  totalSubmissions: number;
}

export class ContestProblemCard extends React.PureComponent<ContestProblemCardProps> {
  render() {
    const { contest, problem, problemName } = this.props;

    const problemPoints = problem.points === null || problem.points === undefined ? '' : ` [${problem.points} points]`;

    return (
      <ContentCardLink to={`/contests/${contest.slug}/problems/${problem.alias}`}>
        <div className="contest-problem-card__name">
          <span data-key="name">
            {problem.alias}. {problemName}
            {problemPoints}
          </span>
        </div>
        <div data-key="status" className="contest-problem-card__status secondary-info">
          {this.renderStatus()}
        </div>
        <div className="clearfix" />
      </ContentCardLink>
    );
  }

  private renderStatus = () => {
    const { problem, totalSubmissions } = this.props;
    if (problem.status === ContestProblemStatus.Closed) {
      return <Tag intent={Intent.DANGER}>CLOSED</Tag>;
    }
    if (!!problem.submissionsLimit) {
      return <span>{problem.submissionsLimit - totalSubmissions} submissions left</span>;
    }
    return <div />;
  };
}
