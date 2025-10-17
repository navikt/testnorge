import styled from 'styled-components';
import React from 'react';
import {CheckmarkCircleFillIcon, ExclamationmarkTriangleFillIcon, XMarkOctagonFillIcon} from "@navikt/aksel-icons";

const Alert = styled.div`
    display: flex;
`;

const Label = styled.p<{ color: string }>`
    padding-left: 5px;
    padding-top: 5px;
    display: flex;
    margin: 0;
    color: ${(props) => props.color};
    font-style: italic;
`;

type Props = {
    label?: string;
    color: string;
    Icon: typeof CheckmarkCircleFillIcon | typeof XMarkOctagonFillIcon | typeof ExclamationmarkTriangleFillIcon;
};

export default ({label = '', color, Icon}: Props) => (
    <Alert role="alert">
        <Icon width={30} height={30} color={color}/>
        <Label color={color}>{label}</Label>
    </Alert>
);
