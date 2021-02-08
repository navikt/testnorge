import styled from 'styled-components';
import React from 'react';
import { SuccessFilled } from '@navikt/ds-icons';

const SuccessAlert = styled.div`
  display: flex;
`;

const Label = styled.p`
  padding-left: 5px;
  padding-top: 5px;
  display: flex;
  margin: 0;
  color: #06893a;
  font-style: italic;
`;

type Props = {
  label?: string;
};

export default ({ label = '' }: Props) => (
  <SuccessAlert>
    <SuccessFilled width={30} height={30} color="#06893A" />
    <Label>{label}</Label>
  </SuccessAlert>
);
