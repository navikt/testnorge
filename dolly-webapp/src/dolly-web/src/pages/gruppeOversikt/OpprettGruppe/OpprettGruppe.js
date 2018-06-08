import React, { Component } from 'react'
import { Input } from 'nav-frontend-skjema'
import Knapp from 'nav-frontend-knapper'
import Api from '~/service/Api'
import PropTypes from 'prop-types'
import './OpprettGruppe.less'

const initialState = {
	gruppe: {
		navn: '',
		team: '',
		hensikt: ''
	},
	error: null
}

export default class OpprettGruppe extends Component {
	static propTypes = {
		onSuccess: PropTypes.func.isRequired
	}

	state = {
		...initialState
	}

	createGroup = async e => {
		try {
			// TODO: Validations

			// TODO: Temp values for default values
			const gruppe = {
				...this.state.gruppe,
				personer: '0',
				eier: 'Andreas Ludvigsen',
				env: ''
			}

			const res = await Api.postGruppe(gruppe)

			// IF success
			if (res.data.id) return this.props.onSuccess()
		} catch (error) {
			this.setState({ error })
		}
	}

	onInputChange = e => {
		const { name, value } = e.target

		const gruppe = {
			...this.state.gruppe,
			[name]: value
		}

		this.setState({ gruppe })
	}

	render() {
		const { navn, team, hensikt } = this.state
		return (
			<div className="opprett-gruppe">
				<Input label="NAVN" name="navn" value={navn} onChange={this.onInputChange} />
				<Input label="TEAM" name="team" value={team} onChange={this.onInputChange} />
				<Input label="HENSIKT" name="hensikt" value={hensikt} onChange={this.onInputChange} />
				<Knapp type="hoved" onClick={this.createGroup}>
					OPPRETT
				</Knapp>
			</div>
		)
	}
}
