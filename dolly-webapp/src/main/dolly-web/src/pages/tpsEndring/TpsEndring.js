import React, { PureComponent, Fragment } from 'react'
import Overskrift from '~/components/overskrift/Overskrift'
import { Formik, Form, Field } from 'formik'
import { FormikInput } from '~/components/fields/Input/Input'
import ContentContainer from '~/components/contentContainer/ContentContainer'
import './TpsEndring.less'
import Checkbox from '~/components/fields/Checkbox/Checkbox'
import Knapp from 'nav-frontend-knapper'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import { FormikDatepicker } from '~/components/fields/Datepicker/Datepicker'
import { TpsfApi } from '~/service/Api'

export default class TPSEndring extends PureComponent {
	state = {
		isFetching: false
	}

	onSubmit = values => {
		console.log(values)
		this.setState({ isFetching: true }, () => {
			TpsfApi.createFoedselmelding(values).then(res => console.log(res))
		})
	}

	render() {
		return (
			<Fragment>
				<Overskrift label={'Send TPS-endringsmelding'} />
				{this._renderSendFoedselsmelding()}
				{this._renderSendDoedsmelding()}
			</Fragment>
		)
	}

	_renderSendFoedselsmelding = () => {
		let initialValues = {
			morsId: 0,
			farsId: 2,
			IdType: 2,
			foedseldato: 0,
			kjoenn: 0,
			miljoe: 0,
			adresse: 'Elgeseter gate 9'
		}
		return (
			<ContentContainer>
				<Formik
					onSubmit={this.onSubmit}
					initialValues={initialValues}
					render={props => {
						const { values, touched, errors, dirty, isSubmitting } = props
						return (
							<Form autoComplete="off">
								<h2>Send fødselsmelding</h2>
								<div className="tps-endring-foedselmelding-top">
									<Field name="morsId" label="MORS IDENT" component={FormikInput} />
									<Field name="farsId" label="FARS IDENT" component={FormikInput} />
									<Field
										name="BARNETS IDENTTYPE"
										label="BARNETS IDENTTYPE"
										component={FormikDollySelect}
									/>
									<Field name="IdType" label="BARNETS FØDSELSDATO" component={FormikDatepicker} />
									<Field name="foedseldato" label="BARNETS KJØNN" component={FormikDollySelect} />
									<Field name="miljoe" label="SEND TIL MILJØ" component={FormikDollySelect} />
								</div>
								<div className="tps-endring-foedselmelding-bottom">
									<Field name="adresse" label="ADRESSE" component={FormikDollySelect} />
									<Knapp type="hoved" htmlType="submit">
										Opprett doedsmelding
									</Knapp>
								</div>
							</Form>
						)
					}}
				/>
			</ContentContainer>
		)
	}

	_renderSendDoedsmelding = () => {
		let initialValues = {
			id: 1,
			handling: 2,
			doedsdato: 2,
			miljoe: 0
		}

		return (
			<ContentContainer>
				<Formik
					onSubmit={this.onSubmit}
					initialValues={initialValues}
					render={props => {
						const { values, touched, errors, dirty, isSubmitting } = props
						return (
							<Form autoComplete="off">
								<h2>Send dødsmelding</h2>
								<div className="tps-endring-foedselmelding-top">
									<Field name="id" label="ID" component={FormikInput} />
									<Field name="handling" label="HANDLING" component={FormikInput} />
									<Field name="doedsdato" label="DØDSDATO" component={FormikInput} />
									<Field name="miljoe" label="SEND TIL MILJØ" component={FormikInput} />
									<div className="skjemaelement" />
									<div className="skjemaelement" />
								</div>
								<div className="knapp-container">
									<Knapp type="hoved" htmlType="submit">
										Opprett fødselsmelding
									</Knapp>
								</div>
							</Form>
						)
					}}
				/>
			</ContentContainer>
		)
	}
}
