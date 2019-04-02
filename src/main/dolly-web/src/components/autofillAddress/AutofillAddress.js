import React, { Component, Fragment } from 'react'
import Knapp from 'nav-frontend-knapper'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import { TpsfApi, DollyApi } from '~/service/Api'
import './AutofillAddress.less'
import InputSelector from '~/components/fields/InputSelector'
import { Field } from 'formik'
import LinkButton from '~/components/button/LinkButton/LinkButton'

const initialState = {
	isFetching: false,
	adresseValgt: false,
	gyldigeAdresser: [],
	husnummerOptions: []
}

export default class AutofillAddress extends Component {
	state = { ...initialState }

	placeHolderGenerator = type => {
		if (type === 'boadresse_postnr') return 'Velg postnummer...'
		if (type === 'boadresse_kommunenr') return 'Velg kommunenummer...'
		if (type === 'boadresse_gateadresse') return 'Søk etter adresse...'
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
		return options
	}

	fetchHusnr = () => {
		const husnr = this.state.husnummerOptions
		var options = []
		husnr.forEach(nr => {
			options.push({
				label: nr,
				value: nr
			})
		})
		return options
	}

	setQueryString = () => {
		let queryString = ''
		if (this.props.formikProps.values.boadresse_gateadresse) {
			queryString += `&adresseNavnsok=${this.props.formikProps.values.boadresse_gateadresse}`
		}
		if (this.props.formikProps.values.boadresse_postnr) {
			queryString += `&postNrsok=${this.props.formikProps.values.boadresse_postnr}`
		}
		if (this.props.formikProps.values.boadresse_kommunenr) {
			queryString += `&kommuneNrsok=${this.props.formikProps.values.boadresse_kommunenr}`
		}
		return queryString
	}

	onAdresseChangeHandler = input => {
		const { formikProps } = this.props
		let addressData = ''

		if (input) addressData = input.adrObject

		let newAddressObject = {
			boadresse_gateadresse: '',
			boadresse_husnummer: '',
			boadresse_gatekode: '',
			boadresse_kommunenr: '',
			boadresse_postnr: ''
		}

		if (addressData) {
			let { adrnavn, husnrfra, husnrtil, gkode, pnr, knr } = addressData
			let husnr = []
			for (var i = husnrfra; i <= husnrtil; i++) {
				husnr.push(parseInt(i))
			}
			this.setState({ husnummerOptions: husnr })

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
		}
		formikProps.setValues({ ...formikProps.values, ...newAddressObject, boadresse_husnummer: '' })
	}

	onHusnrChangeHandler = input => {
		const { formikProps } = this.props
		let husnrInput = ''
		if (input) husnrInput = input.value
		formikProps.setValues({ ...formikProps.values, boadresse_husnummer: husnrInput })
	}

	onClickGyldigAdresse = () => {
		return this.setState({ isFetching: true }, async () => {
			try {
				const query = this.setQueryString()
				let generateAddressResponse

				query.length > 0
					? (generateAddressResponse = await TpsfApi.generateAddress(query))
					: (generateAddressResponse = await TpsfApi.generateRandomAddress())

				const addressData = generateAddressResponse.data.response.data1.adrData
				this.setState({ gyldigeAdresser: addressData })

				let status = generateAddressResponse.data.response.status.utfyllendeMelding

				if (addressData === undefined) {
					return this.setState({
						isFetching: false,
						status
					})
				}
				return this.setState({
					isFetching: false,
					adresseValgt: true,
					status
				})
			} catch (err) {
				return this.setState({ isFetching: false })
			}
		})
	}

	onClickEndreAdresse = () => {
		const { formikProps } = this.props
		formikProps.setValues({ ...formikProps.values, boadresse_husnummer: '' })
		this.setState({ adresseValgt: false })
	}

	clearAll = () => {
		const { formikProps } = this.props
		let newAddressObject = {
			boadresse_gateadresse: '',
			boadresse_husnummer: '',
			boadresse_gatekode: '',
			boadresse_kommunenr: '',
			boadresse_postnr: ''
		}
		formikProps.setValues({ ...formikProps.values, ...newAddressObject })
	}

	renderAdresseSelect = () => {
		const gyldigeAdresser = this.fetchAdresser()
		const husNummer = this.fetchHusnr()
		return (
			<div>
				{this.state.adresseValgt && (
					<div className="address-container">
						<Field
							className="gyldigadresse-select"
							name="boadresse_gateadresse"
							placeholder="Velg gyldig adresse..."
							label="Gyldig adresse"
							component={FormikDollySelect}
							beforeChange={this.onAdresseChangeHandler}
							options={gyldigeAdresser}
						/>
						{this.state.husnummerOptions && (
							<div>
								<Field
									className="husnummer-select"
									name="boadresse_husnummer"
									placeholder="Velg husnummer..."
									label="Husnummer"
									component={FormikDollySelect}
									beforeChange={this.onHusnrChangeHandler}
									options={husNummer}
								/>
							</div>
						)}
						<LinkButton text="Endre adresse" onClick={this.onClickEndreAdresse} />
					</div>
				)}
			</div>
		)
	}

	renderSelect = item => {
		const selectProps = {
			loadOptions: this.loadOptionsSelector(item.id),
			placeholder: this.placeHolderGenerator(item.id)
		}
		const InputComponent = InputSelector(item.inputType)

		if (item.id !== 'boadresse_husnummer') {
			if (item.inputType === 'text') {
				return (
					<Field
						key={item.id}
						name={item.id}
						label={item.label}
						component={InputComponent}
						placeholder={selectProps.placeholder}
					/>
				)
			}
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
	}

	render() {
		const items = this.props.items
		return (
			<Fragment>
				<div className="address-wrapper">
					{!this.state.adresseValgt && (
						<div className="address-container">
							{items.map(
								item =>
									item.inputType && item.id !== 'boadresse_flyttedato' && this.renderSelect(item)
							)}
							<LinkButton text="Nullstill alle" onClick={this.clearAll} />
						</div>
					)}

					<div className="address-container">
						{!this.state.adresseValgt && (
							<Knapp
								className="generate-address-button"
								type="standard"
								autoDisableVedSpinner
								mini
								spinner={this.state.isFetching}
								onClick={this.onClickGyldigAdresse}
							>
								Hent gyldige adresser
							</Knapp>
						)}
						{this.state.gyldigeAdresser && this.renderAdresseSelect()}
						{this.state.gyldigeAdresser === undefined && <p>Fant ingen gyldige adresser</p>}
					</div>

					<div className="address-container">
						{items.map(
							item =>
								item.inputType && item.id === 'boadresse_flyttedato' && this.renderSelect(item)
						)}
					</div>
				</div>
			</Fragment>
		)
	}
}
