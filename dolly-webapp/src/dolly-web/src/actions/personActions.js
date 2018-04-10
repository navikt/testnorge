import * as types from './actionTypes';
import axios from 'axios';
import ContentApi from '../ContentApi';


export function loadPersonsSuccess(persons){
    return {
        type: types.LOAD_PERSONS_SUCCESS,
        persons: persons
    };
}

export function createPersonSuccess(person){
    return {
        type: types.CREATE_PERSONS_SUCCESS,
        person: person
    };
}

export function updatePersonSuccess(person){
    return {
        type: types.UPDATE_PERSONS_SUCCESS,
        person: person
    };
}

export function fetchPersons() {
    return dispatch => {
        try{
            return (async () => {
                const response = await axios.get(ContentApi.getPersons());
                dispatch(loadPersonsSuccess(response.data));
            })();

        } catch(error) {
            console.log(error);
        }
    }
}

export function createPerson(person){
    return dispatch => {
        try{
            return (async () => {
                const response = await axios.post(ContentApi.postPersons(), person);
                dispatch(createPersonSuccess(response.data));
            })();

        } catch(error){
            console.log(error)
        }
    }
}

export function updatePerson(person){
    return dispatch => {
        try{
            return (async () => {
                const response = await axios.post(ContentApi.putPersons(person.id), person);
                dispatch(updatePersonSuccess(response.data));
            })();

        } catch(error){
            console.log(error)
        }
    }
}

/*
        return PersonApi.fetchAllPersons().then(fetchedPersons => {

            console.log("--> Action creator: fetch persons ");
            dispatch(loadPersonsSuccess(fetchedPersons))

        }).catch(error => {
            throw (error);
        })



export function createPerson(person){
    return dispatch => {
        return PersonApi.createPerson(person).then(createdPerson => {

            dispatch(createPersonSuccess(createdPerson));

        }).catch(error => {
            throw (error);
        })
    }
}
 */
