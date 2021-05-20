import React from 'react';
import Alertstripe from './Alertstripe';

export type ErrorAlertstripeProps = {
  label: string;
};

const ErrorAlertstripe = ({ label }: ErrorAlertstripeProps) => (
  <Alertstripe label={label} type="feil" />
);

ErrorAlertstripe.displayName = 'ErrorAlertstripe';

export default ErrorAlertstripe;
