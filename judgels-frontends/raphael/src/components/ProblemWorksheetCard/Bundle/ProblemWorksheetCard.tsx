import * as React from 'react';

import { ProblemStatementCard } from './ProblemStatementCard/ProblemStatementCard';
import { ProblemWorksheet } from '../../../modules/api/sandalphon/problemBundle';
import { ItemSubmission } from '../../../modules/api/sandalphon/submissionBundle';

import './ProblemWorksheetCard.css';

export interface ProblemWorksheetCardProps {
  alias?: string;
  worksheet: ProblemWorksheet;
  latestSubmissions: { [id: string]: ItemSubmission };
  onAnswerItem: (itemJid: string, answer: string) => Promise<any>;
}

export class ProblemWorksheetCard extends React.PureComponent<ProblemWorksheetCardProps> {
  render() {
    return <div className="bundle-problem-worksheet">{this.renderStatement()}</div>;
  }

  private renderStatement = () => {
    const { worksheet, latestSubmissions, alias } = this.props;
    return (
      <ProblemStatementCard
        alias={alias}
        statement={worksheet.statement}
        onAnswerItem={this.props.onAnswerItem}
        items={worksheet.items}
        latestSubmissions={latestSubmissions}
        reasonNotAllowedToSubmit={this.props.worksheet.reasonNotAllowedToSubmit}
      />
    );
  };
}
