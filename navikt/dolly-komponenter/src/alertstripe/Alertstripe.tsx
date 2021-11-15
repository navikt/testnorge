import styled from 'styled-components';
import React from 'react';
import Alertstripe from 'nav-frontend-alertstriper';

type Props = {
    label: string;
    type: 'suksess' | 'feil' | 'advarsel';
};

const Alert = styled(Alertstripe)`
  margin-top: 20px;
`;

export default ({label, type, ...props}: Props) => <Alert {...props} type={type}>{label}</Alert>;
