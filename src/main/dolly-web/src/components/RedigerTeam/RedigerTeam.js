import React, { PureComponent } from 'react'
import { FormikInput } from '~/components/fields/Input/Input'
import { Formik, Form, Field, getIn } from 'formik'
import DisplayFormikState from '~/utils/DisplayFormikState'
import Knapp from 'nav-frontend-knapper'
import * as yup from 'yup'
import Loading from '~/components/ui/loading/Loading'

export default class RedigerTeam extends PureComponent {
	static propTypes = {}

	erRedigering = Boolean(getIn(this.props.team, 'id', false))

	onHandleSubmit = async (values, actions) => {
		const { createTeam, updateTeam, team } = this.props
		const res = this.erRedigering ? await updateTeam(team.id, values) : await createTeam(values)
	}

	validation = () =>
		yup.object().shape({
			navn: yup
				.string()
				.trim()
				.required('Navn er et påkrevd felt')
				.max(30, 'Maksimalt 30 bokstaver'),
			beskrivelse: yup
				.string()
				.trim()
				.required('Gi en liten beskrivelse av teamet')
				.max(200, 'Maksimalt 200 bokstaver')
		})

	render() {
		const { onCancel, team, teamIsUpdating } = this.props
		if (teamIsUpdating) {
			return (
				<div className="opprett-tabellrad loading">
					<Loading label="oppdaterer gruppe" />
				</div>
			)
		}

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
					// const { values, touched, errors, dirty, isSubmitting } = props
					return (
						<Form className="opprett-tabellrad" autoComplete="off">
							<div className="fields">
								<Field name="navn" label="NAVN" autoFocus component={FormikInput} />
								<Field name="beskrivelse" label="BESKRIVELSE" component={FormikInput} />

								<Knapp mini type="standard" htmlType="button" onClick={onCancel}>
									Avbryt
								</Knapp>
								<Knapp mini type="hoved" htmlType="submit">
									{this.erRedigering ? 'Lagre' : 'Opprett'}
								</Knapp>
							</div>

							{/* <DisplayFormikState {...props} /> */}
						</Form>
					)
				}}
			/>
		)
	}
}
