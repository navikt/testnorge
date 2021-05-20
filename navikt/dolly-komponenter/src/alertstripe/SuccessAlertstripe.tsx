import React from 'react';
import Alertstripe from './Alertstripe';

export type SuccessAlertstripeProps = {
  label: string;
};

const SuccessAlertstripe = ({ label }: SuccessAlertstripeProps) => (
  <Alertstripe label={label} type="suksess" />
);

SuccessAlertstripe.displayName = 'SuccessAlertstripe';

export default SuccessAlertstripe;
