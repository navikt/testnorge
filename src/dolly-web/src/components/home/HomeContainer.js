import React, {Component} from 'react';
import { bindActionCreators } from 'redux';
import {connect} from 'react-redux';
import * as gruppeActions from '../../actions/gruppeActions';
import GruppeList from "./GruppeListe";
import {Input} from 'nav-frontend-skjema';

class Home extends Component{
    constructor(props, context){
        super(props, context);

        this.createPerson = this.createPerson.bind(this);
    }

    createPerson(){
        //this.props.personsActions.createPerson({});
    }


    render(){
        const {grupper} = this.props;

        return (
            <div id="home-container">
                <h1>Home Container</h1>
                <Input label='testy' />
                <input label="Bare" />
                <GruppeList grupper={grupper}/>
            </div>
        )

    }
}

function mapStateToProps(stateFrom, ownProps){
    return {
        grupper: stateFrom.gruppeReducer
    };
}

function mapDispatchToProps(dispatch){
    return {
        gruppeActions: bindActionCreators(gruppeActions, dispatch)
    }
}

const function_connectReduxAndComponent = connect(mapStateToProps, mapDispatchToProps);

export default function_connectReduxAndComponent(Home);
