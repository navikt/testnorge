import React from 'react';
import {CheckmarkCircleFillIcon} from '@navikt/aksel-icons';
import Alert from './Alert';

export type SuccessAlertProps = {
    label?: string;
};

const SuccessAlert = ({label}: SuccessAlertProps) => (
    <Alert color="#06893A" label={label} Icon={CheckmarkCircleFillIcon}/>
);

SuccessAlert.displayName = 'SuccessAlert';

export default SuccessAlert;
