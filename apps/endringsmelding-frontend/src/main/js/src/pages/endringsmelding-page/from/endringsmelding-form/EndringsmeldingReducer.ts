type SetHentMiljoerSuccessAction = 'SET_HENT_MILJOER_SUCCESS';
type SetHentMiljoerErrorAction = 'SET_HENT_MILJOER_ERROR';
type SubmitStartAction = 'SET_SUBMIT_START';
type SubmitSuccessAction = 'SET_SUBMIT_SUCCESS';
type SubmitErrorAction = 'SET_SUBMIT_ERROR';
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
      type: SetIdentAction;
      value: string;
    };

export type State = {
  ident: string;
  success: boolean;
  loading: boolean;
  errorMessage?: string;
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
        success: true,
        errorMessage: null,
        successMessage: null,
      };
    case Action.SET_HENT_MILJOER_ERROR_ACTION:
      return {
        ...state,
        success: false,
        errorMessage: null,
        successMessage: null,
      };
    case Action.SET_SUBMIT_START:
      return { ...state, loading: true };
    case Action.SET_SUBMIT_ERROR:
      return {
        ...state,
        loading: false,
        errorMessage: action.errorMessage,
      };
    case Action.SET_SUBMIT_SUCCESS:
      return {
        ...state,
        loading: false,
        successMessage: action.successMessage,
      };
    default:
      return state;
  }
};
