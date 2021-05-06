import React from 'react';
import { WarningFilled } from '@navikt/ds-icons';
import Alert from "./Alert";

type Props = {
    label?: string;
};

export default ({ label }: Props) => (
    <Alert color="#FF9100" label={label} Icon={WarningFilled}/>
);

