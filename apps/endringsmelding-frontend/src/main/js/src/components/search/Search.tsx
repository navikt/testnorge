import React, { FocusEventHandler, useState } from 'react';

import styled from 'styled-components';
import { Input as NavInput } from 'nav-frontend-skjema';
import { Knapp as NavKnapp } from 'nav-frontend-knapper';
import { SuccessAlert, WarningAlert } from '@/components/alert';

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
  };
  onBlur?: (value: string) => void;
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

export default <T extends unknown>({ labels, onSearch, onBlur }: Props<T>) => {
  const [loading, setLoading] = useState(false);
  const [value, setValue] = useState('');
  const [success, setSuccess] = useState(undefined);

  const _onSearch = (value: string) => {
    setLoading(true);
    setSuccess(undefined);
    return onSearch(value)
      .then((response) => {
        setSuccess(true);
        return response;
      })
      .catch(() => {
        setSuccess(false);
      })
      .finally(() => setLoading(false));
  };

  return (
    <Search>
      <Input
        label={labels.label}
        defaultValue=""
        onBlur={(e) => {
          if (onBlur) {
            onBlur(e.target.value);
          }
          setValue(e.target.value);
        }}
      />
      <Knapp onClick={() => _onSearch(value)} disabled={loading}>
        {labels.button}
      </Knapp>
      <Alert>
        {success == undefined ? null : !success ? (
          <WarningAlert label={labels.onNotFound} />
        ) : (
          <SuccessAlert label={labels.onFound} />
        )}
      </Alert>
    </Search>
  );
};
