import React, { Component } from 'react'
import Button from '~/components/button/Button'
import PropTypes from 'prop-types'
import Loading from '~/components/loading/Loading'
import Icon from '~/components/icon/Icon'
import { DollyApi } from '~/service/Api'

export default class GenererSyntVerdier extends Component {
	// Ikke i bruk. Mangler å få fields til å rerendere når de har fått nye verdier i formikProps.
	// Framprovoserer oppdatering for å se at verdier blir lagt inn ved å f.eks. legge til et institusjonsopphold til.
	constructor(props) {
		super(props)

		this.state = {
			isFetching: false
		}
	}

	render() {
		const { isFetching } = this.state

		return isFetching ? (
			<div className="static-value">
				<Loading />
			</div>
		) : (
			<div>
				<Button
					onClick={() => this._onClick()}
					kind="file-hand"
					className="flexbox--align-center field-group-add"
				>
					FYLL INN SYNTETISKE TESTVERDIER
				</Button>
			</div>
		)
	}

	_onClick = () => {
		const { antall, type } = this.props
		let path, res
		switch (type) {
			case 'institusjonsopphold':
				path = '/api/v1/generate/inst'
		}
		this.setState({ isFetching: true }, async () => {
			try {
				res = await DollyApi.getSyntData(path, antall)
				this._formaterData(type, res.data)
				return this.setState({ isFetching: false })
			} catch (err) {
				console.error('error: Fant ikke syntverdier')
				return this.setState({ isFetching: false })
			}
		})
	}

	_formaterData = (type, res) => {
		switch (type) {
			case 'institusjonsopphold':
				return res.map((values, idx) => {
					Object.assign(this.props.formikValues[idx], {
						institusjonstype: values.k_opphold_inst_t,
						varighet: values.k_varig_inst_t,
						startdato: values.dato_fom,
						faktiskSluttdato: values.dato_tom
					})
				})
		}
	}
}
