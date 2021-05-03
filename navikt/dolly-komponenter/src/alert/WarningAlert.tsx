import React from 'react';
import { WarningFilled } from '@navikt/ds-icons';
import Alert from './Alert';

export type WarningAlertProps = {
  label?: string;
};

const WarningAlert = ({ label }: WarningAlertProps) => (
  <Alert color="#FF9100" label={label} Icon={WarningFilled} />
);

WarningAlert.displayName = 'WarningAlert';

export default WarningAlert;
