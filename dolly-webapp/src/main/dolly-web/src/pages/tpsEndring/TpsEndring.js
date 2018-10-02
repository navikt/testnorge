import React, { PureComponent, Fragment } from 'react'
import Overskrift from '~/components/overskrift/Overskrift'
import { Formik, Form, Field } from 'formik'
import { FormikInput } from '~/components/fields/Input/Input'
import ContentContainer from '~/components/contentContainer/ContentContainer'
import './TpsEndring.less'
import Knapp from 'nav-frontend-knapper'
import { EnvironmentManager } from '~/service/Kodeverk'
import SendFoedselsmelding from './SendFoedselsmelding'

export default class TPSEndring extends PureComponent {
	constructor() {
		super()
		this.evns = new EnvironmentManager().getAllDropdownEnvironments()
	}

	render() {
		return (
			<Fragment>
				<Overskrift label={'Send TPS-endringsmelding'} />
				<SendFoedselsmelding dropdownMiljoe={this.evns} />
				{/* {this._renderSendFoedselsmelding()} */}
				{this._renderSendDoedsmelding()}
			</Fragment>
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
