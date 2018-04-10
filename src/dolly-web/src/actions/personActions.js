import * as types from './actionTypes';
import axios from 'axios';

// URL
const mockApiUrl = "http://localhost:3050";


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
            const getPersons = async () => {
                const response = await axios.get(mockApiUrl+"/persons");
                dispatch(loadPersonsSuccess(response.data));
            };
            return getPersons();

        } catch(error) {
            console.log(error);
        }
    }
}

export function createPerson(person){
    console.log("Create person called");
    console.log(person);
    return dispatch => {
        try{
            const createPerson = async () => {
                let response = await axios.post(mockApiUrl+"/persons", person);
                console.log(response.data);
                dispatch(createPersonSuccess(response.data));
            };
            return createPerson();

        } catch(error){
            console.log(error)
        }
    }
}

export function updatePerson(id){
    return dispatch => {
        try{
            const updatePerson = async () => {
                let response = await axios.post(mockApiUrl+"/persons/"+id, id);
                dispatch(updatePersonSuccess(response.data));
            };
            return updatePerson();

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
