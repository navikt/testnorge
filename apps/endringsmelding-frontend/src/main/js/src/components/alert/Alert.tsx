import styled from 'styled-components';
import React from 'react';
import { SuccessFilled, WarningFilled, ErrorFilled } from '@navikt/ds-icons';
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
  Icon: typeof SuccessFilled | typeof ErrorFilled | typeof WarningFilled;
};

export default ({ label = '', color, Icon }: Props) => (
  <Alert role="alert">
    <Icon width={30} height={30} color={color} />
    <Label color={color}>{label}</Label>
  </Alert>
);
