import React, {Component} from 'react';
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import * as gruppeActions from '../../actions/gruppeActions';
import axios from 'axios';
import PersonListe from './PersonListe';
import ContentApi from '../../ContentApi';


class Gruppe extends Component {
    constructor(props, context) {
        super(props, context);

        const gruppeId = parseInt(props.match.params.gruppeId); //TODO når bruker ekte API så vil id være samme type, kan da fjerne denne
        let gruppe = this.props.grupper.find(gruppe => gruppe.id === gruppeId);

        this.state = {
            personer: [],
            gruppe: gruppe
        };
    }

    //TODO Skal hente personer basert på testidenter i gruppen etter hvert.
    componentDidMount(){
        try {
            (async () => {
                const response = await axios.get(ContentApi.getPersons());
                this.setState( {personer: response.data });
            })();

        } catch (error) {
            console.log(error);
        }
    }

    render() {
        return (
            <div id="gruppe-container">
                <h1>Gruppe Container</h1>
                <PersonListe personer={this.state.personer}/>
            </div>
        )
    }
}

function mapStateToProps(stateFrom, ownProps) {
    return {
        grupper: stateFrom.gruppeReducer
    };
}

function mapDispatchToProps(dispatch) {
    return {
        gruppeActions: bindActionCreators(gruppeActions, dispatch)
    }
}

const function_connectReduxAndComponent = connect(mapStateToProps, mapDispatchToProps);

export default function_connectReduxAndComponent(Gruppe);

const testPers = {
    "id": "111",
    "fnr": "83435728331",
    "kjonn": "k",
    "fornavn": "teessst",
    "etternavn": "blaaaaa",
    "adresse": "xmwgoe"
};
