import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import * as yup from 'yup'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import Overskrift from '~/components/overskrift/Overskrift'
import NavigationConnector from '../Navigation/NavigationConnector'
import MiljoVelger from '~/components/miljoVelger/MiljoVelger'
import { AttributtManager } from '~/service/Kodeverk'
import { Formik, FieldArray, Field } from 'formik'
import _get from 'lodash/get'

export default class Step3 extends PureComponent {
	static propTypes = {
		identtype: PropTypes.string,
		antall: PropTypes.number,
		selectedAttributeIds: PropTypes.arrayOf(PropTypes.string),
		values: PropTypes.object,
		sendBestilling: PropTypes.func.isRequired,
		setEnvironments: PropTypes.func.isRequired
	}

	constructor(props) {
		super(props)
		this.AttributtManager = new AttributtManager()
		this.SelectedAttributes = this.AttributtManager.listSelectedByHovedKategoriWithChildNodes(
			props.selectedAttributeIds
		)
		this.EnvValidation = yup.object().shape({
			environments: yup.array().required('Velg minst ett miljø')
		})
	}

	submit = values => {
		this.props.setEnvironments({ values: values.environments })
		this.props.sendBestilling()
	}

	renderValues = () => {
		return this.SelectedAttributes.map(hovedKategori => this.renderHovedKategori(hovedKategori))
	}

	renderHovedKategori = ({ hovedKategori, items }) => {
		return (
			<Fragment key={hovedKategori.navn}>
				<h4>{hovedKategori.navn}</h4>
				<div className="oppsummering-blokk">{items.map(item => this.renderSubKategori(item))}</div>
			</Fragment>
		)
	}

	renderSubKategoriBlokk = (header, items, values) => {
		return (
			<div className="oppsummering-multifield" key={header}>
				<h4>{header}</h4>
				<div className="oppsummering-blokk">{items.map(item => this.renderItem(item, values))}</div>
			</div>
		)
	}

	renderSubKategori = ({ subKategori, items }) => {
		const { values } = this.props
		if (subKategori.multiple) {
			const valueArray = _get(this.props.values, subKategori.id)
			return valueArray.map((values, idx) => {
				return this.renderSubKategoriBlokk(
					`${subKategori.navn} ${idx ? idx + 1 : ''}`,
					items,
					values
				)
			})
		}

		if (!subKategori.showInSummary) {
			return items.map(item => this.renderItem(item, values))
		}

		return this.renderSubKategoriBlokk(subKategori.navn, values)
	}

	renderItem = (item, stateValues) => {
		return (
			<StaticValue
				key={item.id}
				header={item.label}
				value={_get(stateValues, item.id)}
				format={item.format}
			/>
		)
	}

	onClickPrevious = values => {
		this.props.setEnvironments({ values, goBack: true })
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
										this.onClickPrevious(formikProps.values.environments)
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
