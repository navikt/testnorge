type SetMiljoerOptionsAction = 'SET_MILJOER_OPTIONS';
type SetIdentAction = 'SET_IDENT';
type SetMiljoerAction = 'SET_MILJOER';
type SetHandlingAction = 'SET_HANDLING';
type SetDoedsdatoAction = 'SET_DOEDSDATO';
type SetValidateAction = 'SET_VALIDATE';
type SetErrorListAction = 'SET_ERROR_LIST';
type RemoveErrorItemAction = 'REMOVE_ERROR_ITEM';

type Handling = 'SETTE_DOEDSDATO' | 'ENDRET_DOEDSDATO' | 'ANNULLERE_DOEDSDATO';

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
      value: Handling;
    }
  | {
      type: SetDoedsdatoAction;
      value: string;
    }
  | {
      type: SetErrorListAction;
      value: Map<string, string>;
    }
  | {
      type: RemoveErrorItemAction;
      value: string;
    }
  | {
      type: SetMiljoerOptionsAction;
      value: string[];
    }
  | {
      type: SetValidateAction;
      value: boolean;
    };

export type State = {
  miljoOptions: string[];
  handling: Handling;
  ident: string;
  doedsdato: string;
  miljoer: string[];
  validate: boolean;
  errorList: Map<string, string>;
};

export class Action {
  public static SET_MILJOER_OPTIONS_ACTION: SetMiljoerOptionsAction = 'SET_MILJOER_OPTIONS';
  public static SET_IDENT_ACTION: SetIdentAction = 'SET_IDENT';
  public static SET_HANDLING_ACTION: SetHandlingAction = 'SET_HANDLING';
  public static SET_DOEDSDATO_ACTION: SetDoedsdatoAction = 'SET_DOEDSDATO';
  public static SET_MILJOER_ACTION: SetMiljoerAction = 'SET_MILJOER';
  public static SET_VALIDATE_ACTION: SetValidateAction = 'SET_VALIDATE';
  public static SET_ERROR_LIST: SetErrorListAction = 'SET_ERROR_LIST';
  public static REMOVE_ERROR_ITEM: RemoveErrorItemAction = 'REMOVE_ERROR_ITEM';
}

export default (state: State, action: Actions) => {
  switch (action.type) {
    case Action.SET_IDENT_ACTION:
      return { ...state, ident: action.value.trim() };
    case Action.SET_HANDLING_ACTION:
      if (action.value === 'ANNULLERE_DOEDSDATO') {
        return { ...state, doedsdato: null, handling: action.value };
      }
      return { ...state, handling: action.value };
    case Action.SET_DOEDSDATO_ACTION:
      return { ...state, doedsdato: action.value };
    case Action.SET_MILJOER_ACTION:
      return { ...state, miljoer: action.value };
    case Action.SET_VALIDATE_ACTION:
      return { ...state, validate: action.value };
    case Action.SET_MILJOER_OPTIONS_ACTION:
      return { ...state, miljoOptions: action.value };
    case Action.SET_ERROR_LIST:
      return { ...state, errorList: action.value };
    case Action.REMOVE_ERROR_ITEM:
      const newErrorList = new Map(state.errorList);
      newErrorList.delete(action.value);
      return { ...state, errorList: newErrorList };
    default:
      return state;
  }
};
