import React, {Component} from 'react';
import { bindActionCreators } from 'redux';
import {connect} from 'react-redux';
import * as gruppeActions from '../../actions/gruppeActions';
import GruppeList from "./GruppeListe";
import {Input} from 'nav-frontend-skjema';
import InputDatePicker from '../felles/fields/InputDatePicker';
import moment from 'moment';

class Home extends Component{
    constructor(props, context){
        super(props, context);
        this.state = {
            date: moment(),
            date_formatted: ""
        };

        this.createPerson = this.createPerson.bind(this);
        this.onChangeTest = this.onChangeTest.bind(this);
    }

    createPerson(){
        //this.props.personsActions.createPerson({});
    }

    onChangeTest(date) {
        let date_formatted = date.format("DD/MM/YYYY");

        this.setState( {
            date_formatted: date_formatted,
            date: date
        });

    }


    render(){
        const {grupper} = this.props;

        return (
            <div id="home-container">
                <h1>Home Container</h1>
                <Input label='testy' />
                <input label="Bare" />
                <GruppeList grupper={grupper}/>

                <InputDatePicker
                    id="test"
                    dateFormat="DD/MM/YYYY"
                    label="Date"
                    onChange={this.onChangeTest}
                    statePropToChange={this.state.date}
                />
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
