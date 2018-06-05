import axios from 'axios'
import ContentApi from '~/ContentApi'

export const types = {
    CREATE_GRUPPE_SUCCESS: 'CREATE_GRUPPE_SUCCESS',
    UPDATE_GRUPPE_SUCCES: 'UPDATE_GRUPPE_SUCCES',
    LOAD_GRUPPER_SUCCESS: 'LOAD_GRUPPER_SUCCESS'
}

const initialState = [];

export default (state = initialState, action) => {
    switch (action.type) {
        case types.LOAD_GRUPPER_SUCCESS:
            return action.grupper
        case types.UPDATE_GRUPPE_SUCCES:
            return [
                ...state.filter(gruppe => gruppe.id !== action.gruppe.id),
                Object.assign({}, action.gruppe)
            ];
        case types.CREATE_GRUPPE_SUCCESS:
            return [...state, Object.assign({}, action.gruppe)]
        default:
            return state
    }
}

const loadGrupperSuccess = grupper => ({
    type: types.LOAD_GRUPPER_SUCCESS,
    grupper: grupper
});

export const updateGruppeSuccess = gruppe => ({
    type: types.UPDATE_GRUPPE_SUCCES,
    gruppe: gruppe
});

export const createGruppeSuccess = gruppe => ({
    type: types.CREATE_GRUPPE_SUCCESS,
    gruppe: gruppe
});


// THUNKS
export const fetchGrupper = () => async dispatch => {
    try {
        const response = await axios.get(ContentApi.getGrupper());
        dispatch(loadGrupperSuccess(response.data));
    } catch (error) {
        console.log(error)
    }
};

export const updateGruppe = (gruppe) => async dispatch => {
    try {
        const response = await axios.post(ContentApi.putGruppe(gruppe.id), gruppe);
        dispatch(updateGruppeSuccess(response.data))
    } catch (error) {
        console.log(error)
    }
};
