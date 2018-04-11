import { Card, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { FormattedDate } from 'react-intl';
import FormattedDuration from 'react-intl-formatted-duration';

import { ButtonLink } from '../../../../../../../../components/ButtonLink/ButtonLink';
import { Contest, ContestList } from '../../../../../../../../modules/api/uriel/contest';

import './ContestListTable.css';

export interface ContestListTableProps {
  contestList: ContestList;
}

export class ContestListTable extends React.Component<ContestListTableProps, {}> {
  render() {
    const { contestList } = this.props;
    const list = contestList.data.map(contest => (
      <div key={contest.jid} className="flex-row justify-content-space-between contest-list-item-container">
        <div>
          <h3>{contest.name}</h3>
          <p>
            <small>
              <FormattedDate
                value={contest.beginTime * 1000}
                year="numeric"
                month="short"
                day="numeric"
                hour="numeric"
                hour12={false}
                minute="numeric"
                timeZoneName="short"
              />
              {this.renderDurationSeparator(contest)}
              {this.renderDuration(contest)}
            </small>
          </p>
        </div>
        <div className="flex-column contest-list-item-info">
          <div className="flex-row justify-content-flex-end">
            <ButtonLink
              to={`/competition/contests/${contest.jid}`}
              intent={Intent.PRIMARY}
              className="contest-list-view-result"
            >
              View contest
            </ButtonLink>
          </div>
        </div>
      </div>
    ));

    return <Card className="contest-list-container">{list}</Card>;
  }

  private renderDurationSeparator = (contest: Contest) => {
    if (!contest.duration) {
      return null;
    }
    return ' | ';
  };

  private renderDuration = (contest: Contest) => {
    if (!contest.duration) {
      return null;
    }

    const Span = props => <span {...props} />;
    return (
      <FormattedDuration seconds={contest.duration} format="{days} {hours} {minutes} {seconds}" textComponent={Span} />
    );
  };
}
