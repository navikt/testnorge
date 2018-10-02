import * as yup from 'yup'
import Loading from '~/components/loading/Loading'
import { TpsfApi } from '~/service/Api'
import TpsfService from '../../service/services/tpsf/TpsfService'
import React, { PureComponent, Fragment } from 'react'
import { Formik, Form, Field } from 'formik'
import { FormikInput } from '~/components/fields/Input/Input'
import ContentContainer from '~/components/contentContainer/ContentContainer'
import Knapp from 'nav-frontend-knapper'

export default class SendDoedsmelding extends PureComponent {
	state = {
		isFetching: false,
		errorMessage: null,
		meldingSent: false
	}

	render = () => {
		let initialValues = {
			ident: '',
			handling: 'C',
			doedsdato: '2018-10-01T10:00:00.000Z',
			miljoe: 't0'
		}

		return (
			<ContentContainer>
				<Formik
					onSubmit={this._onSubmit}
					initialValues={initialValues}
					render={props => {
						const { values, touched, errors, dirty, isSubmitting } = props
						return (
							<Form autoComplete="off">
								<h2>Send dødsmelding</h2>
								<div className="tps-endring-foedselmelding-top">
									<Field name="ident" label="IDENT" component={FormikInput} />
									<Field name="handling" label="HANDLING" component={FormikInput} />
									<Field name="doedsdato" label="DØDSDATO" component={FormikInput} />
									<Field name="miljoe" label="SEND TIL MILJØ" component={FormikInput} />
									<div className="skjemaelement" />
									<div className="skjemaelement" />
								</div>
								<div className="knapp-container">
									<Knapp type="hoved" htmlType="submit">
										Opprett dødsmelding
									</Knapp>
								</div>
							</Form>
						)
					}}
				/>
				{this.state.isFetching && <Loading label="Sender dødsmelding" />}
				{this.state.errorMessage && (
					<h4 className="error-message"> Feil: {this.state.errorMessage} </h4>
				)}
				{this.state.meldingSent && <h3 className="success-message">Dødsmelding sendt </h3>}
			</ContentContainer>
		)
	}

	_onSubmit = values => {
		console.log(values)
		this.setState({ isFetching: true, meldingSent: false, errorMessage: null }, () => {
			TpsfApi.createDoedsmelding(values)
				.then(res => {
					this.setState({ meldingSent: true, isFetching: false })
					console.log(res)
				})
				.catch(err => {
					console.log(err.response)
					this.setState({
						meldingSent: false,
						errorMessage: err.response.data.message,
						isFetching: false
					})
				})
		})
	}
}
