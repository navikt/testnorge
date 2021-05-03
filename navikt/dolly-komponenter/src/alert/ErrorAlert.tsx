import React from 'react';
import { ErrorFilled } from '@navikt/ds-icons';
import Alert from './Alert';

export type ErrorAlertProps = {
  label?: string;
};

const ErrorAlert = ({ label }: ErrorAlertProps) => (
  <Alert color="#BA3A26" label={label} Icon={ErrorFilled} />
);

ErrorAlert.displayName = 'ErrorAlert';

export default ErrorAlert;
