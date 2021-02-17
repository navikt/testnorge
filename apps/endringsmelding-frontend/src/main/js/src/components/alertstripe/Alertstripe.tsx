import styled from 'styled-components';
import React from 'react';
import Alertstripe from 'nav-frontend-alertstriper';

type Props = {
    label: string,
    type: "suksess" | "feil"
}

const Alert = styled(Alertstripe)`
    margin-top: 20px 
`;

export default ({label, type}: Props)=>(
    <Alert type={type}>{label}</Alert>
)