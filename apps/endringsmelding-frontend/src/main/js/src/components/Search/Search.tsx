import React from 'react';

import styled from 'styled-components';
import { Input } from 'nav-frontend-skjema';
import { Knapp } from 'nav-frontend-knapper';

const Search = styled.div`
  display: flex;
  width: 100%;
`;

const InputStyle = styled.div`
  width: 70%;
`;

type Props = {
  labels: {
    label: string;
    button: string;
  };
};

export default ({ labels }: Props) => (
  <Search>
    <InputStyle>
      <Input label={labels.label} />
    </InputStyle>
    <Knapp style={{ height: 30, alignSelf: 'flex-end', marginLeft: 20 }}>{labels.button}</Knapp>
  </Search>
);
