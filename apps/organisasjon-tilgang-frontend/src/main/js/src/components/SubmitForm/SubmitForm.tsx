import React, { useState } from 'react';
import styled from 'styled-components';
import { ErrorAlertstripe, Knapp, SuccessAlertstripe } from '@navikt/dolly-komponenter';

const StyledForm = styled.form`
  display: flex;
  flex-direction: column;
  margin-right: 25px;
  min-width: 300px;
`;

const StyledHovedknapp = styled(Knapp)`
  margin: 20px 0;
  && {
    align-self: flex-start;
    margin-left: 0;
  }
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
        loading={loading}
        onClick={(event: { preventDefault: () => void }) => {
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
