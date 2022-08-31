import styled from 'styled-components';
import React from 'react';
import { Alert as NavAlert } from '@navikt/ds-react';

type Props = {
    label: string;
    variant: 'error' | 'warning' | 'info' | 'success';
};

const Alert = styled(NavAlert)`
    margin-top: 20px;
`;

export default ({ label, variant, ...props }: Props) => <Alert {...props} variant={variant}>{label}</Alert>;
