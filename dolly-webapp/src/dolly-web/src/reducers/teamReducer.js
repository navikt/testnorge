import * as types from '../actions/actionTypes';

export default function teamReducer(state = [], actionObj) {
    switch (actionObj.type){
        case types.LOAD_TEAMS_SUCCESS:
            return actionObj.teams;
        case types.CREATE_TEAM_SUCCESS:
            return [
                ...state, Object.assign({}, actionObj.team)
            ];
        default:
            return state;
    }
}
