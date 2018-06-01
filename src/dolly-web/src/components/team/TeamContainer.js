import React, { Component } from 'react'
import { connect } from 'react-redux'
import TeamForm from './TeamForm'
import axios from 'axios'
import ContentApi from '../../ContentApi'
import { createTeamSuccess } from '~/ducks/team'

class Team extends Component {
	onClickSave = team => {
		let team_obj = Object.assign({}, team)
		this.opprettTeam(team_obj).catch(error => {
			console.log(error)
		})
	}

	opprettTeam = async team => {
		const response = await axios.post(ContentApi.postTeams(), team)
		this.props.createTeamSuccess(response.data)
	}

	render() {
		return <TeamForm onClickSave={this.onClickSave} />
	}
}

// Todo: Flytt til connector
const mapDispatchToProps = dispatch => ({
	createTeamSuccess: res => dispatch(createTeamSuccess(res))
})

export default connect(
	null,
	mapDispatchToProps
)(Team)
