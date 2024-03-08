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
import _ from 'lodash';

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

export default <T extends unknown>({ labels, onChange, setMiljoer }: Props<T>) => {
  const [value, setValue] = useState('');
  const [search, setSearch] = useState(null);
  const [response, setResponse] = useState(null);
  const [loading, setLoading] = useState(null);
  const [error, setError] = useState(null);

  const renderAlert = () => {
    if (_.isEmpty(response?.miljoer)) {
      return null;
    } else if (error) {
      return <ErrorAlert label={labels.onError} />;
    } else if (response.miljoer.length === 0) {
      return <WarningAlert label={labels.onNotFound} />;
    } else {
      return <SuccessAlert label={labels.onFound} />;
    }
  };

  const hentMiljoeInfo = async (ident: string) => {
    setError(false);
    setLoading(true);
    return fetch(`/endringsmelding-service/api/v1/ident/miljoer`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ident: ident }),
    })
      .then(async (res) => {
        console.log('res: ', res); //TODO - SLETT MEG
        setLoading(false);
        const jsonResponse = await res.json();
        setResponse(jsonResponse);
      })
      .catch((reason) => {
        console.error(reason);
        setLoading(false);
        setError(true);
        if (reason?.response?.status === 401 || reason?.response?.status === 403) {
          console.error('Auth feilet');
        }
        if (reason.status === 404 || reason.response?.status === 404) {
          if (reason.response?.data?.error) {
            throw new Error(reason.response?.data?.error);
          }
        }
        throw new Error(`Henting av data fra endringsmelding-service feilet.`);
      });
  };

  useEffect(() => {
    if (search && search.length === 11) {
      hentMiljoeInfo(search);
    } else {
      setError('Ident må være 11 siffer.');
    }
  }, [search]);

  useEffect(() => {
    setMiljoer(response?.miljoer);
  }, [response]);

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
        onClick={(event: any) => {
          event.preventDefault();
          setSearch(value);
        }}
        disabled={loading || isSyntheticIdent(value)}
        loading={loading}
      >
        {labels.button}
      </StyledKnapp>
      {isSyntheticIdent(value) && <StyledWarning label={labels.syntIdent} />}
      <Alert>{renderAlert()}</Alert>
    </Search>
  );
};
