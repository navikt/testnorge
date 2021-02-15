import React, { useReducer } from 'react';
import Search from '@/components/search/Search';
import { Hovedknapp } from 'nav-frontend-knapper';

import { Form, Line } from '@/components/form';
import EndringsmeldingService from '@/service/EndringsmeldingService';
import { State, reducer, Action } from './EndringsmeldingReducer';
import { ErrorAlertstripe, SuccessAlertstripe } from '@/components/alertstripe';

type Props = {
  children: React.ReactNode;
  labels: {
    search: string;
    submit: string;
  };
  getSuccessMessage: () => string;
  getErrorMessage?: () => string;
  onSend: () => Promise<unknown>;
  valid: () => boolean;
  setIdent: (value: string) => void;
  setMiljoer: (value: string[]) => void;
};

export const initState: State = {
  ident: '',
  loading: false,
  success: false,
};

export default ({
  children,
  onSend,
  valid,
  labels,
  setMiljoer,
  setIdent,
  getSuccessMessage,
  getErrorMessage = () => 'Noe gikk galt.',
}: Props) => {
  const [state, dispatch] = useReducer(reducer, initState);

  const onSearch = (value: string) =>
    EndringsmeldingService.fetchMiljoer(value)
      .then((response) => {
        setMiljoer(response);
        dispatch({ type: Action.SET_HENT_MILJOER_SUCCESS_ACTION });
      })
      .catch((e) => {
        dispatch({ type: Action.SET_HENT_MILJOER_ERROR_ACTION });
        throw e;
      });

  const onSubmit = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault();
    if (valid()) {
      dispatch({ type: Action.SET_SUBMIT_START });
      onSend()
        .then(() =>
          dispatch({ type: Action.SET_SUBMIT_SUCCESS, successMessage: getSuccessMessage() })
        )
        .catch(() => dispatch({ type: Action.SET_SUBMIT_ERROR, errorMessage: getErrorMessage() }));
    }
  };

  return (
    <Form>
      <Search
        onSearch={onSearch}
        onBlur={(value) => {
          setIdent(value);
          dispatch({ type: Action.SET_IDENT_ACTION, value: value });
        }}
        labels={{
          label: labels.search,
          button: 'Søk etter person',
          onFound: 'Person funnet',
          onNotFound: 'Person ikke funnet',
          onError: 'Noe gikk galt',
        }}
      />
      {state.success && (
        <>
          {children}
          <Line reverse={true}>
            <Hovedknapp onClick={onSubmit} htmlType="submit" disabled={state.loading}>
              {labels.submit}
            </Hovedknapp>
          </Line>
        </>
      )}
      {!!state.successMessage && <SuccessAlertstripe label={state.successMessage} />}
      {!!state.errorMessage && <ErrorAlertstripe label={state.errorMessage} />}
    </Form>
  );
};
