import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import * as yup from 'yup'
import RemoveableField from '~/components/fields/RemoveableField/RemoveableField'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import Button from '~/components/button/Button'
import KodeverkValueConnector from '~/components/fields/KodeverkValue/KodeverkValueConnector'
import Overskrift from '~/components/overskrift/Overskrift'
import NavigationConnector from '../Navigation/NavigationConnector'
import MiljoVelgerConnector from '~/components/miljoVelger/MiljoVelgerConnector'
import { AttributtManager } from '~/service/Kodeverk'
import { Field, Formik, FieldArray } from 'formik'
import _get from 'lodash/get'
import Formatters from '~/utils/DataFormatter'
import BestillingMapper from '~/utils/BestillingMapper'
import { FormikInput } from '~/components/fields/Input/Input'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import { FormikDollySelect } from '~/components/fields/Select/Select'

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
		this.state = { edit: false, showMalNavnError: false }
		this.AttributtManager = new AttributtManager()
		this.EnvValidation = yup.object().shape({
			environments: yup.array().required('Velg minst ett miljø'),
			nyMal: yup.boolean(),
			malNavn: yup.string().when('nyMal', {
				is: nyMal => nyMal === true,
				then: yup.string().required('Malnavn er påkrevd'),
				otherwise: yup.string()
			})
		})
	}

	render() {
		const {
			identtype,
			antall,
			environments,
			selectedAttributeIds,
			identOpprettesFra,
			eksisterendeIdentListe
		} = this.props

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
						{identOpprettesFra === BestillingMapper() ? (
							<div className="grunnoppsett">
								<StaticValue header="TYPE" value={identtype} />
								<StaticValue header="ANTALL PERSONER" value={antall.toString()} />
							</div>
						) : (
							<div className="grunnoppsett">
								<StaticValue header="IDENTER" value={eksisterendeIdentListe} />
							</div>
						)}
						{selectedAttributeIds.length > 0 && (
							<div className="flexbox--align-center--justify-end edit-align-right">
								<Button
									className="flexbox--align-center"
									kind="eraser"
									onClick={() => this.setState({ edit: !this.state.edit })}
								>
									FJERN ATTRIBUTTER
								</Button>
							</div>
						)}
					</div>
					{this.renderValues()}
				</div>

				<Formik
					initialValues={{ environments, malNavn: '' }}
					onSubmit={this.submit}
					validationSchema={this.EnvValidation}
					render={formikProps => {
						return (
							<Fragment>
								<div className="input-container">
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
									{this._renderInputMal(formikProps.values)}
								</div>

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

	_renderInputMal = values => {
		const { showMalNavnError } = this.state
		return (
			<div className="input-mal-field">
				<h3>Mal</h3>
				<div className="flexbox">
					<Field
						name="nyMal"
						label="Lagrer som mal"
						className="input-field"
						component={FormikDollySelect}
						options={SelectOptionsManager('boolean')}
						value={values.nyMal || false}
					/>
					{values.nyMal && (
						<Field
							name="malNavn"
							label="Mal navn"
							className="input-field"
							type="string"
							component={FormikInput}
						/>
					)}
				</div>
				{showMalNavnError && (
					<span style={{ color: 'red' }}>Mal navn finnes allerede. Oppgi et nytt navn</span>
				)}
			</div>
		)
	}

	submit = values => {
		const { maler } = this.props
		if (values.malNavn && values.malNavn !== '') {
			this.setState({ showMalNavnError: false })
			maler.forEach(mal => {
				if (mal.malBestillingNavn === values.malNavn) {
					this.setState({ showMalNavnError: true })
				}
			})
		}

		this.props.setEnvironments({ values: values.environments })
		this.props.createBestillingMal(values.malNavn)
		this.props.sendBestilling()
	}

	renderValues = () => {
		// console.log('this.SelectedAttributes :', this.SelectedAttributes)
		return this.SelectedAttributes.map(hovedKategori => this.renderHovedKategori(hovedKategori))
	}

	renderHovedKategori = ({ hovedKategori, items }) => {
		// console.log('hovedKategori :', hovedKategori);
		// console.log('items :', items);
		let removable = items.every(
			nested =>
				!nested.subKategori.showInSummary &&
				!nested.items.every(item => this.props.selectedAttributeIds.includes(item.id))
		)

		return (
			<Fragment key={hovedKategori.navn}>
				<h4>{hovedKategori.navn}</h4>
				<RemoveableField
					onRemove={() => this._onRemoveHovedKategori(items)}
					removable={this.state.edit && removable}
					removableText={'FJERN RAD'}
				>
					<div className="oppsummering-blokk oppsummering-blokk-margin">
						{items.map(item => this.renderSubKategori(item))}
					</div>
				</RemoveableField>
			</Fragment>
		)
	}

	renderSubKategori = ({ subKategori, items }) => {
		const { values } = this.props

		if (!subKategori.showInSummary) {
			return items.map(item => this.renderItem(item, values))
		}
		return this.renderSubKategoriBlokk(subKategori.navn, items, values)
	}

	renderSubKategoriBlokk = (header, items, values) => {
		if (!items.every(nested => nested.items)) {
			let removable = !items.every(item => this.props.selectedAttributeIds.includes(item.id))
			return (
				<div className="oppsummering-multifield" key={header}>
					<RemoveableField
						removable={removable && this.state.edit}
						removableText={'FJERN RAD'}
						onRemove={() => this._onRemoveSubKategori(items, header)}
					>
						<h4>{typeof header === 'number' ? `# ${header}` : header}</h4>
						<div className="oppsummering-blokk">
							{items.map(item => this.renderItem(item, values))}
						</div>
					</RemoveableField>
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
		// console.log('subKategori :', subKategori)
		// console.log('items :', items)
		const { values } = this.props
		if (!subKategori.showInSummary) {
			return items.map(item => this.renderItem(item, values))
		}
		if (subKategori.id === 'arena') {
			return items[0].items.map(item => this.renderItem(item, values))
		}
		return this.renderSubKategoriBlokk(subKategori.navn, items, values)
	}

	renderItem = (item, stateValues) => {
		// console.log('item :', item)
		// console.log('stateValues :', stateValues)
		if (item.items) {
			const valueArray = _get(this.props.values, item.id)
			const numberOfValues = valueArray.length
			return valueArray.map((values, idx) => {
				Object.keys(values).map(attr => !values[attr] && delete values[attr])
				return valueArray.length > 1
					? this.renderSubKategoriBlokk(idx + 1, item.items, values)
					: this.renderSubKategoriBlokk(null, item.items, values)
			})
		}

		if (!item.inputType) return null

		let itemValue = Formatters.oversettBoolean(_get(stateValues, item.id))

		if (item.dataSource === 'ARENA') {
			item.id === 'arenaBrukertype'
				? (itemValue = Formatters.uppercaseAndUnderscoreToCapitalized(
						_get(stateValues['arenaforvalter'][0], item.id)
				  ))
				: (itemValue = Formatters.oversettBoolean(_get(stateValues['arenaforvalter'][0], item.id)))
		}

		const staticValueProps = {
			key: item.id,
			header: item.label,
			value: itemValue !== '' ? itemValue : null,
			format: item.format
		}

		if (item.onlyShowAfterSelectedValue && !itemValue) return null
		if ((item.id === 'utenFastBopel' || item.id === 'ufb_kommunenr') && !itemValue) return null
		return (
			<RemoveableField
				removable={this.state.edit && this.props.selectedAttributeIds.indexOf(item.id) >= 0}
				onRemove={() => this._onRemove(item)}
				key={item.id}
			>
				{item.apiKodeverkId ? (
					<KodeverkValueConnector apiKodeverkId={item.apiKodeverkId} {...staticValueProps} />
				) : // * Trenger stoette for apiKodeverkId som er avhengig av andre attributt. Decamelize for bedre ux imidlertig
				item.id === 'typeinntekt' ? (
					<StaticValue {...staticValueProps} value={Formatters.decamelize(itemValue, ' ')} />
				) : (
					<StaticValue {...staticValueProps} />
				)}
			</RemoveableField>
		)
	}

	_onRemoveHovedKategori(items) {
		const _findIds = item => (item.items ? [].concat(...item.items.map(i => _findIds(i))) : item.id)
		this.props.deleteValues({ values: _findIds(items) })
	}

	_onRemoveSubKategori(items, header) {
		if (typeof header === 'number') {
			//|| header === null
			this.props.deleteValuesArray({
				values: [...new Set(items.map(item => item.subKategori.id))],
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
}
