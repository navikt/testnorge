import React, { useState } from 'react';

import styled from 'styled-components';
import { Input as NavInput } from 'nav-frontend-skjema';
import { ErrorAlert, Knapp, SuccessAlert, WarningAlert } from '@navikt/dolly-komponenter';
import { NotFoundError } from '@navikt/dolly-lib';

const GruppeSearch = styled.div`
  display: flex;
  flex-direction: row;
`;

const Input = styled(NavInput)`
  width: 50%;
`;

type Props<T> = {
  onSearch: (value: string) => Promise<T>;
  labels: {
    label: string;
    button: string;
    onFound: string;
    onNotFound: string;
    onError: string;
  };
  onChange?: (value: string) => void;
};

const Alert = styled.div`
  width: 25%;
  display: flex;
  align-items: flex-end;
  padding-bottom: 5px;
  padding-left: 7px;
`;

export default <T extends unknown>({ labels, onSearch, onChange }: Props<T>) => {
  const [loading, setLoading] = useState(false);
  const [value, setValue] = useState('');
  const [success, setSuccess] = useState(undefined);
  const [error, setError] = useState(false);

  const _onSearch = (search: string) => {
    setLoading(true);
    setSuccess(undefined);
    setError(false);
    return onSearch(search)
      .then((response) => {
        setSuccess(true);
        return response;
      })
      .catch((e) => {
        setSuccess(false);
        if (!(e instanceof NotFoundError)) {
          setError(true);
        }
      })
      .finally(() => setLoading(false));
  };

  return (
    <GruppeSearch>
      <Input
        label={labels.label}
        defaultValue=""
        onChange={(e) => {
          if (onChange) {
            onChange(e.target.value);
          }
          setValue(e.target.value);
        }}
      />
      <Knapp onClick={() => _onSearch(value)} disabled={loading}>
        {labels.button}
      </Knapp>
      <Alert>
        {success && <SuccessAlert label={labels.onFound} />}
        {error && <ErrorAlert label={labels.onError} />}
        {!(success && error) && <WarningAlert label={labels.onNotFound} />}
      </Alert>
    </GruppeSearch>
  );
};
