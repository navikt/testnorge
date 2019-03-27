import React, { Component, Fragment } from 'react'
import Knapp from 'nav-frontend-knapper'
import DollySelect from '~/components/fields/Select/Select'
import Input from '~/components/fields/Input/Input'
import { TpsfApi, DollyApi } from '~/service/Api'
import './AutofillAddress.less'
import InputSelector from '~/components/fields/InputSelector'
import { Field } from 'formik'

const initialState = {
	isFetching: false,
	boadresse_gateadresse: '',
	boadresse_husnummer: '',
	boadresse_postnr: '',
	boadresse_kommunenr: '',
	gyldigeAdresser: [],
	gyldigeAdresserKey: 0
}

export default class AutofillAddress extends Component {
	state = { ...initialState }

	// onInputChangeHandler = input => {
	// 	this.setState({ ...this.state, [input.id]: input.value })
	// }

	// onGatenavnChangeHandler = input => {
	// 	this.setState({ boadresse_gateadresse: input.target.value })
	// }

	// onHusnummerChangeHandler = input => {
	// 	this.setState({ boadresse_husnummer: input.target.value })
	// }

	placeHolderGenerator = type => {
		// console.log('type :', type)
		if (type === 'boadresse_postnr') return 'Velg postnummer...'
		if (type === 'boadresse_kommunenr') return 'Velg kommunenummer...'
		if (type === 'boadresse_gateadresse') return 'Søk etter adresse...'
		if (type === 'boadresse_husnummer') return 'Velg husnummer...'
		return ''
	}

	loadOptionsSelector = type => {
		if (type === 'boadresse_postnr') return () => this.fetchKodeverk('Postnummer', type)
		if (type === 'boadresse_kommunenr') return () => this.fetchKodeverk('Kommuner', type)
		return undefined
	}

	fetchKodeverk = (kodeverkNavn, type) => {
		return DollyApi.getKodeverkByNavn(kodeverkNavn).then(res => {
			return {
				options: res.data.koder.map(kode => ({
					label: `${kode.value} - ${kode.label}`,
					value: kode.value,
					id: type
				}))
			}
		})
	}

	fetchAdresser = () => {
		const adr = this.state.gyldigeAdresser
		var options = []
		if (adr.length > 1) {
			adr.forEach(adresse => {
				options.push({
					label: adresse.adrnavn + ', ' + adresse.pnr + ' ' + adresse.psted,
					value: adresse.adrnavn,
					adrObject: adresse
				})
			})
		} else {
			options.push({
				label: adr.adrnavn + ', ' + adr.pnr + ' ' + adr.psted,
				value: adr.adrnavn,
				adrObject: adr
			})
		}
		return Promise.resolve({ options: options })
	}

	onAdresseChangeHandler = input => {
		const { formikProps } = this.props
		const addressData = input.adrObject

		let newAddressObject = {
			boadresse_gateadresse: '',
			boadresse_husnummer: '',
			boadresse_gatekode: '',
			boadresse_kommunenr: '',
			boadresse_postnr: ''
		}
		if (addressData) {
			let { adrnavn, husnrfra, gkode, pnr, knr } = addressData

			if (this.props.formikProps.values.boadresse_husnummer) {
				husnrfra = this.props.formikProps.values.boadresse_husnummer
			}

			newAddressObject = {
				boadresse_gateadresse: adrnavn.toString(),
				boadresse_husnummer: husnrfra.toString(),
				boadresse_gatekode: gkode.toString(),
				boadresse_kommunenr: knr.toString(),
				boadresse_postnr: pnr.toString()
			}
			console.log('newAddressObject :', newAddressObject)
		}

		formikProps.setValues({ ...formikProps.values, ...newAddressObject })
	}

	setQueryString = () => {
		let queryString = ''
		if (this.props.formikProps.values.boadresse_gateadresse) {
			queryString += `&adresseNavnsok=${this.props.formikProps.values.boadresse_gateadresse}`
		}
		if (this.props.formikProps.values.boadresse_husnummer) {
			queryString += `&husNrsok=${this.props.formikProps.values.boadresse_husnummer}`
		}
		if (this.props.formikProps.values.boadresse_postnr) {
			queryString += `&postNrsok=${this.props.formikProps.values.boadresse_postnr}`
		}
		if (this.props.formikProps.values.boadresse_kommunenr) {
			queryString += `&kommuneNrsok=${this.props.formikProps.values.boadresse_kommunenr}`
		}
		return queryString
	}

	onClickHandler = () => {
		return this.setState({ isFetching: true }, async () => {
			try {
				const query = this.setQueryString()
				let generateAddressResponse

				query.length > 0
					? (generateAddressResponse = await TpsfApi.generateAddress(query))
					: (generateAddressResponse = await TpsfApi.generateRandomAddress())

				const addressData = generateAddressResponse.data.response.data1.adrData
				console.log('addressData :', addressData)
				this.setState({ gyldigeAdresser: addressData })

				const count = parseInt(generateAddressResponse.data.response.data1.antallForekomster)

				let status = generateAddressResponse.data.response.status.utfyllendeMelding

				return this.setState({
					isFetching: false,
					gyldigeAdresserKey: this.state.gyldigeAdresserKey + 1,
					status
				})
			} catch (err) {
				return this.setState({ isFetching: false })
			}
		})
	}

	renderAdresseSelect = () => {
		return (
			<DollySelect
				key={this.state.gyldigeAdresserKey}
				placeholder="Velg gyldig adresse..."
				name="generator-select"
				label=""
				onChange={this.onAdresseChangeHandler}
				loadOptions={this.fetchAdresser}
			/>
		)
	}

	renderSelect = item => {
		// const input = this.state
		const selectProps = {
			loadOptions: this.loadOptionsSelector(item.id),
			placeholder: this.placeHolderGenerator(item.id)
		}
		const InputComponent = InputSelector(item.inputType)

		// if (item.id === 'boadresse_gateadresse' || item.id === 'boadresse_husnummer') {
		// 	return (
		// 		<Field
		// 			key={item.id}
		// 			name={item.id}
		// 			label={item.label}
		// 			component={InputComponent}
		// 			placeholder={selectProps.placeholder}
		// 		/>
		// 	)
		// }

		return (
			<Field
				key={item.id}
				name={item.id}
				label={item.label}
				component={InputComponent}
				{...selectProps}
			/>
		)
	}

	render() {
		const items = this.props.items
		console.log('this :', this.props.formikProps.values)

		return (
			<Fragment>
				<div className="address-container">
					{items.map(item => item.inputType && this.renderSelect(item))}
				</div>
				<div className="get-address-container">
					<Knapp
						className="generate-address"
						type="standard"
						autoDisableVedSpinner
						mini
						spinner={this.state.isFetching}
						onClick={this.onClickHandler}
					>
						Hent gyldige adresser
					</Knapp>
					{this.state.gyldigeAdresser &&
						this.state.gyldigeAdresserKey > 0 &&
						this.renderAdresseSelect()}
					{this.state.gyldigeAdresser === undefined && <p>Fant ingen gyldige adresser</p>}
				</div>
			</Fragment>
		)
	}
}

// ------------------------------------------------------------------------------------

// import React, { Component, Fragment } from 'react'
// import Knapp from 'nav-frontend-knapper'
// import { Radio } from 'nav-frontend-skjema'
// import Lukknapp from 'nav-frontend-lukknapp'
// import Modal from 'react-modal'
// import DollySelect from '~/components/fields/Select/Select'
// import { TpsfApi, DollyApi } from '~/service/Api'

// import './AutofillAddress.less'

// const customStyles = {
// 	content: {
// 		top: '50%',
// 		left: '50%',
// 		right: 'auto',
// 		bottom: 'auto',
// 		marginRight: '-50%',
// 		transform: 'translate(-50%, -50%)',
// 		width: '25%',
// 		minWidth: '500px',
// 		overflow: 'inherit'
// 	}
// }

// Modal.setAppElement('#root')

// const initialState = {
// 	isFetching: false,
// 	type: 'random',
// 	input: '',
// 	status: ''
// }

// export default class AutofillAddress extends Component {
// 	state = { modalOpen: false, ...initialState }

// 	open = () => {
// 		this.setState({ modalOpen: true, ...initialState })
// 	}

// 	close = () => {
// 		this.setState({ modalOpen: false })
// 	}

// 	chooseType = type => {
// 		this.setState({ type, input: '' })
// 	}

// 	onInputChangeHandler = value => {
// 		this.setState({ input: value })
// 	}

// 	placeHolderGenerator = () => {
// 		const { type } = this.state
// 		if (type === 'pnr') return 'Velg postnummer...'
// 		if (type === 'knr') return 'Velg kommunenummer...'
// 		return ''
// 	}

// 	loadOptionsSelector = () => {
// 		const { type } = this.state
// 		if (type === 'pnr') return () => this.fetchKodeverk('Postnummer')
// 		if (type === 'knr') return () => this.fetchKodeverk('Kommuner')
// 		return undefined
// 	}

// 	fetchKodeverk = kodeverkNavn => {
// 		return DollyApi.getKodeverkByNavn(kodeverkNavn).then(res => {
// 			return {
// 				options: res.data.koder.map(kode => ({
// 					label: `${kode.value} - ${kode.label}`,
// 					value: kode.value
// 				}))
// 			}
// 		})
// 	}

// 	setQueryString = () => {
// 		const { input, type } = this.state
// 		const value = input.value
// 		switch (type) {
// 			case 'pnr':
// 				return `&postNr=${value}`
// 			case 'knr':
// 				return `&kommuneNr=${value}`
// 			default:
// 				return ''
// 		}
// 	}

// 	onClickHandler = () => {
// 		const { formikProps } = this.props

// 		return this.setState({ isFetching: true }, async () => {
// 			try {
// 				const generateAddressResponse = await TpsfApi.generateAddress(this.setQueryString())
// 				const addressData = generateAddressResponse.data.response.data1.adrData
// 				const count = parseInt(generateAddressResponse.data.response.data1.antallForekomster)

// 				let status = generateAddressResponse.data.response.status.utfyllendeMelding
// 				let newAddressObject = {
// 					boadresse_gateadresse: '',
// 					boadresse_husnummer: '',
// 					boadresse_kommunenr: '',
// 					boadresse_postnr: ''
// 				}

// 				if (count > 0) {
// 					const { adrnavn, husnrfra, pnr, knr } = addressData

// 					newAddressObject = {
// 						boadresse_gateadresse: adrnavn.toString(),
// 						boadresse_husnummer: husnrfra.toString(),
// 						boadresse_kommunenr: knr.toString(),
// 						boadresse_postnr: pnr.toString()
// 					}
// 					status = ''
// 				}

// 				formikProps.setValues({ ...formikProps.values, ...newAddressObject })

// 				return this.setState({ isFetching: false, modalOpen: false, status })
// 			} catch (err) {
// 				return this.setState({ isFetching: false, modalOpen: false })
// 			}
// 		})
// 	}

// 	renderSelect = () => {
// 		const { type, input } = this.state

// 		const selectProps = {
// 			loadOptions: this.loadOptionsSelector(),
// 			placeholder: this.placeHolderGenerator(),
// 			disabled: type === 'random'
// 		}

// 		return (
// 			<DollySelect
// 				key={type}
// 				name="generator-select"
// 				label="Velg verdi"
// 				onChange={this.onInputChangeHandler}
// 				value={input}
// 				{...selectProps}
// 			/>
// 		)
// 	}

// 	render() {
// 		const { type, status } = this.state

// 		return (
// 			<Fragment>
// 				<div className="generate-address-create">
// 					<Knapp type="standard" mini onClick={this.open}>
// 						Generer gyldig adresse
// 					</Knapp>
// 					{status && <span className="generate-address-create_status">{status}</span>}
// 				</div>

// 				<Modal
// 					style={customStyles}
// 					isOpen={this.state.modalOpen}
// 					onRequestClose={this.close}
// 					shouldCloseOnEsc
// 				>
// 					<div className="generate-address-container">
// 						<form className="generate-address-form">
// 							{this._renderRadioBtn(true, type, 'random', 'Tilfeldig')}
// 							{this._renderRadioBtn(true, type, 'pnr', 'Postnummer')}
// 							{this._renderRadioBtn(true, type, 'knr', 'Kommunenummer')}
// 						</form>
// 						{this.renderSelect()}
// 						<Knapp
// 							className="generate-address"
// 							type="standard"
// 							autoDisableVedSpinner
// 							mini
// 							spinner={this.state.isFetching}
// 							onClick={this.onClickHandler}
// 						>
// 							Generer
// 						</Knapp>
// 						<Lukknapp onClick={this.close} />
// 					</div>
// 				</Modal>
// 			</Fragment>
// 		)
// 	}

// 	_renderRadioBtn = (autoFocus, type, checkedType, label) => {
// 		return (
// 			<Radio
// 				autoFocus={autoFocus ? autoFocus : false}
// 				checked={type === checkedType}
// 				label={label}
// 				name={label}
// 				onChange={() => this.chooseType(checkedType)}
// 			/>
// 		)
// 	}
// }
