import React, { useState } from 'react';

import styled from 'styled-components';
import { TextField as NavInput } from '@navikt/ds-react';
import { Button as NavKnapp } from '@navikt/ds-react';
import { ErrorAlert, SuccessAlert, WarningAlert } from '@navikt/dolly-komponenter';
import { NotFoundError } from '@navikt/dolly-lib';

const Search = styled.div`
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

const Knapp = styled(NavKnapp)`
  width: 25%;
  height: 30px;
  align-self: flex-end;
  margin-left: 20px;
`;

export default <T extends unknown>({ labels, onSearch, onChange }: Props<T>) => {
  const [loading, setLoading] = useState(false);
  const [value, setValue] = useState('');
  const [success, setSuccess] = useState(undefined);
  const [error, setError] = useState(false);

  const _onSearch = (value: string) => {
    setLoading(true);
    setSuccess(undefined);
    setError(false);
    return onSearch(value)
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
    <Search>
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
      <Knapp onClick={() => _onSearch(value)} disabled={loading} spinner={loading}>
        {labels.button}
      </Knapp>
      <Alert>
        {success == undefined ? null : !success ? (
          error ? (
            <ErrorAlert label={labels.onError} />
          ) : (
            <WarningAlert label={labels.onNotFound} />
          )
        ) : (
          <SuccessAlert label={labels.onFound} />
        )}
      </Alert>
    </Search>
  );
};
