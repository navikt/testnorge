import React, { Component, Fragment } from 'react'
import Knapp from 'nav-frontend-knapper'
import { Radio } from 'nav-frontend-skjema'
import Lukknapp from 'nav-frontend-lukknapp'
import Modal from 'react-modal'
import DollySelect from '~/components/fields/Select/Select'
import { TpsfApi } from '~/service/Api'

import './AutofillAddress.less'

const customStyles = {
	content: {
		top: '50%',
		left: '50%',
		right: 'auto',
		bottom: 'auto',
		marginRight: '-50%',
		transform: 'translate(-50%, -50%)',
		width: '25%',
		minWidth: '500px'
	}
}

Modal.setAppElement('#root')

export default class AutofillAddress extends Component {
	state = {
		modalOpen: true,
		isFetching: false,
		type: 'random',
		input: ''
	}

	open = () => {
		this.setState({ modalOpen: true })
	}

	close = () => {
		this.setState({ modalOpen: false })
	}

	chooseType = type => {
		this.setState({ type, input: '' })
	}

	onInputChangeHandler = value => {
		this.setState({ input: value })
	}

	placeHolderGenerator = () => {
		const { type } = this.state
		if (type === 'pnr') return 'Velg postnummer...'
		if (type === 'knr') return 'Velg kommunenummer...'
		return ''
	}

	onClickHandler = () => {
		const { formikProps } = this.props

		return this.setState({ isFetching: true }, async () => {
			try {
				const generateAddressResponse = await TpsfApi.generateAddress()
				const addressData = generateAddressResponse.data.response.data1.adrData
				const { adrnavn, husnrfra, pnr, knr } = addressData

				const newAddressObject = {
					boadresse_gateadresse: adrnavn,
					boadresse_husnummer: husnrfra,
					boadresse_kommunenr: knr,
					boadresse_postnr: pnr.toString()
				}

				formikProps.setValues({ ...formikProps.values, ...newAddressObject })

				return this.setState({ isFetching: false, modalOpen: false })
			} catch (err) {
				return this.setState({ isFetching: false, modalOpen: false })
			}
		})
	}

	render() {
		const { type } = this.state
		return (
			<Fragment>
				<Knapp type="standard" mini onClick={this.open}>
					Generer gyldig adresse
				</Knapp>

				<Modal style={customStyles} isOpen={this.state.modalOpen}>
					<div className="generate-address-container">
						<Lukknapp onClick={this.close} />
						<form className="generate-address-form">
							<Radio
								checked={type === 'random'}
								label="Tilfeldig"
								name="Tilfeldig"
								onChange={() => this.chooseType('random')}
							/>
							<Radio
								checked={type === 'pnr'}
								label="Postnummer"
								name="Postnummer"
								onChange={() => this.chooseType('pnr')}
							/>
							<Radio
								checked={type === 'knr'}
								label="Kommunenummer"
								name="Kommunenummer"
								onChange={() => this.chooseType('knr')}
							/>
						</form>
						<DollySelect
							name="generator-select"
							label="Velg verdi"
							placeholder={this.placeHolderGenerator()}
							onChange={this.onInputChangeHandler}
							disabled={type === 'random'}
						/>
						<Knapp
							className="generate-address"
							type="standard"
							autoDisableVedSpinner
							mini
							spinner={this.state.isFetching}
							onClick={this.onClickHandler}
						>
							Generer
						</Knapp>
					</div>
				</Modal>
			</Fragment>
		)
	}
}
