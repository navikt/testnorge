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
import { TpsfApi, DollyApi } from '~/service/Api'
import TpsfService from '../../service/services/tpsf/TpsfService'

export default class TPSEndring extends PureComponent {
	state = {
		isFetching: false,
		foedselmeldingSent: false,
		nyttBarn: null
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

	validation = () =>
		yup.object().shape({
			navn: yup.string().required('Navn er et påkrevd felt'),
			teamId: yup.number().nullable(),
			// .required('Du må velge hvilket team gruppen skal knyttes til'),
			hensikt: yup.string().required('Gi en liten beskrivelse av hensikten med gruppen')
		})

	onFoedselsMeldingSubmit = values => {
		console.log(values)
		this.setState({ isFetching: true }, () => {
			TpsfApi.createFoedselmelding(values).then(res => {
				TpsfService.getKontaktInformasjon(res.data.personId, 't0').then(res2 => {
					console.log(res2)
					this.setState({ nyttBarn: res2.data.person, foedselmeldingSent: true, isFetching: false })
				})
				// this.setState({ foedselmeldingSent: true, isFetching: false })
			})
		})
	}

	_renderSendFoedselsmelding = () => {
		let initialValues = {
			identMor: '01111206747',
			identFar: '',
			identtype: 'FNR',
			foedselsdato: '2018-12-01T00:00:00',
			kjonn: 'M',
			miljoe: 't0',
			adresseFra: 'LAGNY'
		}
		return (
			<ContentContainer>
				<Formik
					onSubmit={this.onFoedselsMeldingSubmit}
					initialValues={initialValues}
					render={props => {
						const { values, touched, errors, dirty, isSubmitting } = props
						return (
							<Form autoComplete="off">
								<h2>Send fødselsmelding</h2>
								<div className="tps-endring-foedselmelding-top">
									<Field name="identMor" label="MORS IDENT" component={FormikInput} />
									<Field name="identFar" label="FARS IDENT" component={FormikInput} />
									<Field
										name="identtype"
										label="BARNETS IDENTTYPE"
										component={FormikDollySelect}
										// loadOptions={} //TODO: spoer Cong for load options her
									/>
									<Field name="date" label="BARNETS FØDSELSDATO" component={FormikDatepicker} />
									<Field name="kjonn" label="BARNETS KJØNN" component={FormikDollySelect} />
									<Field name="miljoe" label="SEND TIL MILJØ" component={FormikDollySelect} />
								</div>
								<div className="tps-endring-foedselmelding-bottom">
									<Field name="adresseFra" label="ADRESSE" component={FormikDollySelect} />
									<Knapp type="hoved" htmlType="submit">
										Opprett fødselsmelding
									</Knapp>
								</div>
							</Form>
						)
					}}
				/>
				{this.state.nyttBarn && this._renderNyttBarn(this.state.nyttBarn)}
			</ContentContainer>
		)
	}

	_renderNyttBarn = person => {
		return (
			<Fragment>
				<h2>Gratulere, {person.personNavn.gjeldendePersonnavn} ble født! </h2>
				<p className="build-version">
					den {person.fodselsdato} med personnr {person.fodselsnummer}
				</p>
			</Fragment>
		)
	}

	_renderNyttBarn2 = () => {
		return <h2> ble født! </h2>
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
										Opprett dødsmelding
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
