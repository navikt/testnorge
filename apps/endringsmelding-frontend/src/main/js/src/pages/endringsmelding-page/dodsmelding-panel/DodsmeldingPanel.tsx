import React, { useReducer } from 'react';
import Panel from 'nav-frontend-paneler';
import Search from '@/components/search/Search';
import { Hovedknapp } from 'nav-frontend-knapper';
import styled from 'styled-components';

import { DatePickerFormItem, Form, Line, SelectFormItem } from '@/components/form';
import reducer, { Action, State } from './DodsmedlingReducer';

const Knapp = styled(Hovedknapp)``;

export const initState: State = {
  miljoOptions: [],
  handing: 'SETTE_DOEDSDATO',
  ident: '',
  dodsdato: '',
  miljoer: [],
  success: false,
  validate: false,
};

const notEmptyString = (value: string) => !!value && value !== '';
const notEmptyList = (value: unknown[]) => !!value && value.length > 0;

export default () => {
  const [state, dispatch] = useReducer(reducer, initState);

  console.log(state);

  const onSearch = (value: string) =>
    new Promise<string[]>((resolve, reject) =>
      setTimeout(() => resolve(['T4', 't5', 'T6', 'T7']), 500)
    )
      .then((response) => {
        dispatch({ type: Action.SET_MILJOER_OPTIONS_ACTION, value: response });
        dispatch({ type: Action.SET_SUCCESS_ACTION, value: true });
        dispatch({ type: Action.SET_VALIDATE_ACTION, value: false });
      })
      .catch((e) => {
        dispatch({ type: Action.SET_SUCCESS_ACTION, value: false });
        throw e;
      });

  const validate = () => {
    return notEmptyString(state.dodsdato) && notEmptyList(state.miljoer);
  };

  const onSubmit = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault();
    dispatch({ type: Action.SET_VALIDATE_ACTION, value: true });
    if (validate()) {
      // TODO do submit
    }
  };

  return (
    <Panel border style={{ borderTop: 0, borderTopLeftRadius: 0, borderTopRightRadius: 0 }}>
      <Form>
        <Search
          onSearch={onSearch}
          onBlur={(value) => dispatch({ type: Action.SET_IDENT_ACTION, value: value })}
          labels={{
            label: 'Ident',
            button: 'Søk etter person',
            onFound: 'Person funnet',
            onNotFound: 'Person ikke funnet',
          }}
        />
        {state.success && (
          <>
            <Line>
              <SelectFormItem
                label="Handling"
                htmlId="handling-select"
                onChange={(value) =>
                  dispatch({
                    type: Action.SET_HANDLING_ACTION,
                    value: value && value.length > 0 ? value[0] : '',
                  })
                }
                options={[
                  {
                    value: 'SETTE_DOEDSDATO',
                    label: 'Sette dødsdato',
                  },
                  {
                    value: 'ENDRET_DOEDSDATO',
                    label: 'Endret dødsdato',
                  },
                  {
                    value: 'ANNULLERE_DOEDSDATO',
                    label: 'Annullert dødsdato',
                  },
                ]}
              />
              <DatePickerFormItem
                id="dodsdato-field"
                label="Dødsdato"
                onBlur={(value) => dispatch({ type: Action.SET_DODSDATO_ACTION, value: value })}
                error={state.validate && !notEmptyString(state.dodsdato) ? 'På kreved' : null}
              />
              <SelectFormItem
                onChange={(value) => dispatch({ type: Action.SET_MILJOER_ACTION, value: value })}
                htmlId="miljo-dodsdato-select"
                multi={true}
                label="Send til miljo"
                error={state.validate && !notEmptyList(state.miljoer) ? 'På kreved' : null}
                options={state.miljoOptions.map((value: string) => ({
                  value: value,
                  label: value.toUpperCase(),
                }))}
              />
            </Line>
            <Line reverse={true}>
              <Knapp onClick={onSubmit} htmlType="submit">
                Opprett dødsmelding
              </Knapp>
            </Line>
          </>
        )}
      </Form>
    </Panel>
  );
};
