import React, { useReducer } from 'react';

import { DatePickerFormItem, Line, SelectFormItem } from '@navikt/dolly-komponenter';
import reducer, { Action, State } from './DodsmeldingReducer';
import EndringsmeldingService from '@/service/EndringsmeldingService';
import { EndringsmeldingForm } from '../endringsmelding-form';

export const initState: State = {
  miljoOptions: [],
  handling: 'SETTE_DOEDSDATO',
  ident: '',
  doedsdato: '',
  miljoer: [],
  validate: false,
};

const notEmptyString = (value: string) => !!value && value !== '';
const notEmptyList = (value: unknown[]) => !!value && value.length > 0;

export default () => {
  const [state, dispatch] = useReducer(reducer, initState);

  const onValidate = () => {
    dispatch({ type: Action.SET_VALIDATE_ACTION, value: true });
    return (
      (state.handling === 'ANNULLERE_DOEDSDATO' || notEmptyString(state.doedsdato)) &&
      notEmptyList(state.miljoer)
    );
  };

  const onSend = () =>
    EndringsmeldingService.sendDodsmelding(
      {
        doedsdato: state.doedsdato,
        ident: state.ident.trim(),
        handling: state.handling,
      },
      state.miljoer
    );

  const getSuccessMessage = () => {
    const miljoer = state.miljoer.join(', ');
    if (state.handling === 'SETTE_DOEDSDATO') {
      return `Send dødsmelding for ident ${state.ident} ble sendt til miljø ${miljoer}.`;
    }
    if (state.handling === 'ENDRET_DOEDSDATO') {
      return `Endret dødsdato til ${state.doedsdato} for ident ${state.ident} ble sendt til miljø ${miljoer}.`;
    }
    return `Annullert dødsmelding for ident ${state.ident} ble sendt til miljø ${miljoer}.`;
  };
  return (
    <EndringsmeldingForm
      labels={{
        submit: 'Opprett dødsmelding',
        search: 'Ident',
      }}
      onSend={onSend}
      valid={onValidate}
      setIdent={(ident) => dispatch({ type: Action.SET_IDENT_ACTION, value: ident })}
      getSuccessMessage={getSuccessMessage}
      setMiljoer={(miljoer) =>
        dispatch({ type: Action.SET_MILJOER_OPTIONS_ACTION, value: miljoer })
      }
    >
      <Line>
        <SelectFormItem
          label="Handling"
          htmlId="handling-select"
          onChange={(value) =>
            // @ts-ignore
            dispatch({
              type: Action.SET_HANDLING_ACTION,
              value: value && value.length > 0 ? value[0] : 'SETTE_DOEDSDATO',
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
        {state.handling !== 'ANNULLERE_DOEDSDATO' && (
          <DatePickerFormItem
            id="doedsdato-field"
            label="Dødsdato*"
            onBlur={(value) => dispatch({ type: Action.SET_DOEDSDATO_ACTION, value: value })}
            error={state.validate && !notEmptyString(state.doedsdato) ? 'Påkrevd' : null}
          />
        )}
        <SelectFormItem
          onChange={(value) => dispatch({ type: Action.SET_MILJOER_ACTION, value: value })}
          htmlId="miljo-dodsdato-select"
          multi={true}
          label="Send til miljo*"
          error={state.validate && !notEmptyList(state.miljoer) ? 'Påkrevd' : null}
          options={state.miljoOptions.map((value: string) => ({
            value: value,
            label: value.toUpperCase(),
          }))}
        />
      </Line>
    </EndringsmeldingForm>
  );
};
