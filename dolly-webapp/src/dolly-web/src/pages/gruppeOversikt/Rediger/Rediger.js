import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import { FormikInput } from '~/components/fields/Input/Input'
import { Formik, Form, Field } from 'formik'
import { DollyApi } from '~/service/Api'
import DisplayFormikState from '~/utils/DisplayFormikState'
import Knapp from 'nav-frontend-knapper'

import './Rediger.less'

export default class Rediger extends PureComponent {
	static propTypes = {
		gruppe: PropTypes.shape({
			navn: PropTypes.string,
			teamTilhoerlighetNavn: PropTypes.string,
			hensikt: PropTypes.string
		}),
		createGruppe: PropTypes.func,
		updateGruppe: PropTypes.func,
		cancelRedigerOgOpprett: PropTypes.func
	}

	createGroup = async e => {
		const { index, createGruppe, updateGruppe } = this.props
		const res = gruppeObj.id ? await updateGruppe(gruppeObj) : await createGruppe(gruppeObj)
	}

	render() {
		const { cancelRedigerOgOpprett, currentUserId } = this.props
		return (
			<Formik
				initialValues={{
					navn: '',
					team: null,
					hensikt: ''
				}}
				onSubmit={(values, actions) => {
					console.log(values, actions)
				}}
				render={props => {
					const { values, touched, errors, dirty, isSubmitting } = props
					return (
						<Form className="opprett-gruppe" autoComplete="off">
							<Field name="navn" label="Navn" className="test" component={FormikInput} />
							<Field
								name="team"
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
							<DisplayFormikState {...props} />
						</Form>
					)
				}}
			/>
		)
	}
}
