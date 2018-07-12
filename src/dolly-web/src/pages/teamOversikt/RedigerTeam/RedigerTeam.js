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

export default class RedigerTeam extends PureComponent {
	static propTypes = {}

	erRedigering = Boolean(getIn(this.props.team, 'id', false))

	onHandleSubmit = async (values, actions) => {
		const { createTeam, updateTeam, team } = this.props
		const res = this.erRedigering ? await updateTeam(team.id, values) : await createTeam(values)
	}

	validation = () =>
		yup.object().shape({
			navn: yup.string().required('Navn er et påkrevd felt'),
			beskrivelse: yup.string().required('Gi en liten beskrivelse av teamet')
		})

	render() {
		const { closeOpprettRedigerTeam, team } = this.props

		let initialValues = {
			navn: getIn(team, 'navn', ''),
			beskrivelse: getIn(team, 'beskrivelse', '')
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
							<h2>{this.erRedigering ? 'Rediger team' : 'Opprett team'}</h2>
							<div className="fields">
								<Field name="navn" label="Navn" autoFocus component={FormikInput} />
								<Field name="beskrivelse" label="Beskrivelse" component={FormikInput} />

								<Knapp type="hoved" htmlType="submit">
									{this.erRedigering ? 'OPPDATER' : 'OPPRETT'}
								</Knapp>
								<Knapp type="standard" htmlType="button" onClick={closeOpprettRedigerTeam}>
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
