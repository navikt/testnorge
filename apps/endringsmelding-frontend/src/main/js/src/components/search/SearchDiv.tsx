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

const SearchDiv = styled.div`
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
  setMiljoer: any;
  setShow: any;
  labels: {
    label: string;
    button: string;
    delete: string;
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

export const Search = <T extends unknown>({ labels, onChange, setShow, setMiljoer }: Props<T>) => {
  const [value, setValue] = useState('');
  const [query, setQuery] = useState(null);
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
        setLoading(false);
        setError(false);
        const jsonResponse = await res.json();
        setResponse(jsonResponse?.[0]);
        setShow(true);
      })
      .catch((reason) => {
        console.error(reason);
        setShow(false);
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
    if (query && query.length === 11) {
      hentMiljoeInfo(query);
    } else {
      setError('Ident må være 11 siffer.');
    }
  }, [query]);

  useEffect(() => {
    setMiljoer(response?.miljoer);
  }, [response]);

  return (
    <SearchDiv>
      <Input
        label={labels.label}
        defaultValue=""
        onChange={(e) => {
          setShow(false);
          setResponse(null);
          setMiljoer([]);
          if (onChange) {
            onChange(e.target.value);
          }
          setValue(e.target.value);
        }}
      />
      <StyledKnapp
        onClick={(event: any) => {
          event.preventDefault();
          setQuery(value);
        }}
        disabled={loading || isSyntheticIdent(value)}
        loading={loading}
      >
        {labels.button}
      </StyledKnapp>
      {isSyntheticIdent(value) && <StyledWarning label={labels.syntIdent} />}
      <Alert>{renderAlert()}</Alert>
    </SearchDiv>
  );
};
