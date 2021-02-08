import React from 'react';

import styled from 'styled-components';
import { Header } from '@/components/Header';
import { Profil } from '@/components/Profil';
import { Page } from '@/components/Page';
import { EndringsmeldingPage } from '@/pages';

type Button = {
  primary: boolean;
};

const Button = styled.button`
  /* Adapt the colors based on primary prop */
  background: ${(props: Button) => (props.primary ? 'palevioletred' : 'white')};
  color: ${(props: Button) => (props.primary ? 'white' : 'palevioletred')};

  font-size: 1em;
  margin: 1em;
  padding: 0.25em 1em;
  border: 2px solid palevioletred;
  border-radius: 3px;
`;

function App() {
  return (
    <div>
      <Header>
        <Profil />
      </Header>
      <EndringsmeldingPage />
    </div>
  );
}

export default App;
