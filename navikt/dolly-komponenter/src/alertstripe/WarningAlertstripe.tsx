import React from 'react';
import Alertstripe from './Alertstripe';

export type WarningAlertstripeProps = {
    label: string;
};

const WarningAlertstripe = ({label, ...props}: WarningAlertstripeProps) => (
    <Alertstripe {...props} label={label} type="advarsel"/>
);

WarningAlertstripe.displayName = 'WarningAlertstripe';

export default WarningAlertstripe;
