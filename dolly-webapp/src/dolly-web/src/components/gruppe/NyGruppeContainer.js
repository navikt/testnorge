import React, {Component} from 'react';
import NyGruppeForm from './NyGruppeForm';
import axios from 'axios';
import ContentApi from '../../ContentApi';
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import * as gruppeActions from '../../actions/gruppeActions';


export class NyGruppe extends Component {
    constructor(props, context){
        super(props, context);

        this.onClickSave = this.onClickSave.bind(this);
        this.opprettGruppe = this.opprettGruppe.bind(this);
    }

    onClickSave(gruppe) {
        let gruppe_obj = Object.assign({}, gruppe);
        this.opprettGruppe(gruppe_obj).catch(error => {
            console.log(error);
        });
    }

    async opprettGruppe(gruppe){
        const response = await axios.post(ContentApi.postGrupper(), gruppe);
        this.props.gruppeActions.createGruppeSuccess(response.data);
    }

    render() {

        return(
            <NyGruppeForm
                onClickSave={this.onClickSave}
                onChangeInput={this.onInputChange}
            />
        )
    }
}

function mapDispatchToProps(dispatch) {
    return {
        gruppeActions: bindActionCreators(gruppeActions, dispatch)
    }
}

const function_connectReduxAndComponent = connect(null, mapDispatchToProps);

export default function_connectReduxAndComponent(NyGruppe);
