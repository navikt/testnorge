import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import { Field, Formik, FieldArray } from 'formik'
import * as yup from 'yup'
import _get from 'lodash/get'
import RemoveableField from '~/components/fields/RemoveableField/RemoveableField'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import Button from '~/components/ui/button/Button'
import KodeverkValueConnector from '~/components/fields/KodeverkValue/KodeverkValueConnector'
import Overskrift from '~/components/ui/overskrift/Overskrift'
import NavigationConnector from '../Navigation/NavigationConnector'
import MiljoVelgerConnector from '~/components/miljoVelger/MiljoVelgerConnector'
import { AttributtManager } from '~/service/Kodeverk'
import Formatters from '~/utils/DataFormatter'
import BestillingMapper from '~/utils/BestillingMapper'
import { FormikInput } from '~/components/fields/Input/Input'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import DataSourceMapper from '~/utils/DataSourceMapper'

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
							label="Malnavn"
							className="input-field-mal-input"
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
		return this.SelectedAttributes.map(hovedKategori => this.renderHovedKategori(hovedKategori))
	}

	renderHovedKategori = ({ hovedKategori, items }) => {
		let removable = items.every(nested => {
			return (
				!nested.subKategori.showInSummary &&
				!nested.items.every(item => this.props.selectedAttributeIds.includes(item.id))
			)
		})
		return (
			<Fragment key={hovedKategori.navn}>
				<h4>{hovedKategori.navn}</h4>
				<RemoveableField
					onRemove={() => this._onRemoveHovedKategori(items)}
					removable={this.state.edit && removable}
					removableText={'FJERN RAD'}
				>
					<div className="oppsummering-blokk oppsummering-blokk-margin">
						{items.map(item => {
							return this.renderSubKategori(item)
						})}
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
		let fieldType = 'oppsummering-multifield-uten-border'

		// Legger til border hvis det finnes flere f.eks. inntekter,
		// eller hvis f.eks. både inntekter og arbeidsforhold ligger under samme hovedkategori
		// Gjøres mer generell?
		if (
			typeof header === 'number' ||
			(header === 'Partner' && this.props.selectedAttributeIds.includes('barn')) ||
			(header === 'Arbeidsforhold' && this.props.selectedAttributeIds.includes('inntekt')) ||
			(header === 'Falsk identitet' &&
				this.props.selectedAttributeIds.includes('falskIdentitet')) ||
			(header === 'Utenlands-ID' &&
				this.props.selectedAttributeIds.includes('utenlandskIdentifikasjonsnummer')) ||
			(header === 'Institusjonsopphold' &&
				this.props.selectedAttributeIds.includes('institusjonsopphold'))
		) {
			fieldType = 'oppsummering-multifield'
		}
		if (
			!items.every(nested => {
				return nested.items
			})
		) {
			let removable = !items.every(item => this.props.selectedAttributeIds.includes(item.id))
			return (
				<div className={fieldType} key={header}>
					<RemoveableField
						removable={removable && this.state.edit}
						removableText={'FJERN RAD'}
						onRemove={() => this._onRemoveSubKategori(items, header)}
					>
						{header && <h4>{typeof header === 'number' ? `# ${header}` : header}</h4>}

						<div
							className={
								typeof items[0].subGruppe === 'string'
									? 'oppsummering-blokk margin'
									: 'oppsummering-blokk'
							}
						>
							{items.map(item => this.renderItem(item, values, header))}
						</div>
					</RemoveableField>
				</div>
			)
		}
		return (
			<div className={fieldType} key={header}>
				{header && !items[0].subGruppe && <h4>{header}</h4>}
				<div className="oppsummering-blokk">
					{items.map(item => this.renderItem(item, values, header))}
				</div>
			</div>
		)
	}

	renderItem = (item, stateValues, header) => {
		if (item.items) {
			let valueArray = _get(this.props.values, item.id)

			if (
				item.id === 'barn_utvandret' ||
				item.id === 'barn_innvandret' ||
				item.id === 'barn_forsvunnet'
			) {
				let barnIndex = 0
				if (header) barnIndex = header - 1
				valueArray = _get(this.props.values.barn[barnIndex], item.id)
			}
			if (!valueArray) return
			return valueArray.map((values, idx) => {
				Object.keys(values).map(attr => {
					return !values[attr] && delete values[attr]
				})

				const header =
					valueArray.length > 1 ? idx + 1 : item.subGruppe ? item.items[0].subGruppe : null

				return this.renderSubKategoriBlokk(header, item.items, values)
			})
		}

		const itemValue = this._formatereValue(item, _get(stateValues, item.id))

		if (!item.inputType) return null
		if (item.onlyShowAfterSelectedValue && !itemValue) return null
		if ((item.id === 'utenFastBopel' || item.id === 'ufb_kommunenr') && !itemValue) return null

		const staticValueProps = {
			key: item.id,
			header: item.label,
			value: itemValue !== '' ? itemValue : null,
			format: item.format,
			size: item.size,
			optionHeight: item.size
		}

		return (
			<RemoveableField
				removable={this.state.edit && this.props.selectedAttributeIds.indexOf(item.id) >= 0}
				onRemove={() => this._onRemove(item)}
				key={item.id}
			>
				{item.apiKodeverkId ? (
					<KodeverkValueConnector
						apiKodeverkId={item.apiKodeverkId}
						showValue={item.id === 'kommunenr' || item.id === 'postnr' ? true : false}
						{...staticValueProps}
					/>
				) : // * Trenger stoette for apiKodeverkId som er avhengig av andre attributt.  Decamelize for bedre ux imidlertig
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
		if (typeof header === 'number' || header === null) {
			this.props.deleteValuesArray({
				values: [...new Set(items.map(item => item.subKategori.id))],
				index: header - 1
			})
		} else {
			let slettHeader = header || items[0].subKategori.id
			slettHeader === 'arena' && (slettHeader = DataSourceMapper(items[0].dataSource))
			slettHeader === 'adressat' && (slettHeader = items[0].hovedKategori.id)
			this.props.deleteValues({ values: [slettHeader.toLowerCase()] })
		}
	}

	_onRemove(item) {
		this.props.deleteValues({ values: [item.id] })
	}

	onClickPrevious = values => {
		this.props.setEnvironments({ values, goBack: true })
	}

	_formatereValue = (item, value) => {
		if (item.dataSource === 'ARENA') {
			return item.id === 'arenaBrukertype'
				? Formatters.uppercaseAndUnderscoreToCapitalized(value)
				: Formatters.oversettBoolean(value)
		}

		if (item.dataSource === 'PDLF' && item.subKategori.id === 'utenlandskIdentifikasjonsnummer') {
			return Formatters.oversettBoolean(
				_get(stateValues['utenlandskIdentifikasjonsnummer'][0], item.id)
			)
		}

		if (item.dataSource === 'INST' && (item.id === 'institusjonstype' || item.id === 'varighet')) {
			return Formatters.showLabel(item.id, value)
		}

		if (
			item.dataSource === 'UDI' &&
			value &&
			(item.id === 'arbeidsOmfang' ||
				item.id === 'typeArbeidsadgang' ||
				item.id === 'oppholdsstatus' ||
				item.id === 'typeOpphold' ||
				item.id === 'ikkeOppholdGrunn' ||
				item.id === 'tredjelandsBorgereValg' ||
				item.id === 'oppholdstillatelseType' ||
				item.id === 'eosEllerEFTAtypeOpphold' ||
				item.id === 'eosEllerEFTAOppholdstillatelse' ||
				item.id === 'eosEllerEFTABeslutningOmOppholdsrett' ||
				item.id === 'eosEllerEFTAVedtakOmVarigOppholdsrett' ||
				item.id === 'nyIdent')
		) {
			return Formatters.showLabel(item.id, value)
		}

		if (
			value &&
			(item.id === 'soeknadOmBeskyttelseUnderBehandling' || item.id === 'harArbeidsAdgang')
		)
			return Formatters.allCapsToCapitalized(value)

		if (value === 'true') return Formatters.oversettBoolean(true) // Quickfix fra SelectOptions(stringBoolean)
		if (value === 'false') return Formatters.oversettBoolean(false)
		return Formatters.oversettBoolean(value)
	}
}
