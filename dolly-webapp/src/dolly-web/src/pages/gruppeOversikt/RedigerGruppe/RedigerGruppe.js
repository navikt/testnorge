import React, { Component } from 'react'
import { Input, Select } from 'nav-frontend-skjema'
import Knapp from 'nav-frontend-knapper'
import Api from '~/service/Api'
import PropTypes from 'prop-types'
import './RedigerGruppe.less'

const initialState = {
	gruppe: {
		navn: '',
		teamTilhoerlighetNavn: '',
		hensikt: ''
	},
	error: null
}

export default class RedigerGruppe extends Component {
	static propTypes = {
		onSuccess: PropTypes.func.isRequired,
		onCancel: PropTypes.func.isRequired,
		gruppe: PropTypes.shape({
			navn: PropTypes.string,
			teamTilhoerlighetNavn: PropTypes.string,
			hensikt: PropTypes.string
		}),
		redigering: PropTypes.boolean
	}

	constructor(props) {
		super(props)

		let _state = Object.assign({}, initialState)

		if (props.gruppe) _state.gruppe = props.gruppe

		console.log(_state)

		this.state = {
			..._state
		}
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

			// TODO: Bruk endepunkt for redigering
			const res = this.props.redigering ? await Api.postGruppe(gruppe) :  await Api.postGruppe(gruppe)

			// IF success
			if (res.data.id) return this.props.onSuccess()
		} catch (error) {
			this.setState({ error })
		}
	}

	onInputChange = e => {
		const { name, value } = e.target

		console.log(name, value)

		const gruppe = {
			...this.state.gruppe,
			[name]: value
		}

		this.setState({ gruppe })
	}

	render() {
		const { navn, teamTilhoerlighetNavn, hensikt } = this.state

		//TODO: Finne faktiske teams som en bruker er medlem av. Kanskje dette bare skal fetches hver gang vi går inn i gruppeOversikt?
		const test = ['team', 'team1', 'team2']

		return (
			<div className="opprett-gruppe">
				<Input label="NAVN" name="navn" value={navn} onChange={this.onInputChange} />
				<Select label="TEAM" name="teamTilhoerlighetNavn" value={teamTilhoerlighetNavn} onChange={this.onInputChange}>
					{test.map((teamObj, idx) => 
						<option value={teamObj} key={idx}>{teamObj}</option>
					)}
				</Select>
				<Input label="HENSIKT" name="hensikt" value={hensikt} onChange={this.onInputChange} />
				<Knapp type="hoved" onClick={this.createGroup}>
					{this.props.redigering ? 'OPPDATER' : 'OPPRETT'}
				</Knapp>
				<Knapp type="standard" onClick={this.props.onCancel}>
					Avbryt
				</Knapp>
			</div>
		)
	}
}
