import React, { PureComponent, Fragment } from 'react'
import { Field } from 'formik'
import _intersection from 'lodash/intersection'
import { DollyApi } from '~/service/Api'
import { AttributtType } from '~/service/kodeverk/AttributtManager/Types'
import Panel from '~/components/panel/Panel'
import InputSelector from '~/components/fields/InputSelector'
import FormEditorFieldArray from './FormEditorFieldArray'
import AutofillAddressConnector from '~/components/autofillAddress/AutofillAddressConnector'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import KodeverkValueConnector from '~/components/fields/KodeverkValue/KodeverkValueConnector'
import Button from '~/components/button/Button'
import _xor from 'lodash/fp/xor'
import './FormEditor.less'
import Postadresse from '../postadresse/Postadresse'

export default class FormEditor extends PureComponent {
	render() {
		const { FormikProps, ClosePanels, AttributtListe } = this.props
		// console.log('this :', this)
		// TODO: editMode burde være en props for hele klassen.
		// editMode? renderEdit....: renderNormal
		return AttributtListe.map(hovedKategori =>
			// Ikke vis kategori som har default ikke-valgt radio button
			this.renderHovedKategori(hovedKategori, FormikProps, ClosePanels)
		)
	}

	renderHovedKategori = ({ hovedKategori, items }, formikProps, closePanels) => {
		const { getAttributtListByHovedkategori, AttributtListeToAdd, AddedAttributes } = this.props
		const hovedKategoriAttributes = getAttributtListByHovedkategori(hovedKategori)

		const hasError = hovedKategoriAttributes.some(attr => {
			const error = formikProps.errors[attr]
			if (error) {
				const touched = formikProps.touched[attr]
				if (touched) {
					if (typeof touched === 'object') {
						const objectHasError = error.some((obj, idx) => {
							if (obj) {
								return Object.keys(obj).some(x => {
									return Boolean(touched[idx] && touched[idx][x])
								})
							}
						})
						return objectHasError
					}
					return true
				}
			}
			return false
		})

		let notYetAddedAttributes = []

		if (AttributtListeToAdd) {
			AttributtListeToAdd.forEach(item => {
				item.hovedKategori.id === hovedKategori.id &&
					item.items.forEach(item => {
						notYetAddedAttributes = _xor(item.items, AddedAttributes)
					})
			})
		}

		return (
			<Panel
				key={hovedKategori.id}
				heading={<h3>{hovedKategori.navn}</h3>}
				startOpen={!closePanels}
				errors={hasError}
			>
				{items.map((item, idx) => {
					return this.renderFieldContainer(item, idx, formikProps)
				})}

				<div className="add-buttons-container">
					{notYetAddedAttributes &&
						notYetAddedAttributes.map((element, i) => {
							return this.renderAddButton(element.label, element, i)
						})}
				</div>
			</Panel>
		)
	}

	renderAddButton = (label, element, i) => {
		return (
			<Button
				className="flexbox--align-center"
				kind="add-circle"
				onClick={() => this.props.onAddAttribute(element)}
				key={i}
			>
				{label}
			</Button>
		)
	}

	//Ny knapp ligger også på adressekategorien. Hvordan sortere hvilke attributt som skal være med?
	renderFieldContainer = ({ subKategori, items }, uniqueId, formikProps) => {
		// TODO: Finn en bedre identifier på å skjule header hvis man er ett fieldArray
		const isAdresse = 'boadresse' === (items[0].parent || items[0].id)
		const isFieldarray = Boolean(items[0].items)

		if (isAdresse) {
			return (
				<div className="subkategori" key={uniqueId}>
					{!isFieldarray && <h4>{subKategori.navn}</h4>}
					<div className="subkategori-field-group">
						<AutofillAddressConnector items={items} formikProps={formikProps} />
					</div>
				</div>
			)
		}

		if (subKategori.id === 'postadresse') {
			return (
				<div className="subkategori" key={uniqueId}>
					{!isFieldarray && <h4>{subKategori.navn}</h4>}
					<div className="subkategori-field-group">
						<Postadresse items={items} formikProps={formikProps} />
					</div>
				</div>
			)
		}

		return (
			<div className="subkategori" key={uniqueId}>
				{!isFieldarray && <h4>{subKategori.navn}</h4>}
				<div className="subkategori-field-group">
					{items.map(
						item =>
							this._shouldRenderFieldComponent(items, item, formikProps)
								? isFieldarray
									? FormEditorFieldArray(
											item,
											formikProps,
											this.renderFieldComponent,
											this.renderFieldSubItem,
											this._shouldRenderFieldComponent,
											this.props.editMode
									  )
									: this.renderFieldComponent(item, formikProps.values)
								: null
					)}
				</div>
			</div>
		)
	}

	// Avhengigheter mellom valgte verdi og field
	// TODO: Vurder om denne løsningen er optimalt når AttributtSystem blir formatert
	// Denne funksjonaliteten burde kanskje være i AttributtManager
	// Denne metode er bygd med fokus for AAREG-felter.

	_shouldRenderFieldComponent = (items, item, formikProps, parentObject) => {
		const valgteVerdier = formikProps.values
		const errors = formikProps.errors

		if (item.onlyShowAfterSelectedValue) {
			const { parentId, idx } = parentObject
			const attributtId = item.onlyShowAfterSelectedValue.attributtId
			const dependantAttributt = items.find(attributt => attributt.id === attributtId)

			const valueIndex = item.onlyShowAfterSelectedValue.valueIndex

			if (
				valgteVerdier[parentId][idx][attributtId] !== dependantAttributt.options[valueIndex].value
			) {
				delete valgteVerdier[parentId][idx][item.id]

				if (errors[parentId] && errors[parentId][idx] && errors[parentId][idx][item.id]) {
					delete errors[parentId][idx][item.id]

					if (Object.keys(errors[parentId][idx]).length === 0) {
						delete errors[parentId][idx]
					}
					let toDelete = true

					errors[parentId].forEach(element => {
						if (element) {
							toDelete = false
						}
					})
					toDelete && delete errors[parentId]
				}
				return false
			}
		}

		return true
	}

	renderFieldComponent = (item, valgteVerdier, parentObject) => {
		if (!item.inputType) return null

		const InputComponent = InputSelector(item.inputType)
		const componentProps = this.extraComponentProps(item, valgteVerdier, parentObject)

		if (this.props.editMode && AttributtType.SelectAndRead === item.attributtType) {
			let valgtVerdi = valgteVerdier[item.id]
			if (parentObject) {
				const { parentId, idx } = parentObject
				const itemIdParsed = item.id.substring(item.id.indexOf(']') + 1)
				valgtVerdi = valgteVerdier[parentId][idx][itemIdParsed]
			}
			const staticValueProps = {
				key: item.key || item.id,
				header: item.label,
				value: valgtVerdi,
				headerType: 'label',
				optionalClassName: 'skjemaelement static'
			}
			if (item.apiKodeverkId) {
				return <KodeverkValueConnector apiKodeverkId={item.apiKodeverkId} {...staticValueProps} />
			}
			return <StaticValue {...staticValueProps} />
		}

		return (
			<Field
				key={item.key || item.id}
				name={item.id}
				label={item.label}
				component={InputComponent}
				size={item.size}
				{...componentProps}
				{...item.inputTypeAttributes}
			/>
		)
	}

	renderFieldSubItem = item => {
		const InputComponent = InputSelector(item.inputType)

		return (
			/* REG-3377: Alex - Under utvikling */
			<p>{item.id}</p>
			// <Field
			// 	key={item.key || item.id}
			// 	name={item.id}
			// 	label={item.label}
			// 	component={InputComponent}
			// 	size={item.size}
			// 	{...componentProps}
			// 	{...item.inputTypeAttributes}
			// />
		)
	}

	extraComponentProps = (item, valgteVerdier, parentObject) => {
		switch (item.inputType) {
			case 'select': {
				const placeholder = !item.validation ? 'Ikke spesifisert' : 'Velg..'

				if (item.dependentOn) {
					if (parentObject) {
						// Sjekk if item er avhengig av en valgt verdi
						const { parentId, idx } = parentObject
						const valgtVerdi = valgteVerdier[parentId][idx][item.dependentOn]
						item.apiKodeverkId = valgtVerdi
						// Override for force rerender av react select
						item.key = valgtVerdi
					} else {
						// TODO: Implement når vi trenger avhengighet mellom flat attributter
					}
				}
				if (item.apiKodeverkId) {
					const showValueInLabel = item.apiKodeverkShowValueInLabel ? true : false
					return {
						placeholder: placeholder,
						loadOptions: async () => {
							const res = await DollyApi.getKodeverkByNavn(item.apiKodeverkId)
							return DollyApi.Utils.NormalizeKodeverkForDropdown(res, showValueInLabel)
						}
					}
				} else {
					return {
						placeholder: placeholder,
						options: item.options
					}
				}
			}
			case 'number': {
				return {
					type: item.inputType
				}
			}
			default:
				return {}
		}
	}
}
