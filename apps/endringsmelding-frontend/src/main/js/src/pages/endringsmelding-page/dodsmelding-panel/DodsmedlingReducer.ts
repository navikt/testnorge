type SetMiljoerOptionsAction = 'SET_MILJOER_OPTIONS';
type SetIdentAction = 'SET_IDENT';
type SetMiljoerAction = 'SET_MILJOER';
type SetHandlingAction = 'SET_HANDLING';
type SetDodsdatoAction = 'SET_DODSDATO';
type SetSuccessAction = 'SET_SUCCESS';
type SetValidateAction = 'SET_VALIDATE';

type Actions =
  | {
      type: SetIdentAction;
      value: string;
    }
  | {
      type: SetMiljoerAction;
      value: string[];
    }
  | {
      type: SetHandlingAction;
      value: string;
    }
  | {
      type: SetDodsdatoAction;
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
  handing: string;
  ident: string;
  dodsdato: string;
  miljoer: string[];
  success: boolean;
  validate: boolean;
};

export class Action {
  public static SET_MILJOER_OPTIONS_ACTION: SetMiljoerOptionsAction = 'SET_MILJOER_OPTIONS';
  public static SET_IDENT_ACTION: SetIdentAction = 'SET_IDENT';
  public static SET_SUCCESS_ACTION: SetSuccessAction = 'SET_SUCCESS';
  public static SET_HANDLING_ACTION: SetHandlingAction = 'SET_HANDLING';
  public static SET_DODSDATO_ACTION: SetDodsdatoAction = 'SET_DODSDATO';
  public static SET_MILJOER_ACTION: SetMiljoerAction = 'SET_MILJOER';
  public static SET_VALIDATE_ACTION: SetValidateAction = 'SET_VALIDATE';
}

export default (state: State, action: Actions) => {
  switch (action.type) {
    case Action.SET_IDENT_ACTION:
      return { ...state, ident: action.value };
    case Action.SET_SUCCESS_ACTION:
      return { ...state, success: action.value };
    case Action.SET_HANDLING_ACTION:
      return { ...state, handling: action.value };
    case Action.SET_DODSDATO_ACTION:
      return { ...state, dodsdato: action.value };
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
