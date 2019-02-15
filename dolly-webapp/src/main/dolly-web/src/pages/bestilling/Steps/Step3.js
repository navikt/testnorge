import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import * as yup from 'yup'
import RemoveableFeild from '~/components/fields/RemoveableField/RemoveableField'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import Button from '~/components/button/Button'
import KodeverkValueConnector from '~/components/fields/KodeverkValue/KodeverkValueConnector'
import Overskrift from '~/components/overskrift/Overskrift'
import NavigationConnector from '../Navigation/NavigationConnector'
import MiljoVelgerConnector from '~/components/miljoVelger/MiljoVelgerConnector'
import { AttributtManager } from '~/service/Kodeverk'
import { Formik, FieldArray, Field } from 'formik'
import _get from 'lodash/get'
import Formatters from '~/utils/DataFormatter'

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
		this.state = { edit: false }
		this.AttributtManager = new AttributtManager()
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
		let removable = items.every(
			nested =>
				!nested.subKategori.showInSummary &&
				!nested.items.every(item => this.props.selectedAttributeIds.includes(item.id))
		)

		return (
			<Fragment key={hovedKategori.navn}>
				<h4>{hovedKategori.navn}</h4>
				<RemoveableFeild
					onRemove={() => this._onRemoveHovedKategori(items)}
					removable={this.state.edit && removable}
					style={{ borderLeft: '15px' }}
				>
					<div className="oppsummering-blokk oppsummering-blokk-margin">
						{items.map(item => this.renderSubKategori(item))}
					</div>
				</RemoveableFeild>
			</Fragment>
		)
	}

	renderSubKategoriBlokk = (header, items, values) => {
		if (!items.every(nested => nested.items)) {
			let removable = !items.every(item => this.props.selectedAttributeIds.includes(item.id))
			return (
				<div className="oppsummering-multifield" key={header}>
					<RemoveableFeild
						removable={removable && this.state.edit}
						onRemove={() => this._onRemoveSubKategori(items, header)}
					>
						<h4>{typeof header === 'number' ? `# ${header}` : header}</h4>
						<div className="oppsummering-blokk">
							{items.map(item => this.renderItem(item, values))}
						</div>
					</RemoveableFeild>
				</div>
			)
		}
		return (
			<div className="oppsummering-multifield" key={header}>
				<h4>{header}</h4>
				<div className="oppsummering-blokk">{items.map(item => this.renderItem(item, values))}</div>
			</div>
		)
	}

	renderSubKategori = ({ subKategori, items }) => {
		const { values } = this.props

		if (!subKategori.showInSummary) {
			return items.map(item => this.renderItem(item, values))
		}
		return this.renderSubKategoriBlokk(subKategori.navn, items, values)
	}

	renderItem = (item, stateValues) => {
		if (item.items) {
			const valueArray = _get(this.props.values, item.id)
			return valueArray.map((values, idx) => {
				return this.renderSubKategoriBlokk(idx + 1, item.items, values)
			})
		}

		if (!item.inputType) return null

		const staticValueProps = {
			key: item.id,
			header: item.label,
			value: Formatters.oversettBoolean(_get(stateValues, item.id)),
			format: item.format
		}

		return (
			<RemoveableFeild
				removable={this.state.edit && this.props.selectedAttributeIds.indexOf(item.id) >= 0}
				onRemove={() => this._onRemove(item)}
				key={item.id}
			>
				{item.apiKodeverkId ? (
					<KodeverkValueConnector apiKodeverkId={item.apiKodeverkId} {...staticValueProps} />
				) : (
					<StaticValue {...staticValueProps} />
				)}
			</RemoveableFeild>
		)
	}

	_onRemoveHovedKategori(items) {
		let toDelete = []
		items.forEach(nested => nested.items.forEach(item => toDelete.push(item.id)))
		this.props.deleteValues({ values: toDelete })
	}

	_onRemoveSubKategori(items, header) {
		if (typeof header === 'number') {
			this.props.deleteValuesArray({
				values: items.map(item => item.subKategori.id),
				index: header - 1
			})
		} else {
			this.props.deleteValues({ values: [header.toLowerCase()] })
		}
	}

	_onRemove(item) {
		this.props.deleteValues({ values: [item.id] })
	}

	onClickPrevious = values => {
		this.props.setEnvironments({ values, goBack: true })
	}

	render() {
		const { identtype, antall, environments, selectedAttributeIds } = this.props

		this.SelectedAttributes = this.AttributtManager.listSelectedAttributesForValueSelection(
			selectedAttributeIds
		)

		return (
			<div className="bestilling-step3">
				<div className="content-header">
					<Overskrift label="Oppsummering" />
				</div>

				<div className="oppsummering">
					<div className="oppsummering-blokk oppsummering-blokk-margin">
						<StaticValue header="Identtype" value={identtype} />
						<StaticValue header="Antall personer" value={antall.toString()} />
					</div>
					{this.renderValues()}
					{selectedAttributeIds.length > 0 && (
						<div className="flexbox--align-center--justify-start">
							<Button
								className="flexbox--align-center"
								kind="edit"
								onClick={() => this.setState({ edit: !this.state.edit })}
							>
								REDIGER
							</Button>
						</div>
					)}
				</div>

				<Formik
					initialValues={{ environments }}
					onSubmit={this.submit}
					validationSchema={this.EnvValidation}
					render={formikProps => {
						return (
							<Fragment>
								<FieldArray
									name="environments"
									render={arrayHelpers => (
										<MiljoVelgerConnector
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
