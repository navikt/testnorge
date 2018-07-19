import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import { FormikInput } from '~/components/fields/Input/Input'
import { Formik, Form, Field, getIn } from 'formik'
import { DollyApi } from '~/service/Api'
import DisplayFormikState from '~/utils/DisplayFormikState'
import Knapp from 'nav-frontend-knapper'
import FormErrors from '~/components/formErrors/FormErrors'
import * as yup from 'yup'
import Loading from '~/components/loading/Loading'
import Table from '~/components/table/Table'

// import './Rediger.less'

export default class Rediger extends PureComponent {
	static propTypes = {
		gruppe: PropTypes.shape({
			id: PropTypes.number,
			navn: PropTypes.string,
			teamTilhoerlighetNavn: PropTypes.string,
			hensikt: PropTypes.string
		}),
		createGruppe: PropTypes.func,
		updateGruppe: PropTypes.func,
		closeRedigerOgOpprett: PropTypes.func
	}

	erRedigering = Boolean(getIn(this.props.gruppe, 'id', false))

	onHandleSubmit = async (values, actions) => {
		const { createGruppe, updateGruppe, gruppe } = this.props
		// console.log('onHandleSubmit()', values, actions)
		const res = this.erRedigering
			? await updateGruppe(gruppe.id, values)
			: await createGruppe(values)
	}

	validation = () =>
		yup.object().shape({
			navn: yup.string().required('Navn er et påkrevd felt'),
			teamId: yup
				.number()
				.nullable()
				.required('Du må velge hvilket team gruppen skal knyttes til'),
			hensikt: yup.string().required('Gi en liten beskrivelse av hensikten med gruppen')
		})

	render() {
		const { closeRedigerOgOpprett, currentUserId, gruppe, createOrUpdateFetching } = this.props
		if (createOrUpdateFetching) {
			return (
				<Table.Row>
					<Loading label="oppdaterer gruppe" />
				</Table.Row>
			)
		}

		let initialValues = {
			navn: getIn(gruppe, 'navn', ''),
			teamId: getIn(gruppe, 'team.id', null),
			hensikt: getIn(gruppe, 'hensikt', '')
		}

		return (
			<Formik
				initialValues={initialValues}
				validationSchema={this.validation}
				onSubmit={this.onHandleSubmit}
				render={props => {
					const { values, touched, errors, dirty, isSubmitting } = props
					return (
						<Form className="opprett-tabellrad" autoComplete="off">
							<h2>{this.erRedigering ? 'Rediger gruppe' : 'Opprett gruppe'}</h2>
							<div className="fields">
								<Field name="navn" label="Navn" autoFocus component={FormikInput} />
								<Field
									name="teamId"
									label="Velg team"
									component={FormikDollySelect}
									loadOptions={() =>
										DollyApi.getTeamsByUserId(currentUserId).then(
											DollyApi.Utils.NormalizeTeamListForDropdown
										)
									}
								/>
								<Field name="hensikt" label="Hensikt" component={FormikInput} />

								<Knapp type="hoved" htmlType="submit">
									{this.erRedigering ? 'OPPDATER' : 'OPPRETT'}
								</Knapp>
								<Knapp type="standard" htmlType="button" onClick={closeRedigerOgOpprett}>
									Avbryt
								</Knapp>
							</div>

							<FormErrors errors={errors} touched={touched} />
							{/* <DisplayFormikState {...props} /> */}
						</Form>
					)
				}}
			/>
		)
	}
}
