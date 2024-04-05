import React, { useReducer } from 'react';

import { DatePickerFormItem, InputFormItem, Line, SelectFormItem } from '@navikt/dolly-komponenter';
import reducer, { Action, State } from './FodselsmeldingReducer';
import { sendFodselsmelding } from '@/service/EndringsmeldingService';
import { EndringsmeldingForm } from '../endringsmelding-form';
import { format } from 'date-fns';
import { Button } from '@navikt/ds-react';

export const initState: State = {
  miljoOptions: [],
  kjoenType: 'GUTT',
  identType: 'FNR',
  farsIdent: '',
  morsIdent: '',
  foedselsdato: format(new Date(), 'y-MM-dd'),
  address: 'LAG_NY_ADRESSE',
  miljoer: [],
  validate: false,
};

const notEmptyString = (value: string) => !!value && value !== '';
const notEmptyList = (value: unknown[]) => !!value && value.length > 0;

export default () => {
  const [state, dispatch] = useReducer(reducer, initState);

  const onValidate = () => {
    dispatch({ type: Action.SET_VALIDATE_ACTION, value: true });
    return notEmptyString(state.foedselsdato) && notEmptyList(state.miljoer);
  };

  const onSend = () =>
    sendFodselsmelding(
      {
        adresseFra: state.address,
        identFar: state.farsIdent !== '' ? state.farsIdent.trim() : null,
        identMor: state.morsIdent.trim(),
        identtype: state.identType,
        foedselsdato: state.foedselsdato,
        kjoenn: state.kjoenType,
      },
      state.miljoer,
    ).then((response) => {
      console.log('Response: ', response); //TODO - SLETT MEG FØR MERGE
      const error = response?.error && new Map([[state.miljoer?.[0], response.error]]);
      dispatch({ type: Action.SET_BARNS_IDENT, value: response?.ident?.trim() });
      dispatch({ type: Action.SET_ERROR_LIST, value: response?.miljoStatus || error });
      return Promise.resolve(response);
    });

  const getSuccessMessage = (value: string | null) =>
    `Gratulerer, person med ident ${value} ble født i miljø ${state.miljoer.join(', ')}.`;

  return (
    <EndringsmeldingForm
      labels={{
        submit: 'Opprett fødselsmelding',
        search: 'Mors ident',
      }}
      onSend={onSend}
      valid={onValidate}
      setIdent={(ident) => dispatch({ type: Action.SET_MORS_IDENT_ACTION, value: ident.trim() })}
      getSuccessMessage={getSuccessMessage}
      setMiljoer={(miljoer) => {
        dispatch({ type: Action.SET_MILJOER_OPTIONS_ACTION, value: miljoer });

        if (miljoer?.length > 0) {
          dispatch({ type: Action.SET_MILJOER_ACTION, value: [miljoer[0]] });
        }
      }}
    >
      <Line>
        <InputFormItem
          label="Fars ident"
          defaultValue=""
          onBlur={(e) => dispatch({ type: Action.SET_FARS_IDENT_ACTION, value: e.target.value })}
        />
        <SelectFormItem
          label="Barnets identtype"
          htmlId="barnets-identtype-select"
          onChange={(value) =>
            dispatch({
              type: Action.SET_IDENT_TYPE_ACTION,
              value: value && value.length > 0 ? value[0] : '',
            })
          }
          options={[
            {
              value: 'FNR',
              label: 'FNR',
            },
            {
              value: 'DNR',
              label: 'DNR',
            },
            {
              value: 'BOST',
              label: 'BOST',
            },
          ]}
        />
      </Line>
      <Line>
        <SelectFormItem
          label="Barnets kjønn"
          htmlId="barnets-kjoen-select"
          onChange={(value) =>
            // @ts-ignore
            dispatch({
              type: Action.SET_KJOEN_TYPE_ACTION,
              value: value && value.length > 0 ? value[0] : 'GUTT',
            })
          }
          options={[
            {
              value: 'GUTT',
              label: 'Gutt',
            },
            {
              value: 'JENTE',
              label: 'Jente',
            },
            {
              value: 'UKJENT',
              label: 'Ukjent',
            },
          ]}
        />
        <SelectFormItem
          onChange={(value) => dispatch({ type: Action.SET_MILJOER_ACTION, value: value })}
          htmlId="miljo-select"
          label="Send til miljo*"
          error={state.validate && !notEmptyList(state.miljoer) ? 'Påkrevd' : null}
          options={
            !state.miljoOptions || state.miljoOptions?.length === 0
              ? []
              : state.miljoOptions?.map((value: string) => ({
                  value: value,
                  label: value.toUpperCase(),
                }))
          }
        />
        <SelectFormItem
          label="Adresse"
          htmlId="adresse-select"
          onChange={(value) =>
            // @ts-ignore
            dispatch({
              type: Action.SET_ADRESSE_ACTION,
              value: value && value.length > 0 ? value[0] : '',
            })
          }
          options={[
            {
              value: 'LAG_NY_ADRESSE',
              label: 'Lag ny adresse',
            },
            {
              value: 'ARV_FRA_MORS',
              label: 'Arv fra mors',
            },
            {
              value: 'ARV_FRA_FARS',
              label: 'Arv fra fars',
            },
          ]}
        />
      </Line>
      <Line>
        <DatePickerFormItem
          id="foedselsdato-field"
          label="Barnets fødselesdato*"
          onBlur={(value) => dispatch({ type: Action.SET_FOEDSELSDATO_ACTION, value: value })}
          required={true}
        />
      </Line>
      {state.errorList?.size > 0 && (
        <div>
          <h3>Feil ved sending til miljø</h3>
          <ul>
            {Array.from(state.errorList).map(([key, value]) => (
              <li key={key}>
                {key}: {value}
                <Button
                  variant={'tertiary'}
                  onClick={() => dispatch({ type: Action.REMOVE_ERROR_ITEM, value: key })}
                >
                  X
                </Button>
              </li>
            ))}
          </ul>
        </div>
      )}
    </EndringsmeldingForm>
  );
};
