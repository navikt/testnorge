type SetHentMiljoerSuccessAction = 'SET_HENT_MILJOER_SUCCESS';
type SetHentMiljoerErrorAction = 'SET_HENT_MILJOER_ERROR';
type SubmitStartAction = 'SET_SUBMIT_START';
type SubmitSuccessAction = 'SET_SUBMIT_SUCCESS';
type SubmitErrorAction = 'SET_SUBMIT_ERROR';
type SubmitWarningAction = 'SET_SUBMIT_WARNING';
type SetIdentAction = 'SET_IDENT';

type Actions =
  | {
      type: SetHentMiljoerSuccessAction;
    }
  | {
      type: SetHentMiljoerErrorAction;
    }
  | {
      type: SubmitStartAction;
    }
  | {
      type: SubmitSuccessAction;
      successMessage: string;
    }
  | {
      type: SubmitErrorAction;
      errorMessage: string;
    }
  | {
      type: SubmitWarningAction;
      warningMessages: string[];
    }
  | {
      type: SetIdentAction;
      value: string;
    };

export type State = {
  ident: string;
  show: boolean;
  loading: boolean;
  errorMessage?: string;
  warningMessages?: string[];
  successMessage?: string;
};

export class Action {
  public static SET_HENT_MILJOER_SUCCESS_ACTION: SetHentMiljoerSuccessAction =
    'SET_HENT_MILJOER_SUCCESS';
  public static SET_HENT_MILJOER_ERROR_ACTION: SetHentMiljoerErrorAction = 'SET_HENT_MILJOER_ERROR';
  public static SET_IDENT_ACTION: SetIdentAction = 'SET_IDENT';
  public static SET_SUBMIT_START: SubmitStartAction = 'SET_SUBMIT_START';
  public static SET_SUBMIT_SUCCESS: SubmitSuccessAction = 'SET_SUBMIT_SUCCESS';
  public static SET_SUBMIT_ERROR: SubmitErrorAction = 'SET_SUBMIT_ERROR';
  public static SET_SUBMIT_WARNING: SubmitWarningAction = 'SET_SUBMIT_WARNING';
}

export const reducer = (state: State, action: Actions) => {
  switch (action.type) {
    case Action.SET_IDENT_ACTION:
      return {
        ...state,
        ident: action.value,
      };
    case Action.SET_HENT_MILJOER_SUCCESS_ACTION:
      return {
        ...state,
        show: true,
        errorMessage: null,
        successMessage: null,
      };
    case Action.SET_HENT_MILJOER_ERROR_ACTION:
      return {
        ...state,
        show: false,
        errorMessage: null,
        successMessage: null,
      };
    case Action.SET_SUBMIT_START:
      return {
        ...state,
        loading: true,
        successMessage: null,
        warningMessages: null,
        errorMessage: null,
      };
    case Action.SET_SUBMIT_ERROR:
      return {
        ...state,
        loading: false,
        show: false,
        errorMessage: action.errorMessage,
      };
    case Action.SET_SUBMIT_WARNING:
      return {
        ...state,
        loading: false,
        show: true,
        warningMessages: action.warningMessages,
      };
    case Action.SET_SUBMIT_SUCCESS:
      return {
        ...state,
        loading: false,
        show: false,
        successMessage: action.successMessage,
      };
    default:
      return state;
  }
};
