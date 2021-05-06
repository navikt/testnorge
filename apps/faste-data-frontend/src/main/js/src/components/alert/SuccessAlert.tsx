import React from 'react';
import { SuccessFilled } from '@navikt/ds-icons';
import Alert from "./Alert";

type Props = {
    label?: string;
};

export default ({ label }: Props) => (
    <Alert color="#06893A" label={label} Icon={SuccessFilled}/>
);
