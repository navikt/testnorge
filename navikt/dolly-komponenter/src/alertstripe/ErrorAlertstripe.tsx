import React from 'react';
import Alertstripe from "./Alertstripe";

type Props ={
    label: string
}

export default ({ label }: Props) => <Alertstripe label={label} type="feil"/>