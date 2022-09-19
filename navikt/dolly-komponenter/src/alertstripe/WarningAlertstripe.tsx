import React from 'react';
import Alertstripe from './Alertstripe';

export type WarningAlertstripeProps = {
  label: string;
};

const WarningAlertstripe = ({ label, ...props }: WarningAlertstripeProps) => (
  <Alertstripe {...props} label={label} variant="warning" />
);

WarningAlertstripe.displayName = 'WarningAlertstripe';

export default WarningAlertstripe;
