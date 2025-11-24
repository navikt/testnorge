import React from 'react';
import { ExclamationmarkTriangleFillIcon } from '@navikt/aksel-icons';
import Alert from './Alert';

export type WarningAlertProps = {
  label?: string;
};

const WarningAlert = ({ label }: WarningAlertProps) => (
  <Alert color="#FF9100" label={label} Icon={ExclamationmarkTriangleFillIcon} />
);

WarningAlert.displayName = 'WarningAlert';

export default WarningAlert;
