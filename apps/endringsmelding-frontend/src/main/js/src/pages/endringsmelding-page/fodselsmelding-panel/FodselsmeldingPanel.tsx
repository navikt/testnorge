import React, { useReducer } from 'react';
import Panel from 'nav-frontend-paneler';
import Search from '@/components/search/Search';
import { Hovedknapp } from 'nav-frontend-knapper';
import styled from 'styled-components';

import { DatePickerFormItem, Form, InputFormItem, Line, SelectFormItem } from '@/components/form';
import reducer, { Action, State } from './FodselsmedlingReducer';
import EndringsmeldingService from '@/service/EndringsmeldingService';

const Knapp = styled(Hovedknapp)``;

export const initState: State = {
  miljoOptions: [],
  kjoenType: 'GUTT',
  identType: 'FNR',
  farsIdent: '',
  morsIdent: '',
  foedselsdato: '',
  addressAction: 'LAG_NY_ADRESSE',
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
    EndringsmeldingService.fetchMiljoer(value)
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
    return notEmptyString(state.foedselsdato) && notEmptyList(state.miljoer);
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
          onBlur={(value) => dispatch({ type: Action.SET_MORS_IDENT_ACTION, value: value })}
          labels={{
            label: 'Mors ident',
            button: 'Søk',
            onFound: 'Person funnet',
            onNotFound: 'Person ikke funnet',
          }}
        />
        {state.success && (
          <>
            <Line>
              <InputFormItem
                label="Fars ident"
                defaultValue=""
                onBlur={(e) =>
                  dispatch({ type: Action.SET_FARS_IDENT_ACTION, value: e.target.value })
                }
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
              <DatePickerFormItem
                id="foedselsdato-field"
                label="Barnets fødselesdato*"
                onBlur={(value) => dispatch({ type: Action.SET_FOEDSELSDATO_ACTION, value: value })}
                error={state.validate && !notEmptyString(state.foedselsdato) ? 'På kreved' : null}
              />
            </Line>
            <Line>
              <SelectFormItem
                label="Barnets kjønn"
                htmlId="barnets-kjoen-select"
                onChange={(value) =>
                  dispatch({
                    type: Action.SET_KJOEN_TYPE_ACTION,
                    value: value && value.length > 0 ? value[0] : '',
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
                multi={true}
                label="Send til miljo"
                error={state.validate && !notEmptyList(state.miljoer) ? 'På kreved' : null}
                options={state.miljoOptions.map((value: string) => ({
                  value: value,
                  label: value.toUpperCase(),
                }))}
              />
              <SelectFormItem
                label="Adresse"
                htmlId="adresse-select"
                onChange={(value) =>
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
            <Line reverse={true}>
              <Knapp onClick={onSubmit} htmlType="submit">
                Opprett fødselsmelding
              </Knapp>
            </Line>
          </>
        )}
      </Form>
    </Panel>
  );
};
