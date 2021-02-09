type SetMiljoerOptionsAction = 'SET_MILJOER_OPTIONS';
type SetFarsIdentAction = 'SET_FARS_IDENT';
type SetMiljoerAction = 'SET_MILJOER';
type SetIdentTypeAction = 'SET_IDENT_TYPE';
type SetKjoenTypeAction = 'SET_KJOEN_TYPE';
type SetAdresseAction = 'SET_ADRESSE';
type SetMorsIdentAction = 'SET_MORS_IDENT';
type SetFoedselsdatoAction = 'SET_FOEDSELSDATO';
type SetSuccessAction = 'SET_SUCCESS';
type SetValidateAction = 'SET_VALIDATE';

type Actions =
  | {
      type: SetFarsIdentAction;
      value: string;
    }
  | {
      type: SetMiljoerAction;
      value: string[];
    }
  | {
      type: SetIdentTypeAction;
      value: string;
    }
  | {
      type: SetKjoenTypeAction;
      value: string;
    }
  | {
      type: SetAdresseAction;
      value: string;
    }
  | {
      type: SetMorsIdentAction;
      value: string;
    }
  | {
      type: SetFoedselsdatoAction;
      value: string;
    }
  | {
      type: SetMiljoerOptionsAction;
      value: string[];
    }
  | {
      type: SetSuccessAction;
      value: boolean;
    }
  | {
      type: SetValidateAction;
      value: boolean;
    };

export type State = {
  miljoOptions: string[];
  kjoenType: string;
  identType: string;
  farsIdent: string;
  morsIdent: string;
  foedselsdato: string;
  addressAction: string;
  miljoer: string[];
  success: boolean;
  validate: boolean;
};

export class Action {
  public static SET_MILJOER_OPTIONS_ACTION: SetMiljoerOptionsAction = 'SET_MILJOER_OPTIONS';
  public static SET_MORS_IDENT_ACTION: SetMorsIdentAction = 'SET_MORS_IDENT';
  public static SET_SUCCESS_ACTION: SetSuccessAction = 'SET_SUCCESS';
  public static SET_FARS_IDENT_ACTION: SetFarsIdentAction = 'SET_FARS_IDENT';
  public static SET_FOEDSELSDATO_ACTION: SetFoedselsdatoAction = 'SET_FOEDSELSDATO';
  public static SET_IDENT_TYPE_ACTION: SetIdentTypeAction = 'SET_IDENT_TYPE';
  public static SET_KJOEN_TYPE_ACTION: SetKjoenTypeAction = 'SET_KJOEN_TYPE';
  public static SET_ADRESSE_ACTION: SetAdresseAction = 'SET_ADRESSE';
  public static SET_MILJOER_ACTION: SetMiljoerAction = 'SET_MILJOER';
  public static SET_VALIDATE_ACTION: SetValidateAction = 'SET_VALIDATE';
}

export default (state: State, action: Actions) => {
  switch (action.type) {
    case Action.SET_MORS_IDENT_ACTION:
      console.log(action.value);

      return { ...state, morsIdent: action.value };
    case Action.SET_SUCCESS_ACTION:
      return { ...state, success: action.value };
    case Action.SET_FARS_IDENT_ACTION:
      return { ...state, farsIdent: action.value };
    case Action.SET_FOEDSELSDATO_ACTION:
      return { ...state, foedselsdato: action.value };
    case Action.SET_IDENT_TYPE_ACTION:
      return { ...state, identType: action.value };
    case Action.SET_KJOEN_TYPE_ACTION:
      return { ...state, kjoenType: action.value };
    case Action.SET_ADRESSE_ACTION:
      return { ...state, addressAction: action.value };
    case Action.SET_MILJOER_ACTION:
      return { ...state, miljoer: action.value };
    case Action.SET_VALIDATE_ACTION:
      return { ...state, validate: action.value };
    case Action.SET_MILJOER_OPTIONS_ACTION:
      return { ...state, miljoOptions: action.value };
    default:
      return state;
  }
};
