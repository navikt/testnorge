import React, { useEffect, useReducer, useState } from 'react';
import { Search } from '@/components/search';

import {
  ErrorAlertstripe,
  Form,
  Knapp,
  Line,
  SuccessAlertstripe,
  WarningAlertstripe,
} from '@navikt/dolly-komponenter';
import { Action, reducer, State } from './EndringsmeldingReducer';
import { BadRequestError } from '@navikt/dolly-lib/lib/error';
import { useIdentSearch } from '@/useIdentSearch';

type Props<T> = {
  children: React.ReactNode;
  labels: {
    search: string;
    submit: string;
  };
  getSuccessMessage: (value?: T) => string;
  getErrorMessage?: () => string;
  onSend: () => Promise<T>;
  valid: () => boolean;
  setIdent: (value: string) => void;
  setMiljoer: (value: string[]) => void;
};

export const initState: State = {
  ident: '',
  loading: false,
  show: true,
};

export default <T extends {}>({
  children,
  onSend,
  valid,
  labels,
  setMiljoer,
  setIdent,
  getSuccessMessage,
  getErrorMessage = () => 'Noe gikk galt.',
}: Props<T>) => {
  const [state, dispatch] = useReducer(reducer, initState);

  if (state.warningMessages) {
    console.log(state.warningMessages);
  }

  const [search, setSearch] = useState(null);
  const { error, identer, loading } = useIdentSearch(search);

  useEffect(() => {
    console.log('Identinfo fra miljøer: ', identer);
    setMiljoer(identer?.map((ident) => ident.miljoe));
    error
      ? dispatch({ type: Action.SET_HENT_MILJOER_ERROR_ACTION })
      : dispatch({ type: Action.SET_HENT_MILJOER_SUCCESS_ACTION });
  }, [identer, error]);

  const onSubmit = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault();
    if (valid()) {
      dispatch({ type: Action.SET_SUBMIT_START });
      onSend()
        .then((response) =>
          dispatch({ type: Action.SET_SUBMIT_SUCCESS, successMessage: getSuccessMessage(response) })
        )
        .catch((e) => {
          if (e instanceof BadRequestError) {
            return e.response
              .json()
              .then((body: string[]) =>
                dispatch({ type: Action.SET_SUBMIT_WARNING, warningMessages: body })
              );
          }

          return dispatch({ type: Action.SET_SUBMIT_ERROR, errorMessage: getErrorMessage() });
        });
    }
  };
  return (
    <Form>
      <Search
        onChange={(value) => {
          setIdent(value);
          dispatch({ type: Action.SET_IDENT_ACTION, value: value });
        }}
        setMiljoer={setMiljoer}
        dispatch={dispatch}
        labels={{
          label: labels.search,
          button: 'Søk etter person',
          onFound: 'Person funnet',
          onNotFound: 'Person ikke funnet',
          onError: 'Noe gikk galt',
          syntIdent: 'Endringsmelding støtter ikke synt-identer.',
        }}
      />
      {state.show && (
        <>
          {children}
          <Line reverse={true}>
            <Knapp
              variant={'primary'}
              onClick={onSubmit}
              disabled={state.loading}
              loading={state.loading}
            >
              {labels.submit}
            </Knapp>
          </Line>
        </>
      )}
      {!!state.successMessage && <SuccessAlertstripe label={state.successMessage} />}
      {!!state.errorMessage && <ErrorAlertstripe label={state.errorMessage} />}
      {!!state.warningMessages &&
        state.warningMessages.map((warning, index) => (
          <WarningAlertstripe key={index} label={warning} />
        ))}
    </Form>
  );
};
