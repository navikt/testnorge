import React from 'react';
import { ErrorFilled } from '@navikt/ds-icons';
import Alert from "./Alert";

type Props = {
  label?: string;
};

export default ({ label }: Props) => (
    <Alert color="#BA3A26" label={label} Icon={ErrorFilled}/>
);
