import React from 'react';
import { SuccessFilled } from '@navikt/ds-icons';
import Alert from './Alert';

export type SuccessAlertProps = {
  label?: string;
};

const SuccessAlert = ({ label }: SuccessAlertProps) => (
  <Alert color="#06893A" label={label} Icon={SuccessFilled} />
);

SuccessAlert.displayName = 'SuccessAlert';

export default SuccessAlert;
