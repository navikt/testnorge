import React, { PureComponent } from 'react'
import { Formik, Form } from 'formik'
import * as yup from 'yup'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { TpsfApi } from '~/service/Api'
import Loading from '~/components/ui/loading/Loading'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { requiredDate } from '~/utils/YupValidations'
import Logger from '~/logger'

export default class SendDoedsmelding extends PureComponent {
	state = {
		isFetching: false,
		isFetchingMiljoer: false,
		errorMessage: '',
		meldingSent: false,
		handlingsType: '',
		foundIdent: '',
		showErrorMessageFoundIdent: false,
		currentfnr: '',
		environments: [],
		miljoer: [],
		environments_success: [],
		environments_error: [],
		errorFormatted: ''
	}

	validation = () =>
		yup.object().shape({
			ident: yup
				.string()
				.min(11, 'Ident må inneholde 11 sifre.')
				.max(11, 'Ident må inneholde 11 sifre.')
				.required('Ident er et påkrevd felt.'),
			handling: yup.string().required('Handling er et påkrevd felt.'),
			miljoer: yup.array(),
			doedsdato: requiredDate
		})

	_onSubmit = (values, { resetForm }) => {
		Logger.log({ event: 'Send doesmelding' })

		const body = values
		let success_envs = []
		let error_envs = []

		this.setState(
			{
				isFetching: true,
				meldingSent: false,
				errorMessage: '',
				handlingsType: values.handling,
				foundIdent: false
			},
			async () => {
				try {
					const response = await TpsfApi.createDoedsmelding(body)
					const status = response.data.status
					Object.keys(status).map(key => {
						if (status[key] === 'OK') success_envs = [...success_envs, key]
						else error_envs = [...error_envs, key]
					})
					this.setState({
						isFetching: false,
						meldingSent: true,
						currentfnr: '',
						miljoer: [],
						environments_success: success_envs,
						environments_error: error_envs,
						errorFormatted: status
					})
					resetForm()
				} catch (err) {
					this.setState({
						meldingSent: false,
						currentfnr: '',
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
		const fnr = e.target.value.replace(/\s+/g, '')

		if (fnr.length === 11 && !isNaN(fnr)) {
			this.setState(
				{
					isFetchingMiljoer: true,
					environments: [],
					showErrorMessageFoundIdent: false,
					errorMessage: '',
					miljoer: [],
					meldingSent: false,
					foundIdent: false
				},
				async () => {
					try {
						const getMiljoerByFnrRes = await TpsfApi.getMiljoerByFnr(fnr)
						const res_environments = getMiljoerByFnrRes.data.statusPaaIdenter[0].env

						const miljoer = []

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
		let handling = ''

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
		let handling = ''
		let suksessMiljoer = ''
		let feilMiljoer = ''

		const object = this.state.errorFormatted,
			errorMsgArray = Object.keys(object).reduce(function(r, k) {
				return r.concat(k, object[k])
			}, [])

		const errorMessageFormatted = errorMsgArray.filter((element, index) => {
			return index % 2 === 1
		})
		const str = errorMessageFormatted.toString()

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
					<h3 className="tps-endring-tps-endring-error-message">{str.replace(/.,/g, ' -- ')}</h3>
				)}
			</div>
		)
	}

	render() {
		const { foundIdent, environments, currentfnr, miljoer, handlingsType } = this.state

		const initialValues = {
			ident: currentfnr,
			handling: 'C',
			doedsdato: '',
			miljoer: []
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
				>
					{props => {
						const { values, touched, errors, dirty, isSubmitting } = props
						return (
							<Form autoComplete="off">
								<h2>Send dødsmelding</h2>
								<div className="tps-endring-doedsmelding">
									<FormikTextInput name="ident" label="IDENT" onBlur={this._handleOnBlurInput} />

									<FormikSelect
										name="handling"
										label="HANDLING"
										options={handlingOptions}
										isClearable={false}
										disabled={!foundIdent}
										fastfield={false}
									/>

									<FormikDatepicker name="doedsdato" label="DØDSDATO" disabled={!foundIdent} />

									<FormikSelect
										name="miljoer"
										label="SEND TIL MILJØ"
										options={environments}
										isMulti
										disabled={!foundIdent}
									/>
								</div>
								<div className="tps-endring-knapp-container">
									<NavButton type="hoved" htmlType="submit" disabled={!foundIdent}>
										Opprett dødsmelding
									</NavButton>
								</div>
							</Form>
						)
					}}
				</Formik>
				{this.state.isFetchingMiljoer && <Loading label="Søker etter person" />}
				{this.state.showErrorMessageFoundIdent && (
					<h3 className="tps-endring-tps-endring-error-message">
						Finner ikke person med ident: {this.state.currentfnr}
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
