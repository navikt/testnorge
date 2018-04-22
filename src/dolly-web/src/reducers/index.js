import gruppeReducer from './gruppeReducer';
import teamReducer from './teamReducer';

// Knytter alle reducerene i prosjektet samme
const reducerCollection = {
    gruppeReducer: gruppeReducer,
    teamReducer: teamReducer
};

export default reducerCollection;