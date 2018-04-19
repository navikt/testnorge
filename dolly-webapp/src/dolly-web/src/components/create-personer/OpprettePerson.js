import React, {Component} from 'react';
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import * as gruppeActions from '../../actions/gruppeActions';
import axios from 'axios';
import ContentApi from '../../ContentApi';

class CreatePerson extends Component {
    constructor(props, context) {
        super(props, context);

        this.state = {};
    }

    //TODO Skal hente personer basert på testidenter i gruppen etter hvert.
    componentDidMount() {

    }

    render() {
        return (
            <div id="create-person-container">
            </div>
        )
    }
}

function mapStateToProps(stateFrom, ownProps) {
    return {
        grupperState: stateFrom.gruppeReducer
    };
}

function mapDispatchToProps(dispatch) {
    return {
        gruppeActions: bindActionCreators(gruppeActions, dispatch)
    }
}

const function_connectReduxAndComponent = connect(mapStateToProps, mapDispatchToProps);

export default function_connectReduxAndComponent(CreatePerson);

