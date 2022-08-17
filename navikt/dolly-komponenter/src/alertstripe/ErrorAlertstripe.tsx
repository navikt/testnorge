import React from 'react';
import Alertstripe from './Alertstripe';

export type ErrorAlertstripeProps = {
  label: string;
};

const ErrorAlertstripe = ({ label, ...props }: ErrorAlertstripeProps) => (
  <Alertstripe {...props} label={label} variant="error" />
);

ErrorAlertstripe.displayName = 'ErrorAlertstripe';

export default ErrorAlertstripe;
