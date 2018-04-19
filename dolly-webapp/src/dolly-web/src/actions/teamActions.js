import * as types from './actionTypes';
import axios from 'axios';
import ContentApi from '../ContentApi';


export function loadTeamsSuccess(teams){
    return {
        type: types.LOAD_TEAMS_SUCCESS,
        teams: teams
    };
}

export function createTeamSuccess(team){
    return {
        type: types.CREATE_TEAM_SUCCESS,
        team: team
    };
}

export function fetchTeams() {
    return dispatch => {
        try{
            return (async () => {
                const response = await axios.get(ContentApi.getTeams());
                dispatch(loadTeamsSuccess(response.data));
            })();

        } catch(error) {
            console.log(error);
        }
    }
}
