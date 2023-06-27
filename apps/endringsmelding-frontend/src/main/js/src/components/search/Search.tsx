import React, { useEffect, useState } from 'react';

import styled from 'styled-components';
import { TextField as NavInput } from '@navikt/ds-react';
import {
  ErrorAlert,
  Knapp,
  SuccessAlert,
  WarningAlert,
  WarningAlertstripe,
} from '@navikt/dolly-komponenter';
import { useIdentSearch } from '@/useIdentSearch';
import { Action } from '@/pages/endringsmelding-page/form/endringsmelding-form/EndringsmeldingReducer';

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
  dispatch: any;
  setMiljoer: any;
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

const isSyntheticIdent = (value: string) => {
  return value.match('[0-9]{2}[4-9]{1}[0-9]{8}');
};

const StyledWarning = styled(WarningAlertstripe)`
  margin: 30px 0 0 15px;
  height: 50px;
  width: -webkit-fill-available;
`;

export default <T extends unknown>({ labels, onChange, setMiljoer, dispatch }: Props<T>) => {
  const [value, setValue] = useState('');
  const [search, setSearch] = useState(null);

  const { error, identer, loading } = useIdentSearch(search);

  useEffect(() => {
    console.log('Identinfo fra miljøer: ', identer);
    setMiljoer(identer?.map((ident) => ident.miljoe));
    error
      ? dispatch({ type: Action.SET_HENT_MILJOER_ERROR_ACTION })
      : dispatch({ type: Action.SET_HENT_MILJOER_SUCCESS_ACTION });
  }, [identer, error]);

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
        onClick={() => setSearch(value)}
        disabled={loading || isSyntheticIdent(value)}
        loading={loading}
      >
        {labels.button}
      </StyledKnapp>
      {isSyntheticIdent(value) && <StyledWarning label={labels.syntIdent} />}
      <Alert>
        {!identer ? null : identer.length === 0 ? (
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
