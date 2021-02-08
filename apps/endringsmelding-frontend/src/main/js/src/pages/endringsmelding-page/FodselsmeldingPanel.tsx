import React, { useReducer } from 'react';
import Panel from 'nav-frontend-paneler';
import Search from '@/components/search/Search';
import { Input, Label } from 'nav-frontend-skjema';
import { Datepicker } from 'nav-datovelger';
import { Hovedknapp } from 'nav-frontend-knapper';
import { Select } from '@/components/select';
import styled from 'styled-components';

import './FodselsmeldingPanel.less';

type Action =
  | {
      type: 'SET_FARS_IDENT';
      value: string;
    }
  | {
      type: 'SET_MILJOER';
      value: string[];
    }
  | {
      type: 'SET_IDENT_TYPE';
      value: string;
    }
  | {
      type: 'SET_KJOEN_TYPE';
      value: string;
    }
  | {
      type: 'SET_ADRESSE_ACTION';
      value: string;
    }
  | {
      type: 'SET_MORS_IDENT';
      value: string;
    }
  | {
      type: 'SET_FOEDSELSDATO';
      value: string;
    }
  | {
      type: 'SET_MILJOER_OPTIONS';
      value: string[];
    }
  | {
      type: 'SET_SUCCESS';
      value: boolean;
    };

const reducer = (state: State, action: Action) => {
  switch (action.type) {
    case 'SET_MORS_IDENT':
      return { ...state, morsIdent: action.value };
    case 'SET_SUCCESS':
      return { ...state, success: action.value };
    case 'SET_FARS_IDENT':
      return { ...state, farsIdent: action.value };
    case 'SET_FOEDSELSDATO':
      return { ...state, foedselsdato: action.value };
    case 'SET_IDENT_TYPE':
      return { ...state, identType: action.value };
    case 'SET_KJOEN_TYPE':
      return { ...state, kjoenType: action.value };
    case 'SET_ADRESSE_ACTION':
      return { ...state, addressAction: action.value };
    case 'SET_MILJOER':
      return { ...state, miljoer: action.value };
    case 'SET_MILJOER_OPTIONS':
      return { ...state, miljoOptions: action.value };
    default:
      return state;
  }
};

type State = {
  miljoOptions: [];
  kjoenType: string;
  identType: string;
  farsIdent: string;
  morsIdent: string;
  foedselsdato: string;
  addressAction: string;
  miljoer: string[];
  success: boolean;
};

const initState: State = {
  miljoOptions: [],
  kjoenType: '',
  identType: '',
  farsIdent: '',
  morsIdent: '',
  foedselsdato: '',
  addressAction: '',
  miljoer: [],
  success: false,
};

const Form = styled.form`
  display: flex;
  flex-direction: column;
  align-content: space-around;
  justify-content: space-between;
`;

const Row = styled.div<{ reverse?: boolean }>`
  padding-top: 30px;
  display: flex;
  flex-direction: ${(props) => (props.reverse ? 'row-reverse' : 'row')};
`;

const InputItem = styled(Input)`
  width: 50%;
  padding-right: 10px;
`;

const SelectItem = styled(Select)`
  width: 25%;
  padding-right: 10px;
`;

const DateWrapper = styled.div`
  width: 25%;
  padding-right: 10px;
`;

const Knapp = styled(Hovedknapp)``;
export default () => {
  const [state, dispatch] = useReducer(reducer, initState);

  console.log(state);

  const onSearch = (value: string) =>
    new Promise<string[]>((resolve, reject) =>
      setTimeout(() => resolve(['T4', 't5', 'T6', 'T7']), 500)
    )
      .then((response) => {
        dispatch({ type: 'SET_MILJOER_OPTIONS', value: response });
        dispatch({ type: 'SET_SUCCESS', value: true });
      })
      .catch((e) => {
        dispatch({ type: 'SET_SUCCESS', value: false });
        throw e;
      });

  return (
    <Panel border style={{ borderTop: 0, borderTopLeftRadius: 0, borderTopRightRadius: 0 }}>
      <Form>
        <Search
          onSearch={onSearch}
          onBlur={(value) => dispatch({ type: 'SET_MORS_IDENT', value: value })}
          labels={{
            label: 'Mors ident',
            button: 'Søk',
            onFound: 'Person funnet',
            onNotFound: 'Person ikke funnet',
          }}
        />
        {state.success && (
          <>
            <Row>
              <InputItem
                label="Fars ident"
                defaultValue=""
                onBlur={(e) => dispatch({ type: 'SET_FARS_IDENT', value: e.target.value })}
              />
              <SelectItem
                label="Barnets identtype"
                htmlId="barnets-identtype-select"
                onChange={(value) =>
                  dispatch({
                    type: 'SET_IDENT_TYPE',
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
              <DateWrapper>
                <Label htmlFor="foedselsdato-field">Barnets fødselesdato</Label>
                <Datepicker
                  inputId="foedselsdato-field"
                  onChange={(value) => dispatch({ type: 'SET_FOEDSELSDATO', value: value })}
                  value={state.foedselsdato}
                />
              </DateWrapper>
            </Row>
            <Row>
              <SelectItem
                label="Barnets kjønn"
                htmlId="barnets-kjoen-select"
                onChange={(value) =>
                  dispatch({
                    type: 'SET_KJOEN_TYPE',
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
              <SelectItem
                onChange={(value) => dispatch({ type: 'SET_MILJOER', value: value })}
                htmlId="miljo-select"
                multi={true}
                label="Send til miljo"
                options={state.miljoOptions.map((value) => ({
                  value: value,
                  label: value.toUpperCase(),
                }))}
              />
              <SelectItem
                label="Adresse"
                htmlId="adresse-select"
                onChange={(value) =>
                  dispatch({
                    type: 'SET_ADRESSE_ACTION',
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
            </Row>
            <Row reverse={true}>
              <Knapp onClick={(event) => event.preventDefault()} htmlType="submit">
                Opprett fødselsmelding
              </Knapp>
            </Row>
          </>
        )}
      </Form>
    </Panel>
  );
};
