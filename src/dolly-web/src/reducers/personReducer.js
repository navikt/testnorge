import * as types from '../actions/actionTypes';

export default function personReducer(state = [], actionObj) {
    switch (actionObj.type){
        case types.LOAD_PERSONS_SUCCESS:
            console.log("--> Reducer: LOAD PERSONS state update");
            return actionObj.persons;
        case types.CREATE_PERSONS_SUCCESS:
            console.log("--> Reducer: CREATE PERSONS state update");
            return [
                ...state,
                Object.assign({}, actionObj.person)
            ];
        case types.UPDATE_PERSONS_SUCCESS:
            return [
                ...state.filter(person => person.id !== actionObj.person.id),
                Object.assign({}, actionObj.person)
            ];
        default:
            console.log("--> PersonReducer: case - default");
            return state;
    }
}