import * as yup from 'yup'
import Loading from '~/components/loading/Loading'
import { TpsfApi } from '~/service/Api'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import { FormikDatepicker } from '~/components/fields/Datepicker/Datepicker'
import React, { PureComponent } from 'react'
import { Formik, Form, Field } from 'formik'
import { FormikInput } from '~/components/fields/Input/Input'
import ContentContainer from '~/components/contentContainer/ContentContainer'
import DateValidation from '~/components/fields/Datepicker/DateValidation'
import Knapp from 'nav-frontend-knapper'
import Formatters from '~/utils/DataFormatter'

export default class SendDoedsmelding extends PureComponent {
	state = {
		isFetching: false,
		isFetchingMiljoer: false,
		errorMessage: null,
		meldingSent: false,
		handlingsType: null,
		foundIdent: null,
		showErrorMessageFoundIdent: false,
		currentfnr: '',
		environments: [],
		miljoer: [],
		environments_success: [],
		environments_error: []
	}

	validation = () =>
		yup.object().shape({
			ident: yup
				.string()
				.min(11, 'Ident må inneholde 11 sifre.')
				.max(11, 'Ident må inneholde 11 sifre.')
				.required('Ident er et påkrevd felt.'),
			handling: yup.string().required('Handling er et påkrevd felt.'),
			miljoer: yup.string().required('Miljø er et påkrevd felt.'),
			doedsdato: DateValidation()
		})

	createRequestObjects(values_obj) {
		const miljoer = values_obj.miljoer.map(env => {
			return env.value
		})

		return { ...values_obj, miljoer: miljoer }
	}

	_onSubmit = (values, { resetForm }) => {
		const request = this.createRequestObjects(values)
		var success_envs = []
		var error_envs = []

		this.setState(
			{
				isFetching: true,
				meldingSent: false,
				errorMessage: null,
				handlingsType: values.handling,
				foundIdent: false
			},
			async () => {
				try {
					let response = await TpsfApi.createDoedsmelding({
						...request,
						doedsdato: Formatters.parseDate(values.doedsdato)
					})
					const status = response.data.status

					Object.keys(status).map(key => {
						if (status[key] === 'OK') success_envs = [...success_envs, key]
						else error_envs = [...error_envs, key]
					})

					this.setState({
						isFetching: false,
						meldingSent: true,
						currentfnr: null,
						miljoer: [],
						environments_success: success_envs,
						environments_error: error_envs
					})
					resetForm()
				} catch (err) {
					this.setState({
						meldingSent: false,
						currentfnr: null,
						miljoer: [],
						errorMessage: err.response.data.message,
						isFetching: false
					})
					resetForm()
				}
			}
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
					environments: [],
					showErrorMessageFoundIdent: false,
					errorMessage: null,
					miljoer: [],
					meldingSent: false,
					foundIdent: false
				},
				async () => {
					try {
						const getMiljoerByFnrRes = await TpsfApi.getMiljoerByFnr(fnr)
						const res_environments = getMiljoerByFnrRes.data.statusPaaIdenter[0].env

						let miljoer = []

						if (res_environments.length < 1) {
							return this.setState({
								currentfnr: fnr,
								foundIdent: false,
								isFetchingMiljoer: false,
								miljoer: miljoer,
								showErrorMessageFoundIdent: true
							})
						} else if (res_environments.length === 1) {
							miljoer.push({ value: res_environments[0], label: res_environments[0] })
						}

						const displayEnvironmentsInDropdown = this.fillEnvironmentDropdown(res_environments)
						return this.setState({
							environments: displayEnvironmentsInDropdown,
							currentfnr: fnr,
							miljoer: miljoer,
							foundIdent: true,
							isFetchingMiljoer: false
						})
					} catch (err) {
						this.setState({ isFetchingMiljoer: false, currentfnr: fnr })
					}
				}
			)
		}
	}

	_renderMeldingSent = () => {
		var handling = ''
		switch (this.state.handlingsType) {
			case 'C':
				handling = 'sent'
				break
			case 'U':
				handling = 'endret'
				break
			case 'D':
				handling = 'annullert'
				break
		}
		return <h3 className="tps-endring-success-message"> Dødsmelding {handling} </h3>
	}

	_renderResponseMessage = () => {
		var handling = ''
		var suksessMiljoer = ''
		var feilMiljoer = ''

		if (this.state.environments_success.length > 0)
			suksessMiljoer = this.state.environments_success.join(', ')

		if (this.state.environments_error.length > 0)
			feilMiljoer = this.state.environments_error.join(', ')

		switch (this.state.handlingsType) {
			case 'C':
				handling = 'sent'
				break
			case 'U':
				handling = 'endret'
				break
			case 'D':
				handling = 'annullert'
				break
		}
		return (
			<div>
				{this.state.environments_success.length > 0 && (
					<h3 className="tps-endring-success-message">
						Dødsmelding {handling} i {suksessMiljoer}
					</h3>
				)}

				{this.state.environments_error.length > 0 && (
					<h3 className="tps-endring-tps-endring-error-message">
						Personen var allerede død i {feilMiljoer}
					</h3>
				)}
			</div>
		)
	}

	render() {
		const { foundIdent, environments, currentfnr, miljoer, handlingsType } = this.state

		let initialValues = {
			ident: currentfnr,
			handling: 'C',
			doedsdato: '',
			miljoer: miljoer.slice()
		}

		const handlingOptions = [
			{ value: 'C', label: 'Sette dødsdato' },
			{ value: 'U', label: 'Endre dødsdato' },
			{ value: 'D', label: 'Annullere dødsdato' }
		]

		return (
			<ContentContainer>
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
								<h2>Send dødsmelding</h2>
								<div className="tps-endring-doedsmelding">
									<Field
										name="ident"
										label="IDENT"
										component={FormikInput}
										onBlur={this._handleOnBlurInput}
									/>
									<Field
										name="handling"
										label="HANDLING"
										options={handlingOptions}
										component={FormikDollySelect}
										disabled={foundIdent ? false : true}
									/>
									<Field
										name="doedsdato"
										label="DØDSDATO"
										component={FormikDatepicker}
										disabled={foundIdent ? false : true}
									/>

									<Field
										name="miljoer"
										label="SEND TIL MILJØ"
										options={environments}
										component={FormikDollySelect}
										multi={true}
										disabled={foundIdent ? false : true}
									/>
								</div>
								<div className="tps-endring-knapp-container">
									<Knapp type="hoved" htmlType="submit" disabled={foundIdent ? false : true}>
										Opprett dødsmelding
									</Knapp>
								</div>
							</Form>
						)
					}}
				/>
				{this.state.isFetchingMiljoer && <Loading label="Søker etter testbruker" />}
				{this.state.showErrorMessageFoundIdent && (
					<h3 className="tps-endring-tps-endring-error-message">
						Finner ikke testperson med ident: {this.state.currentfnr}
					</h3>
				)}
				{this.state.isFetching && (
					<Loading
						label={
							handlingsType === 'D'
								? 'Annulerer dødsmelding'
								: handlingsType === 'U'
									? 'Endrer dødsdato'
									: 'Sender dødsmelding'
						}
					/>
				)}
				{this.state.errorMessage && (
					<h4 className="tps-endring-error-message"> Feil: {this.state.errorMessage} </h4>
				)}
				{this.state.meldingSent && this._renderResponseMessage()}
			</ContentContainer>
		)
	}
}
