import * as types from '../actions/actionTypes';

export default function gruppeReducer(state = [], actionObj) {
    switch (actionObj.type){
        case types.LOAD_GRUPPER_SUCCESS:
            return actionObj.grupper;
        case types.UPDATE_GRUPPE_SUCCES:
            return [
                ...state.filter(gruppe => gruppe.id !== actionObj.gruppe.id),
                Object.assign({}, actionObj.gruppe)
            ];
        case types.CREATE_GRUPPE_SUCCESS:
            return [
                ...state, Object.assign({}, actionObj.gruppe)
            ];
        default:
            return state;
    }
}
