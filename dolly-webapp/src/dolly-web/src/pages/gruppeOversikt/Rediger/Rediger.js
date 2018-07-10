import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import { FormikInput } from '~/components/fields/Input/Input'
import { Formik, Form, Field } from 'formik'
import { DollyApi } from '~/service/Api'
import DisplayFormikState from '~/utils/DisplayFormikState'
import Knapp from 'nav-frontend-knapper'
import FormErrors from '~/components/formErrors/FormErrors'
import * as yup from 'yup'

import './Rediger.less'

export default class Rediger extends PureComponent {
	static propTypes = {
		gruppe: PropTypes.shape({
			id: PropTypes.string,
			navn: PropTypes.string,
			teamTilhoerlighetNavn: PropTypes.string,
			hensikt: PropTypes.string
		}),
		createGruppe: PropTypes.func,
		updateGruppe: PropTypes.func,
		cancelRedigerOgOpprett: PropTypes.func
	}

	onHandleSubmit = (values, actions) => {
		console.log('onHandleSubmit()', values, actions)
		// const { createGruppe, updateGruppe } = this.props
		// const res = gruppeObj.id ? await updateGruppe(gruppeObj) : await createGruppe(gruppeObj)
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
		const { cancelRedigerOgOpprett, currentUserId, gruppe } = this.props

		let initialValues = {
			navn: '',
			teamId: null,
			hensikt: ''
		}

		if (this.props.gruppe) {
			initialValues = Object.assign({}, initialValues, {
				navn: gruppe.navn,
				teamId: 3000000,
				hensikt: gruppe.hensikt || ''
			})
		}

		return (
			<Formik
				initialValues={initialValues}
				validationSchema={this.validation}
				onSubmit={this.onHandleSubmit}
				render={props => {
					const { values, touched, errors, dirty, isSubmitting } = props
					return (
						<Form className="opprett-gruppe" autoComplete="off">
							<div className="fields">
								<Field name="navn" label="Navn" className="test" component={FormikInput} />
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
									{this.props.redigering ? 'OPPDATER' : 'OPPRETT'}
								</Knapp>
								<Knapp type="standard" onClick={cancelRedigerOgOpprett}>
									Avbryt
								</Knapp>
							</div>

							<FormErrors errors={errors} touched={touched} />
							<DisplayFormikState {...props} />
						</Form>
					)
				}}
			/>
		)
	}
}
