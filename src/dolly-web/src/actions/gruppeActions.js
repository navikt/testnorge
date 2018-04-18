import * as types from './actionTypes';
import axios from 'axios';
import ContentApi from '../ContentApi';

export function loadGrupperSuccess(grupper){
    return {
        type: types.LOAD_GRUPPER_SUCCESS,
        grupper: grupper
    };
}

export function updateGruppeSuccess(gruppe){
    return {
        type: types.UPDATE_GRUPPE_SUCCES,
        gruppe: gruppe
    };
}

export function fetchGrupper() {
    return dispatch => {
        try{
            return (async () => {
                const response = await axios.get(ContentApi.getGrupper());
                dispatch(loadGrupperSuccess(response.data));
            })();

        } catch(error) {
            console.log(error);
        }
    }
}
