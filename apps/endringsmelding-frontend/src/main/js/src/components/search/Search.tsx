import React, { useState } from 'react';

import styled from 'styled-components';
import { TextField as NavInput } from '@navikt/ds-react';
import {
  ErrorAlert,
  Knapp,
  SuccessAlert,
  WarningAlert,
  WarningAlertstripe,
} from '@navikt/dolly-komponenter';
import { NotFoundError } from '@navikt/dolly-lib';

const Search = styled.div`
  display: flex;
  flex-direction: row;
`;

const Input = styled(NavInput)`
  width: 50%;
`;

const StyledKnapp = styled(Knapp)`
  min-width: 150px;
`;

type Props<T> = {
  onSearch: (value: string) => Promise<T>;
  labels: {
    label: string;
    button: string;
    onFound: string;
    onNotFound: string;
    onError: string;
    syntIdent: string;
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

const isSyntheticFNR = (value: string) => {
  return value.match('[0-9]{2}[4-9]{1}[0-9]{8}');
};

const StyledWarning = styled(WarningAlertstripe)`
  margin: 15px 0 0 10px;
`;

export default <T extends unknown>({ labels, onSearch, onChange }: Props<T>) => {
  const [loading, setLoading] = useState(false);
  const [value, setValue] = useState('');
  const [success, setSuccess] = useState(undefined);
  const [error, setError] = useState(false);

  const _onSearch = (value: string) => {
    if (value) setLoading(true);
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
      <StyledKnapp
        onClick={() => _onSearch(value)}
        disabled={loading || isSyntheticFNR(value)}
        loading={loading}
      >
        {labels.button}
      </StyledKnapp>
      {isSyntheticFNR(value) && <StyledWarning label={labels.syntIdent} />}
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
