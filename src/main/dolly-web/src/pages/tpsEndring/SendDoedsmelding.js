import * as yup from 'yup'
import Loading from '~/components/loading/Loading'
import { TpsfApi } from '~/service/Api'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import { FormikDatepicker } from '~/components/fields/Datepicker/Datepicker'
import React, { PureComponent } from 'react'
import { Formik, Form, Field } from 'formik'
import { FormikInput } from '~/components/fields/Input/Input'
import ContentContainer from '~/components/contentContainer/ContentContainer'
import Knapp from 'nav-frontend-knapper'
import DataFormatter from '~/utils/DataFormatter'
import DateValidation from '~/components/fields/Datepicker/DateValidation'

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
		environments: []
	}

	validation = () =>
		yup.object().shape({
			ident: yup
				.string()
				.min(11, 'Ident må inneholde 11 sifre.')
				.max(11, 'Ident må inneholde 11 sifre.')
				.required('Ident er et påkrevd felt.'),
			handling: yup.string().required('Handling er et påkrevd felt.'),
			miljoe: yup.string().required('Miljø er et påkrevd felt.'),
			doedsdato: DateValidation
		})

	_onSubmit = values => {
		this.setState(
			{
				isFetching: true,
				meldingSent: false,
				errorMessage: null,
				handlingsType: values.handling
			},
			async () => {
				try {
					await TpsfApi.createDoedsmelding({
						...values,
						doedsdato: DataFormatter.parseDate(values.doedsdato)
					})
					return this.setState({ meldingSent: true, isFetching: false })
				} catch (err) {
					this.setState({
						meldingSent: false,
						errorMessage: err.response.data.message,
						isFetching: false
					})
				}
			}
		)
	}

	fillEnvironmentDropdown(environments) {
		const environmentsForDisplay = []
		environments.map(env => {
			environmentsForDisplay.push({ value: env, label: env.toUpperCase() })
		})

		return environmentsForDisplay
	}

	_handleOnBlurInput = e => {
		var fnr = e.target.value.trim()

		if (fnr.length === 11 && this.state.currentfnr !== fnr && !isNaN(fnr)) {
			this.setState({ isFetchingMiljoer: true, showErrorMessageFoundIdent: false }, async () => {
				try {
					const getMiljoerByFnrRes = await TpsfApi.getMiljoerByFnr(fnr)
					const res_environments = getMiljoerByFnrRes.data.statusPaaIdenter[0].env

					if (res_environments.length < 1) {
						return this.setState({
							currentfnr: fnr,
							foundIdent: false,
							isFetchingMiljoer: false,
							showErrorMessageFoundIdent: true
						})
					}
					const displayEnvironmentsInDropdown = this.fillEnvironmentDropdown(res_environments)
					return this.setState({
						environments: displayEnvironmentsInDropdown,
						currentfnr: fnr,
						foundIdent: true,
						isFetchingMiljoer: false
					})
				} catch (err) {
					this.setState({ isFetchingMiljoer: false, currentfnr: fnr })
				}
			})
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
		return <h3 className="success-message"> Dødsmelding {handling} </h3>
	}

	render() {
		const state = this.state

		let initialValues = {
			ident: '',
			handling: 'C',
			doedsdato: '',
			miljoe: ''
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
					validationSchema={this.validation}
					initialValues={initialValues}
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
										disabled={state.foundIdent ? false : true}
									/>
									<Field
										name="doedsdato"
										label="DØDSDATO"
										component={FormikDatepicker}
										disabled={state.foundIdent ? false : true}
									/>

									<Field
										name="miljoe"
										label="SEND TIL MILJØ"
										options={state.environments}
										component={FormikDollySelect}
										disabled={state.foundIdent ? false : true}
									/>
								</div>
								<div className="knapp-container">
									<Knapp type="hoved" htmlType="submit" disabled={state.foundIdent ? false : true}>
										Opprett dødsmelding
									</Knapp>
								</div>
							</Form>
						)
					}}
				/>
				{this.state.isFetchingMiljoer && <Loading label="Søker etter testbruker" />}
				{this.state.showErrorMessageFoundIdent && (
					<h3 className="error-message">
						Finner ikke testperson med ident: {this.state.currentfnr}
					</h3>
				)}
				{this.state.isFetching && <Loading label="Sender dødsmelding" />}
				{this.state.errorMessage && (
					<h4 className="error-message"> Feil: {this.state.errorMessage} </h4>
				)}
				{this.state.meldingSent && this._renderMeldingSent()}
			</ContentContainer>
		)
	}
}
