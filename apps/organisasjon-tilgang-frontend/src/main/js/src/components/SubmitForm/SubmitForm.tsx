import React, { useState } from 'react';
import styled from 'styled-components';
import { Hovedknapp } from 'nav-frontend-knapper';
import { ErrorAlertstripe, SuccessAlertstripe } from '@navikt/dolly-komponenter';

const StyledForm = styled.form`
  display: flex;
  flex-direction: column;
  margin-right: 25px;
  min-width: 300px;
`;

const StyledHovedknapp = styled(Hovedknapp)`
  margin: 20px 0;
`;

type Props<T> = {
  children: React.ReactNode;
  onSubmit: () => Promise<T>;
};

type Status = 'error' | 'success' | null;

const SubmitForm = <T extends unknown>({ children, onSubmit }: Props<T>) => {
  const [status, setStatus] = useState<Status>();
  const [loading, setLoading] = useState<boolean>(false);

  const _onSubmit = () => {
    setStatus(null);
    setLoading(true);
    return onSubmit()
      .then((response) => {
        setStatus('success');
        return response;
      })
      .catch(() => {
        setStatus('error');
      })
      .finally(() => setLoading(false));
  };

  return (
    <StyledForm>
      {children}
      <StyledHovedknapp
        disabled={loading}
        spinner={loading}
        onClick={(event) => {
          event.preventDefault();
          _onSubmit();
        }}
      >
        Opprett
      </StyledHovedknapp>
      {status === 'success' && <SuccessAlertstripe label="Opprettet!" />}
      {status === 'error' && <ErrorAlertstripe label="Noe gikk galt." />}
    </StyledForm>
  );
};

SubmitForm.displayName = 'SubmitForm';

export default SubmitForm;
