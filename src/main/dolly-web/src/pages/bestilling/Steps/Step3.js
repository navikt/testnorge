import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import * as yup from 'yup'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import Overskrift from '~/components/overskrift/Overskrift'
import NavigationConnector from '../Navigation/NavigationConnector'
import MiljoVelger from '~/components/miljoVelger/MiljoVelger'
import { AttributtManager } from '~/service/Kodeverk'
import { Formik, FieldArray, Field } from 'formik'
import FormatDate from '~/utils/FormatDate'
import _get from 'lodash/get'

export default class Step3 extends PureComponent {
	static propTypes = {
		identtype: PropTypes.string,
		antall: PropTypes.number,
		selectedAttributeIds: PropTypes.arrayOf(PropTypes.string),
		values: PropTypes.object,
		postBestilling: PropTypes.func.isRequired,
		setEnvironments: PropTypes.func.isRequired
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
		this.props.setEnvironments(values.environments)
		this.props.postBestilling()
	}

	renderValues = () => {
		return this.SelectedAttributes.map(hovedKategori => this.renderHovedKategori(hovedKategori))
	}

	renderHovedKategori = ({ hovedKategori, items }) => (
		<Fragment key={hovedKategori.navn}>
			<h4>{hovedKategori.navn}</h4>
			<div className="oppsummering-blokk">{items.map(item => this.renderItem(item))}</div>
		</Fragment>
	)

	renderItem = item => {
		let value = _get(this.props.values, item.id)
		if (item.inputType === 'date') value = FormatDate(value)

		return <StaticValue key={item.id} header={item.label} value={value} />
	}

	onClickPrevious = values => {
		this.props.setValuesAndGoBack(values)
	}

	render() {
		const { identtype, antall, environments } = this.props

		return (
			<div className="bestilling-step3">
				<div className="content-header">
					<Overskrift label="Oppsummering" />
				</div>

				<div className="oppsummering">
					<div className="oppsummering-blokk">
						<StaticValue header="Identtype" value={identtype} />
						<StaticValue header="Antall personer" value={antall.toString()} />
					</div>
					{this.renderValues()}
				</div>

				<Formik
					initialValues={{
						environments
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
								<NavigationConnector
									onClickNext={formikProps.submitForm}
									onClickPrevious={() => {
										this.props.setEnvironmentsAndGoBack(formikProps.values.environments)
									}}
								/>
							</Fragment>
						)
					}}
				/>
			</div>
		)
	}
}
