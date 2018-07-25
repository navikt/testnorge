import React, { Component, Fragment } from 'react'
import PropTypes from 'prop-types'
import Input from '~/components/fields/Input/Input'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import Overskrift from '~/components/overskrift/Overskrift'
import Panel from '~/components/panel/Panel'
import { Field } from 'formik'
import { FormikInput } from '~/components/fields/Input/Input'
import { FormikDatepicker } from '~/components/fields/Datepicker/Datepicker'

const inputComponentSelector = {
	date: FormikDatepicker,
	string: FormikInput
}

export default class OppskriftStep2 extends Component {
	static propTypes = {}

	render() {
		const { selectedTypes, attributter, values } = this.props

		return (
			<div>
				<div className="content-header">
					<Overskrift label="Velg verdier" />
				</div>

				<div className="grunnoppsett">
					<StaticValue header="TYPE" value={values.identtype} />
					<StaticValue header="ANTALL PERSONER" value={values.antall && values.antall.toString()} />
				</div>

				<Panel heading={<h3>Personinformasjon</h3>}>
					{attributter.personinformasjon.map((group, idx) => {
						return (
							<Fragment key={idx}>
								<h4>{group.label}</h4>
								{group.items.map(item => {
									const InputComponent = inputComponentSelector[item.type] || FormikInput

									return (
										Boolean(selectedTypes[item.id]) && (
											<Field
												key={item.id}
												name={item.id}
												label={item.label}
												component={InputComponent}
											/>
										)
									)
								})}
							</Fragment>
						)
					})}
				</Panel>

				{/* <Panel heading={<h3>Adresser</h3>}>
					Ullamco Lorem non voluptate adipisicing ipsum pariatur fugiat sint eu nostrud commodo.
				</Panel> */}

				{/* <Panel heading={<h3>Arbeidsforhold</h3>}>
					Ullamco Lorem non voluptate adipisicing ipsum pariatur fugiat sint eu nostrud commodo.
				</Panel> */}
			</div>
		)
	}
}
