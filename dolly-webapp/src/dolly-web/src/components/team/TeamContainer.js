import React, {Component} from 'react';
import TeamForm from './TeamForm';
import axios from 'axios';
import ContentApi from '../../ContentApi';
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import * as teamActions from '../../actions/teamActions';


export class Team extends Component {
    constructor(props, context){
        super(props, context);

        this.onClickSave = this.onClickSave.bind(this);
        this.opprettTeam = this.opprettTeam.bind(this);
    }

    onClickSave(team) {
        let team_obj = Object.assign({}, team);
        this.opprettTeam(team_obj).catch(error => {
            console.log(error);
        });
    }

    async opprettTeam(team){
        const response = await axios.post(ContentApi.postTeams(), team);
        this.props.teamActions.createTeamSuccess(response.data);
    }

    render() {

        return(
            <TeamForm
                onClickSave={this.onClickSave}
            />
        )
    }
}

function mapDispatchToProps(dispatch) {
    return {
        teamActions: bindActionCreators(teamActions, dispatch)
    }
}

const function_connectReduxAndComponent = connect(null, mapDispatchToProps);

export default function_connectReduxAndComponent(Team);
