import React, { PureComponent, Fragment } from 'react'
import { Formik, Form, Field } from 'formik'
import { FormikInput } from '~/components/fields/Input/Input'
import ContentContainer from '~/components/contentContainer/ContentContainer'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import { FormikDatepicker } from '~/components/fields/Datepicker/Datepicker'
import { TpsfApi } from '~/service/Api'
import Knapp from 'nav-frontend-knapper'
import * as yup from 'yup'
import Loading from '~/components/loading/Loading'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import DisplayFormikState from '~/utils/DisplayFormikState'
import DataFormatter from '~/utils/DataFormatter'
import DateValidation from '~/components/fields/Datepicker/DateValidation'

export default class SendFoedselsmelding extends PureComponent {
	state = {
		isFetching: false,
		isFetchingMiljoer: false,
		nyttBarn: null,
		errorMessage: null,
		foundIdentMor: null,
		showErrorMessageFoundIdent: false,
		currentFnrMor: '',
		environments: [],
		miljoer: [],
		response_success: []
	}

	validation = () =>
		yup.object().shape({
			identMor: yup
				.string()
				.min(11, 'Mors ident må inneholde 11 sifre.')
				.max(11, 'Mors ident må inneholde 11 sifre.')
				.required('Mors ident er et påkrevd felt.'),
			identFar: yup
				.string()
				.min(11, 'Ident må inneholde 11 sifre.')
				.max(11, 'Ident må inneholde 11 sifre.'),
			identtype: yup.string().required('Identtype er ett påkrevd felt.'),
			kjonn: yup.string().required('Kjønn er et påkrevd felt.'),
			miljoer: yup.string().required('Miljø er et påkrevd felt.'),
			foedselsdato: DateValidation(),
			adresseFra: yup.string().required('Adresse er et påkrevd felt.')
		})

	createRequestObjects(values) {
		const miljoer = values.miljoer.map(env => {
			return env.value
		})
		return { ...values, miljoer: miljoer }
	}

	_onSubmit = (values, { resetForm }) => {
		const request = this.createRequestObjects(values)
		var success_envs = []

		this.setState(
			{
				isFetching: true,
				nyttBarn: null,
				errorMessage: null,
				foundIdentMor: false,
				miljoer: values.miljoer,
				response_success: []
			},
			async () => {
				try {
					const createFoedselsmeldingRes = await TpsfApi.createFoedselsmelding({
						...request,
						foedselsdato: DataFormatter.parseDate(values.foedselsdato)
					})
					const getKontaktInformasjonRes = await TpsfApi.getKontaktInformasjon(
						createFoedselsmeldingRes.data.personId,
						request.miljoer[0]
					)
					const status = createFoedselsmeldingRes.data.status
					Object.keys(status).map(key => {
						if (status[key] === 'OK') success_envs = [...success_envs, key]
					})
					resetForm()
					return this.setState({
						nyttBarn: getKontaktInformasjonRes.data.person,
						isFetching: false,
						currentFnrMor: null,
						miljoer: [],
						response_success: success_envs
					})
				} catch (err) {
					resetForm()
					this.setState({
						currentFnrMor: null,
						isFetching: false,
						miljoer: [],
						errorMessage: err.response.data.message
					})
				}
			}
		)
	}

	_renderNyttBarn = person => {
		var suksessMiljoer = ''
		if (this.state.response_success.length > 0)
			suksessMiljoer = this.state.response_success.join(', ')

		return (
			<Fragment>
				<h3 className="tps-endring-success-message">
					Gratulerer, {person.personNavn.gjeldendePersonnavn} med ident {person.fodselsnummer} ble
					født i miljø {suksessMiljoer}!
				</h3>
			</Fragment>
		)
	}

	fillEnvironmentDropdown(environments) {
		return environments.map(env => ({ value: env, label: env.toUpperCase() }))
	}

	_handleOnBlurInput = e => {
		let fnr = e.target.value.replace(/\s+/g, '')

		if (fnr.length === 11 && !isNaN(fnr)) {
			this.setState(
				{
					isFetchingMiljoer: true,
					nyttBarn: null,
					miljoer: [],
					environments: [],
					showErrorMessageFoundIdent: false,
					foundIdentMor: false,
					errorMessage: null
				},
				async () => {
					try {
						const getMiljoerByFnrRes = await TpsfApi.getMiljoerByFnr(fnr)
						const res_environments = getMiljoerByFnrRes.data.statusPaaIdenter[0].env

						let miljoer = []

						if (res_environments.length < 1) {
							return this.setState({
								miljoer: miljoer,
								currentFnrMor: fnr,
								foundIdentMor: false,
								isFetchingMiljoer: false,
								showErrorMessageFoundIdent: true
							})
						} else if (res_environments.length === 1) {
							miljoer.push({ value: res_environments[0], label: res_environments[0] })
						}

						const displayEnvironmentsInDropdown = this.fillEnvironmentDropdown(res_environments)
						return this.setState({
							environments: displayEnvironmentsInDropdown,
							currentFnrMor: fnr,
							foundIdentMor: true,
							isFetchingMiljoer: false,
							miljoer: miljoer
						})
					} catch (err) {
						this.setState({ isFetchingMiljoer: false, currentFnrMor: fnr })
					}
				}
			)
		}
	}

	render() {
		const { environments, foundIdentMor, miljoer, currentFnrMor } = this.state

		let initialValues = {
			identMor: currentFnrMor,
			identFar: '',
			identtype: 'FNR',
			foedselsdato: '',
			kjonn: '',
			miljoer: miljoer.slice(),
			adresseFra: ''
		}

		const adresseOptions = [
			{ value: 'LAGNY', label: 'LAG NY ADRESSE' },
			{ value: 'MOR', label: 'ARV FRA MORS' },
			{ value: 'FAR', label: 'ARV FRA FARS' }
		]

		return (
			<ContentContainer className="tps-endring-content-container">
				<Formik
					onSubmit={this._onSubmit}
					onReset={this.initialValues}
					validationSchema={this.validation}
					initialValues={initialValues}
					enableReinitialize
					render={props => {
						const { values, touched, errors, dirty, isSubmitting } = props
						return (
							<Form autoComplete="off">
								<h2>Send fødselsmelding</h2>
								<div className="tps-endring-foedselmelding-top">
									<Field
										name="identMor"
										label="MORS IDENT"
										component={FormikInput}
										onBlur={this._handleOnBlurInput}
									/>
									<Field
										name="identFar"
										label="FARS IDENT"
										component={FormikInput}
										disabled={foundIdentMor ? false : true}
									/>
									<Field
										name="identtype"
										label="BARNETS IDENTTYPE"
										component={FormikDollySelect}
										options={SelectOptionsManager('identtype')}
										disabled={foundIdentMor ? false : true}
									/>
									<Field
										name="foedselsdato"
										label="BARNETS FØDSELSDATO"
										component={FormikDatepicker}
										disabled={foundIdentMor ? false : true}
									/>
									<Field
										name="kjonn"
										label="BARNETS KJØNN"
										component={FormikDollySelect}
										options={SelectOptionsManager('kjonnBarn')}
										disabled={foundIdentMor ? false : true}
									/>
									<Field
										name="miljoer"
										label="SEND TIL MILJØ"
										options={environments}
										component={FormikDollySelect}
										multi={true}
										disabled={foundIdentMor ? false : true}
									/>
								</div>
								<div className="tps-endring-foedselmelding-bottom">
									<Field
										name="adresseFra"
										label="ADRESSE"
										component={FormikDollySelect}
										options={adresseOptions}
										disabled={foundIdentMor ? false : true}
									/>
									<Knapp type="hoved" htmlType="submit" disabled={foundIdentMor ? false : true}>
										Opprett fødselsmelding
									</Knapp>
								</div>
								{/* <DisplayFormikState {...props} /> */}
							</Form>
						)
					}}
				/>
				{this.state.isFetching && <Loading label="Sender fødselsmelding" />}
				{this.state.isFetchingMiljoer && <Loading label="Søker etter testbruker" />}
				{this.state.showErrorMessageFoundIdent && (
					<h3 className="tps-endring-error-message">
						Finner ikke testperson med ident: {this.state.currentFnrMor}
					</h3>
				)}
				{this.state.errorMessage && (
					<h4 className="tps-endring-error-message"> Feil: {this.state.errorMessage} </h4>
				)}
				{this.state.nyttBarn && this._renderNyttBarn(this.state.nyttBarn)}
			</ContentContainer>
		)
	}
}
