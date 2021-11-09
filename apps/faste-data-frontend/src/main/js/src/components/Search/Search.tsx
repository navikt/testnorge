import React, { useState } from 'react';
import {
  ErrorAlertstripe,
  Knapp,
  Line,
  SuccessAlertstripe,
  WarningAlertstripe,
} from '@navikt/dolly-komponenter';
import { Input } from 'nav-frontend-skjema';
import styled from 'styled-components';
import { NotFoundError } from '@navikt/dolly-lib';

export type Props<T> = {
  labels: {
    input: string;
    button: string;
  };
  onSearch: (value: string) => Promise<T>;
};

const StyledInput = styled(Input)`
  flex-grow: 3;
`;
const StyledKnapp = styled(Knapp)`
  flex-grow: 1;
  width: auto !important;
`;

const StyledErrorAlertstripe = styled(ErrorAlertstripe)`
  flex-grow: 1;
`;

const StyledWarningAlertstripe = styled(WarningAlertstripe)`
  flex-grow: 1;
`;

const StyledSuccessAlertstripe = styled(SuccessAlertstripe)`
  flex-grow: 1;
`;

const Search = <T extends unknown>({ labels, onSearch }: Props<T>) => {
  const [value, setValue] = useState('');
  const [loading, setLoading] = useState(false);
  const [status, setStatus] = useState<'error' | 'not-found' | 'success' | null>(null);

  const renderStatus = () => {
    switch (status) {
      case 'error':
        return (
          <Line>
            <StyledErrorAlertstripe label="Noe gikk galt" />
          </Line>
        );
      case 'not-found':
        return (
          <Line>
            <StyledWarningAlertstripe label="Ikke funnet" />
          </Line>
        );
      case 'success':
        return (
          <Line>
            <StyledSuccessAlertstripe label="Success!" />
          </Line>
        );
    }
  };

  return (
    <>
      <Line>
        <StyledInput
          label={labels.input}
          value={value}
          onChange={(e) => {
            setValue(e.target.value);
          }}
        />
        <StyledKnapp
          disabled={loading}
          spinner={loading}
          onClick={(e) => {
            e.preventDefault();
            setStatus(null);

            if (value === '') {
              return;
            }

            setLoading(true);
            onSearch(value)
              .then(() => {
                setLoading(false);
                setStatus('success');
              })
              .catch((error) => {
                if (error && error.name === NotFoundError.name) {
                  setStatus('not-found');
                } else {
                  setStatus('error');
                }
                setLoading(false);
              });
          }}
        >
          {labels.button}
        </StyledKnapp>
      </Line>
      {renderStatus()}
    </>
  );
};

Search.dispalyName = 'Search';

export default Search;
