import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Knapp from 'nav-frontend-knapper'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import Overskrift from '~/components/overskrift/Overskrift'
import MiljoVelger from '~/components/miljoVelger/MiljoVelger'
import { FieldArray, Field } from 'formik'

export default class OppskriftStep3 extends Component {
	static propTypes = {}

	render() {
		const { values, attributter, selectedTypes } = this.props

		console.log(values, attributter)
		return (
			<div>
				<div className="content-header">
					<Overskrift label="Oppsummering" />
				</div>

				<div className="oppsummering">
					<div className="oppsummering-values">
						<StaticValue header="Identtype" value={values.identtype} />
						<StaticValue header="Antall personer" value={values.antall} />
						{attributter.personinformasjon.map(infoType =>
							infoType.items.map(item => {
								return Boolean(selectedTypes[item.id]) ? (
									<StaticValue
										header={item.label}
										value={values[item.id] && values[item.id].toString()}
									/>
								) : null
							})
						)}
					</div>

					{/* <div className="oppsummering-values">
						<StaticValue header="Type" value="FNR" />
						<StaticValue header="Antall personer" value="16" />
						<StaticValue header="Født før" value="01.01.2018" />
						<StaticValue header="Født etter" value="20.06.2007" />
						<StaticValue header="Kjønn" value="Uspesifisert" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
					</div> */}
					{/* <h4>Defaultverdier som blir satt</h4>
					<div className="oppsummering-values">
						<StaticValue header="Statsborgerskap" value="Norsk" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
					</div> */}
				</div>
				<FieldArray
					name="environments"
					render={arrayHelpers => (
						<MiljoVelger
							heading="Hvilke testmiljø vil du opprette testpersonene i?"
							arrayHelpers={arrayHelpers}
							arrayValues={values.environments}
						/>
					)}
				/>

				<Knapp type="hoved" htmlType="submit">
					Opprett testpersoner
				</Knapp>
			</div>
		)
	}
}
