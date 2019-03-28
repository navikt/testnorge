import React, { Component, Fragment } from 'react'
import Knapp from 'nav-frontend-knapper'
import { Radio } from 'nav-frontend-skjema'
import Lukknapp from 'nav-frontend-lukknapp'
import Modal from 'react-modal'
import DollySelect from '~/components/fields/Select/Select'
import { TpsfApi, DollyApi } from '~/service/Api'

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
		minWidth: '500px',
		overflow: 'inherit'
	},
	overlay: {
		background: 'rgba(0,0,0,0.75)',
	}
}

Modal.setAppElement('#root')

const initialState = {
	isFetching: false,
	type: 'random',
	input: '',
	status: ''
}

export default class AutofillAddress extends Component {
	state = { modalOpen: false, ...initialState }

	open = () => {
		this.setState({ modalOpen: true, ...initialState })
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

	loadOptionsSelector = () => {
		const { type } = this.state
		if (type === 'pnr') return () => this.fetchKodeverk('Postnummer')
		if (type === 'knr') return () => this.fetchKodeverk('Kommuner')
		return undefined
	}

	fetchKodeverk = kodeverkNavn => {
		return DollyApi.getKodeverkByNavn(kodeverkNavn).then(res => {
			return {
				options: res.data.koder.map(kode => ({
					label: `${kode.value} - ${kode.label}`,
					value: kode.value
				}))
			}
		})
	}

	setQueryString = () => {
		const { input, type } = this.state
		const value = input.value
		switch (type) {
			case 'pnr':
				return `&postNr=${value}`
			case 'knr':
				return `&kommuneNr=${value}`
			default:
				return ''
		}
	}

	onClickHandler = () => {
		const { formikProps } = this.props

		return this.setState({ isFetching: true }, async () => {
			try {
				const generateAddressResponse = await TpsfApi.generateAddress(this.setQueryString())
				const addressData = generateAddressResponse.data.response.data1.adrData
				const count = parseInt(generateAddressResponse.data.response.data1.antallForekomster)

				let status = generateAddressResponse.data.response.status.utfyllendeMelding
				let newAddressObject = {
					boadresse_gateadresse: '',
					boadresse_husnummer: '',
					boadresse_kommunenr: '',
					boadresse_postnr: ''
				}

				if (count > 0) {
					const { adrnavn, husnrfra, pnr, knr } = addressData

					newAddressObject = {
						boadresse_gateadresse: adrnavn.toString(),
						boadresse_husnummer: husnrfra.toString(),
						boadresse_kommunenr: knr.toString(),
						boadresse_postnr: pnr.toString()
					}
					status = ''
				}

				formikProps.setValues({ ...formikProps.values, ...newAddressObject })

				return this.setState({ isFetching: false, modalOpen: false, status })
			} catch (err) {
				return this.setState({ isFetching: false, modalOpen: false })
			}
		})
	}

	renderSelect = () => {
		const { type, input } = this.state

		const selectProps = {
			loadOptions: this.loadOptionsSelector(),
			placeholder: this.placeHolderGenerator(),
			disabled: type === 'random'
		}

		return (
			<DollySelect
				key={type}
				name="generator-select"
				label="Velg verdi"
				onChange={this.onInputChangeHandler}
				value={input}
				{...selectProps}
			/>
		)
	}

	render() {
		const { type, status } = this.state

		return (
			<Fragment>
				<div className="generate-address-create">
					<Knapp type="standard" mini onClick={this.open}>
						Generer gyldig adresse
					</Knapp>
					{status && <span className="generate-address-create_status">{status}</span>}
				</div>

				<Modal
					style={customStyles}
					isOpen={this.state.modalOpen}
					onRequestClose={this.close}
					shouldCloseOnEsc
				>
					<div className="generate-address-container">
						<form className="generate-address-form">
							{this._renderRadioBtn(true, type, 'random', 'Tilfeldig')}
							{this._renderRadioBtn(true, type, 'pnr', 'Postnummer')}
							{this._renderRadioBtn(true, type, 'knr', 'Kommunenummer')}
						</form>
						{this.renderSelect()}
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
						<Lukknapp onClick={this.close} />
					</div>
				</Modal>
			</Fragment>
		)
	}

	_renderRadioBtn = (autoFocus, type, checkedType, label) => {
		return (
			<Radio
				autoFocus={autoFocus ? autoFocus : false}
				checked={type === checkedType}
				label={label}
				name={label}
				onChange={() => this.chooseType(checkedType)}
			/>
		)
	}
}
