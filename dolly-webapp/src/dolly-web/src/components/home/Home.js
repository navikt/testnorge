import React from 'react';
import { bindActionCreators } from 'redux';
import {connect} from 'react-redux';
import * as personActions from '../../actions/personActions';
import PersonList from './PersonList';
import {Input} from 'nav-frontend-skjema';


class Home extends React.Component{

    constructor(props, context){
        super(props, context);

        this.createPerson = this.createPerson.bind(this);
    }

    createPerson(){
        this.props.personsActions.createPerson(testPers);
    }


    render(){
        const {persons} = this.props;

        return (
            <div id="home-container">
                <h1>Main Container</h1>
                <Input label='testy' />
                <PersonList persons={persons}/>
                <button onClick={this.createPerson}>Ny Person</button>
            </div>
        )

    }
}

function mapStateToProps(stateFrom, ownProps){
    return {
        persons: stateFrom.personReducer
    };
}

function mapDispatchToProps(dispatch){
    return {
        personsActions: bindActionCreators(personActions, dispatch)
    }
}

const function_connectReduxAndComponent = connect(mapStateToProps, mapDispatchToProps);

export default function_connectReduxAndComponent(Home);

const testPers = {
    "id": "111",
    "fnr": "83435728331",
    "kjonn": "k",
    "fornavn": "teessst",
    "etternavn": "blaaaaa",
    "adresse": "xmwgoe"
};
