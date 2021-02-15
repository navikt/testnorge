import React from 'react';

import styled from 'styled-components';
import { Header } from '@/components/header';
import { ProfilLoader } from '@/components/profil';
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
        <ProfilLoader />
      </Header>
      <EndringsmeldingPage />
    </div>
  );
}

export default App;
