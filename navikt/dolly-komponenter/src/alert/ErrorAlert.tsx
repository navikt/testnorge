import React from 'react';
import {XMarkOctagonFillIcon} from '@navikt/aksel-icons';
import Alert from './Alert';

export type ErrorAlertProps = {
    label?: string;
};

const ErrorAlert = ({label}: ErrorAlertProps) => (
    <Alert color="#BA3A26" label={label} Icon={XMarkOctagonFillIcon}/>
);

ErrorAlert.displayName = 'ErrorAlert';

export default ErrorAlert;
