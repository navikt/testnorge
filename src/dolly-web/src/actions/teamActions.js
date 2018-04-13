import * as types from './actionTypes';
import axios from 'axios';
import ContentApi from '../ContentApi';


export function loadTeamsSuccess(grupper){
    return {
        type: types.LOAD_TEAMS_SUCCESS,
        grupper: grupper
    };
}

export function fetchTeam() {
    return dispatch => {
        try{
            return (async () => {
                const response = await axios.get(ContentApi.getGrupper());
                dispatch(loadTeamsSuccess(response.data));
            })();

        } catch(error) {
            console.log(error);
        }
    }
}
