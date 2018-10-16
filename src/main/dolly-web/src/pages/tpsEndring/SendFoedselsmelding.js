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
		nyttBarn: null,
		errorMessage: null
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
			miljoe: yup.string().required('Miljø er et påkrevd felt.'),
			foedselsdato: DateValidation,
			adresseFra: yup.string().required('Adresse er et påkrevd felt.')
		})

	_onSubmit = values => {
		this.setState({ isFetching: true, nyttBarn: null, errorMessage: null }, async () => {
			try {
				const createFoedselsmeldingRes = await TpsfApi.createFoedselsmelding({
					...values,
					foedselsdato: DataFormatter.parseDate(values.foedselsdato)
				})
				const getKontaktInformasjonRes = await TpsfApi.getKontaktInformasjon(
					createFoedselsmeldingRes.data.personId,
					values.miljoe
				)

				return this.setState({
					nyttBarn: getKontaktInformasjonRes.data.person,
					isFetching: false
				})
			} catch (err) {
				this.setState({ isFetching: false, errorMessage: err.response.data.message })
			}
		})
	}

	_renderNyttBarn = person => {
		return (
			<Fragment>
				<h3 className="success-message">
					Gratulerer, {person.personNavn.gjeldendePersonnavn} med ident {person.fodselsnummer} ble
					født!
				</h3>
			</Fragment>
		)
	}

	render() {
		let initialValues = {
			identMor: '',
			identFar: '',
			identtype: 'FNR',
			foedselsdato: '',
			kjonn: '',
			miljoe: 't0',
			adresseFra: ''
		}

		const adresseOptions = [
			{ value: 'LAGNY', label: 'LAG NY ADRESSE' },
			{ value: 'MOR', label: 'ARV FRA MORS' },
			{ value: 'FAR', label: 'ARV FRA FARS' }
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
								<h2>Send fødselsmelding</h2>
								<div className="tps-endring-foedselmelding-top">
									<Field name="identMor" label="MORS IDENT" component={FormikInput} />
									<Field name="identFar" label="FARS IDENT" component={FormikInput} />
									<Field
										name="identtype"
										label="BARNETS IDENTTYPE"
										component={FormikDollySelect}
										options={SelectOptionsManager('identtype')}
									/>
									<Field
										name="foedselsdato"
										label="BARNETS FØDSELSDATO"
										component={FormikDatepicker}
									/>
									<Field
										name="kjonn"
										label="BARNETS KJØNN"
										component={FormikDollySelect}
										options={SelectOptionsManager('kjonnBarn')}
									/>
									<Field
										name="miljoe"
										label="SEND TIL MILJØ"
										options={this.props.dropdownMiljoe}
										component={FormikDollySelect}
									/>
								</div>
								<div className="tps-endring-foedselmelding-bottom">
									<Field
										name="adresseFra"
										label="ADRESSE"
										component={FormikDollySelect}
										options={adresseOptions}
									/>
									<Knapp type="hoved" htmlType="submit">
										Opprett fødselsmelding
									</Knapp>
								</div>
								{/* <DisplayFormikState {...props} /> */}
							</Form>
						)
					}}
				/>
				{this.state.isFetching && <Loading label="Sender fødselsmelding" />}
				{this.state.errorMessage && (
					<h4 className="error-message"> Feil: {this.state.errorMessage} </h4>
				)}
				{this.state.nyttBarn && this._renderNyttBarn(this.state.nyttBarn)}
			</ContentContainer>
		)
	}
}
