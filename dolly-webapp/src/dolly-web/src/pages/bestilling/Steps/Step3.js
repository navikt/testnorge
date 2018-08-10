import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import * as yup from 'yup'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import Overskrift from '~/components/overskrift/Overskrift'
import NavigationConnector from '../Navigation/NavigationConnector'
import MiljoVelger from '~/components/miljoVelger/MiljoVelger'
import { AttributtManager } from '~/service/Kodeverk'
import { Formik, FieldArray, Field } from 'formik'
import DisplayFormikState from '~/utils/DisplayFormikState'

export default class Step3 extends PureComponent {
	static propTypes = {
		selectedAttributeIds: PropTypes.arrayOf(PropTypes.string),
		values: PropTypes.object
	}

	constructor(props) {
		super(props)
		this.AttributtManager = new AttributtManager()
		this.SelectedAttributes = this.AttributtManager.listSelectedByHovedKategori(
			props.selectedAttributeIds
		)
		this.EnvValidation = yup.object().shape({
			environments: yup.array().required('Velg minst ett miljø')
		})
	}

	submit = values => {
		console.log(values)
		this.props.setEnvironments(values)
	}

	renderValues = () => {
		return this.SelectedAttributes.map(hovedKategori => this.renderHovedKategori(hovedKategori))
	}

	renderHovedKategori = ({ hovedKategori, items }) => (
		<Fragment key={hovedKategori.navn}>
			<h4>{hovedKategori.navn}</h4>
			<div>{items.map(item => this.renderItem(item))}</div>
		</Fragment>
	)

	renderItem = item => (
		<StaticValue key={item.id} header={item.label} value={this.props.values[item.id]} />
	)

	render() {
		const { identtype, antall } = this.props
		return (
			<div className="bestilling-step3">
				<div className="content-header">
					<Overskrift label="Oppsummering" />
				</div>

				<div className="oppsummering">
					<div className="oppsummering-values">
						<StaticValue header="Identtype" value={identtype} />
						<StaticValue header="Antall personer" value={antall.toString()} />

						{this.renderValues()}
					</div>
				</div>

				<Formik
					initialValues={{
						environments: []
					}}
					onSubmit={this.submit}
					validationSchema={this.EnvValidation}
					render={formikProps => {
						return (
							<Fragment>
								<FieldArray
									name="environments"
									render={arrayHelpers => (
										<MiljoVelger
											heading="Hvilke testmiljø vil du opprette testpersonene i?"
											arrayHelpers={arrayHelpers}
											arrayValues={formikProps.values.environments}
										/>
									)}
								/>
								<Field
									name="environments"
									render={({ field, form }) => {
										return form.touched[field.name] && form.errors[field.name] ? (
											<span>{form.errors[field.name]}</span>
										) : null
									}}
								/>
								<NavigationConnector onClickNext={formikProps.submitForm} />
								<DisplayFormikState {...formikProps} />
							</Fragment>
						)
					}}
				/>
			</div>
		)
	}
}
