import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import { FormikInput } from '~/components/fields/Input/Input'
import { Formik, Form, Field, getIn } from 'formik'
import { DollyApi } from '~/service/Api'
import DisplayFormikState from '~/utils/DisplayFormikState'
import Knapp from 'nav-frontend-knapper'
import * as yup from 'yup'
import Table from '~/components/table/Table'
import Loading from '~/components/loading/Loading'

export default class RedigerTeam extends PureComponent {
	static propTypes = {}

	erRedigering = Boolean(getIn(this.props.team, 'id', false))

	onHandleSubmit = async (values, actions) => {
		const { closeLeggTilBruker, addMember } = this.props

		const userArray = values.navIdent.map(user => user.value)
		closeLeggTilBruker()
		addMember(userArray)
	}

	validation = () =>
		yup.object().shape({
			navIdent: yup.string().required('Bruker er et påkrevd felt')
		})

	render() {
		const { closeLeggTilBruker, teamMembers } = this.props

		let initialValues = {
			navIdent: ''
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
							<div className="fields">
								<Field
									name="navIdent"
									label="Velg brukere"
									component={FormikDollySelect}
									multi={true}
									loadOptions={() =>
										DollyApi.getBrukere().then(res =>
											DollyApi.Utils.NormalizeBrukerListForDropdown(res.data, teamMembers)
										)
									}
								/>

								<Knapp type="hoved" htmlType="submit">
									Legg til
								</Knapp>
								<Knapp type="standard" htmlType="button" onClick={closeLeggTilBruker}>
									Avbryt
								</Knapp>
							</div>
						</Form>
					)
				}}
			/>
		)
	}
}
