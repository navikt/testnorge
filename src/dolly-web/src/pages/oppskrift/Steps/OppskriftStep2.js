import React, { Component, Fragment } from 'react'
import PropTypes from 'prop-types'
import Input from '~/components/fields/Input/Input'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import Overskrift from '~/components/overskrift/Overskrift'
import Panel from '~/components/panel/Panel'
import { Field } from 'formik'
import { FormikInput } from '~/components/fields/Input/Input'
import { FormikDatepicker } from '~/components/fields/Datepicker/Datepicker'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import OppskriftError from '../OppskriftError'
import { DollyApi } from '~/service/Api'

const inputComponentSelector = {
	date: FormikDatepicker,
	string: FormikInput,
	select: FormikDollySelect
}

const propsSelector = item => {
	switch (item.type) {
		case 'select': {
			if (item.kodeverkNavn) {
				return {
					loadOptions: () =>
						DollyApi.getKodeverkByNavn(item.kodeverkNavn).then(
							DollyApi.Utils.NormalizeKodeverkForDropdown
						)
				}
			} else {
				return {
					options: item.options
				}
			}
		}
		default:
			return {}
	}
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
					<StaticValue header="TYPE" value={values.identtype && values.identtype} />
					<StaticValue header="ANTALL PERSONER" value={values.antall && values.antall.toString()} />
				</div>

				<Panel heading={<h3>Personinformasjon</h3>} startOpen>
					{attributter.personinformasjon.map((group, idx) => {
						return (
							<Fragment key={idx}>
								<h4>{group.label}</h4>
								<div className="oppskrift-field-group">
									{group.items.map(item => {
										const InputComponent = inputComponentSelector[item.type] || FormikInput
										const extraProps = propsSelector(item)

										console.log(item, InputComponent)

										return (
											Boolean(selectedTypes[item.id]) && (
												<Field
													key={item.id}
													name={item.id}
													label={item.label}
													component={InputComponent}
													{...extraProps}
												/>
											)
										)
									})}
								</div>
							</Fragment>
						)
					})}
				</Panel>
			</div>
		)
	}
}
