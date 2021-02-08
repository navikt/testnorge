import styled from 'styled-components';
import React from 'react';
import { WarningFilled } from '@navikt/ds-icons';

const SuccessAlert = styled.div`
  display: flex;
`;

const Label = styled.p`
  padding-left: 5px;
  padding-top: 5px;
  display: flex;
  margin: 0;
  color: #ff9100;
  font-style: italic;
`;

type Props = {
  label?: string;
};

export default ({ label = '' }: Props) => (
  <SuccessAlert>
    <WarningFilled width={30} height={30} color="#FF9100" />
    <Label>{label}</Label>
  </SuccessAlert>
);
