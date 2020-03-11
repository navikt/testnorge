import {createActions, handleActions} from 'redux-actions'
import {FasteDataApi} from '~/service/Api'
import {onSuccess, onRequest} from '~/ducks/utils/requestActions'

export const {getFasteData} = createActions(
    {
        getFasteData: FasteDataApi.getFasteDataTps
    },
    {
        prefix: 'fasteData' // String used to prefix each type
    }
)

const initialState = {
    data: {}
}


export default handleActions(
    {
        [onSuccess(getFasteData)](state, action) {
            state.data = action.payload.data
        }

    },
    initialState
)

export const fetchFasteData = () => (dispatch, getState) => {
    // const { fasteData } = getState()
    // if (kodeverk.data || kodeverk.loading) return false
    return dispatch(getFasteData())
}

export const getFnrFraFasteData = (state) => {
    var liste = []
    for (d in state.data){
        liste = liste + d.fnr;
    }
    return liste
}


