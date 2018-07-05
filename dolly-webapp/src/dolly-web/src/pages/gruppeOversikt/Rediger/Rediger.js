import React, { Component } from 'react'
import { Input, Select } from 'nav-frontend-skjema'
import Knapp from 'nav-frontend-knapper'
import PropTypes from 'prop-types'
import './Rediger.less'

const initialState = {
	gruppe: {
		navn: '',
		teamTilhoerlighetNavn: '',
		hensikt: ''
	},
	error: null
}

export default class Rediger extends Component {
	static propTypes = {
		gruppe: PropTypes.shape({
			navn: PropTypes.string,
			teamTilhoerlighetNavn: PropTypes.string,
			hensikt: PropTypes.string
		}),
		createGruppe: PropTypes.func,
		updateGruppe: PropTypes.func,
		cancelRedigerOgOpprett: PropTypes.func
	}

	constructor(props) {
		super(props)

		let _state = Object.assign({}, initialState)

		if (props.gruppe) _state.gruppe = props.gruppe

		this.state = {
			..._state
		}
	}

	createGroup = async e => {
		const { index, createGruppe, updateGruppe } = this.props
		// TODO: Validations

		// TODO: Temp values for default values
		const gruppeObj = {
			...this.state.gruppe,
			personer: '0',
			eier: 'Andreas Ludvigsen',
			env: ''
		}

		const res = gruppeObj.id ? await updateGruppe(gruppeObj) : await createGruppe(gruppeObj)
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
		const { navn, teamTilhoerlighetNavn, hensikt } = this.state

		//TODO: Finne faktiske teams som en bruker er medlem av. Kanskje dette bare skal fetches hver gang vi går inn i gruppeOversikt?
		const test = ['team', 'team1', 'team2']

		return (
			<div className="opprett-gruppe">
				<Input label="NAVN" name="navn" value={navn} onChange={this.onInputChange} />
				<Select
					label="TEAM"
					name="teamTilhoerlighetNavn"
					value={teamTilhoerlighetNavn}
					onChange={this.onInputChange}
				>
					{test.map((teamObj, idx) => (
						<option value={teamObj} key={idx}>
							{teamObj}
						</option>
					))}
				</Select>
				<Input label="HENSIKT" name="hensikt" value={hensikt} onChange={this.onInputChange} />
				<Knapp type="hoved" onClick={this.createGroup}>
					{this.props.redigering ? 'OPPDATER' : 'OPPRETT'}
				</Knapp>
				<Knapp type="standard" onClick={this.props.cancelRedigerOgOpprett}>
					Avbryt
				</Knapp>
			</div>
		)
	}
}
