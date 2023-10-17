import React from 'react';
import { Page } from '@navikt/dolly-komponenter';
import { AccessList } from '@/components/AccessList';
import AccessForm from '@/components/AccessForm/AccessForm';
import styled from 'styled-components';

const StyledPage = styled(Page)`
  display: flex;
  justify-content: center;
`;

const HomePage = () => {
  return (
    <StyledPage>
      <AccessForm />
      <AccessList />
    </StyledPage>
  );
};

HomePage.displayName = 'HomePage';

export default HomePage;
