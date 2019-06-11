import React, { PureComponent, Fragment } from 'react'
import { Field } from 'formik'
import _intersection from 'lodash/intersection'
import _set from 'lodash/set'
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
import UtenFastBopelConnector from '../utenFastBopel/UtenFastBopelConnector'
import Postadresse from '../postadresse/Postadresse'

export default class FormEditor extends PureComponent {
	render() {
		const { FormikProps, ClosePanels, AttributtListe } = this.props

		// TODO: editMode burde være en props for hele klassen.
		// editMode? renderEdit....: renderNormal
		return AttributtListe.map(hovedKategori => {
			// Ikke vis kategori som har default ikke-valgt radio button
			return this.renderHovedKategori(hovedKategori, FormikProps, ClosePanels)
		})
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
					item.items.forEach(subkatItem => {
						let addedAttrIKategori = []
						AddedAttributes.map(
							attr =>
								attr.hovedKategori.id === item.hovedKategori.id && addedAttrIKategori.push(attr)
						)
						notYetAddedAttributes = _xor(subkatItem.items, addedAttrIKategori)
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
		// const isFieldarray = Boolean(items[0].items)
		const isMultiple = items[0].isMultiple

		if (isAdresse) {
			return (
				<div className="subkategori" key={uniqueId}>
					<h4>{subKategori.navn}</h4>
					<div className="subkategori-field-group">
						<AutofillAddressConnector items={items} formikProps={formikProps} />
					</div>
				</div>
			)
		}

		if (subKategori.id === 'postadresse') {
			return (
				<div className="subkategori" key={uniqueId}>
					<h4>{subKategori.navn}</h4>
					<div className="subkategori-field-group">
						<Postadresse items={items} formikProps={formikProps} />
					</div>
				</div>
			)
		}
		if (subKategori.id === 'doedsbo') {
			//Kan også gjøre sjekk items[0].subGruppe = true
			const subGrupper = this._structureSubGruppe(items[0])
			return (
				<div className="subkategori" key={uniqueId}>
					<h4>{subKategori.navn}</h4>
					{subGrupper.map((subGruppe, idx) => {
						const subGruppeObj = Object.assign({}, { ...items[0], items: subGruppe.items })
						return (
							<div key={idx}>
								<h4 className="subgruppe">{subGruppe.navn}</h4>
								{FormEditorFieldArray(
									subGruppeObj,
									formikProps,
									this.renderFieldComponent,
									this.renderFieldSubItem,
									this._shouldRenderFieldComponent,
									this.props.editMode,
									this._shouldRenderSubItem
								)}
							</div>
						)
					})}
				</div>
			)
		}

		return (
			<div className="subkategori" key={uniqueId}>
				<h4>{subKategori.navn}</h4>
				<div className="subkategori-field-group">
					{items.map(item => {
						const isFieldarray = Boolean(item.items)
						return this._shouldRenderFieldComponent(items, item, formikProps)
							? isFieldarray
								? FormEditorFieldArray(
										item,
										formikProps,
										this.renderFieldComponent,
										this.renderFieldSubItem,
										this._shouldRenderFieldComponent,
										this.props.editMode,
										this._shouldRenderSubItem
								  )
								: this.renderFieldComponent(item, formikProps.values)
							: null
					})}
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
		let shouldRender = true

		if (item.onlyShowAfterSelectedValue) {
			const { parentId, idx } = parentObject
			const attributtId = item.onlyShowAfterSelectedValue.attributtId
			const dependantAttributt = items.find(attributt => attributt.id === attributtId)

			let foundIndex = false
			item.onlyShowAfterSelectedValue.valueIndex.map(index => {
				valgteVerdier[parentId][idx][attributtId] === dependantAttributt.options[index].value &&
					(foundIndex = true)
			})

			if (!foundIndex) {
				this._deleteValidation(item, valgteVerdier, errors, parentId, idx)
				shouldRender = false
			}
		}

		if (item.onlyShowDependentOnOtherValue) {
			const { parentId, idx } = parentObject
			let deleteValidation = true
			const attributtId = item.onlyShowDependentOnOtherValue.attributtId
			const valgtVerdi = formikProps.values[parentId][idx][attributtId]
			const dependentValue = item.onlyShowDependentOnOtherValue.value
			const exceptValue = item.onlyShowDependentOnOtherValue.exceptValue
			dependentValue &&
				dependentValue.map(value => value === valgtVerdi && (deleteValidation = false))

			let exception = false
			exceptValue && exceptValue.map(value => value === valgtVerdi && (exception = true))

			if (exceptValue) deleteValidation = exception
			if (deleteValidation) {
				this._deleteValidation(item, valgteVerdier, errors, parentId, idx)
				shouldRender = false
			} else if (!Object.keys(formikProps.values[parentId][idx]).includes(item.id)) {
				this._setFieldValue(formikProps, parentId, idx, item.id)
			}
		}
		return shouldRender
	}

	_setFieldValue = (formikProps, parentId, idx, itemId) => {
		const path = parentId + '[' + idx + '].' + itemId
		formikProps.setFieldValue(path, '', true)
	}
	_deleteValidation = (item, valgteVerdier, errors, parentId, idx) => {
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
	}

	_shouldRenderSubItem = (item, formikProps, idx) => {
		const subitemId = item.id
		const subKatId = item.subKategori.id
		return Boolean(formikProps.values[subKatId][idx][subitemId][0])
	}

	_renderSubGruppe = (item, formikProps) => {
		// For gruppering av felter i f.eks dødsbo (adressat, adresse, annet)
		const subGruppeArray = this._structureSubGruppe(item)
		return (
			<div className="subGruppe">
				<h4>{item.subKategori.navn}</h4>
				{subGruppeArray.map((subGruppe, idx) => {
					return (
						<div key={idx} className="subGruppe">
							<h4>{subGruppe.navn}</h4>
							<div className="items">
								{subGruppe.items.map(subitem => {
									return this.renderFieldComponent(subitem, formikProps.value)
								})}
							</div>
						</div>
					)
				})}
			</div>
		)
	}

	_structureSubGruppe = item => {
		let subGruppeArray = []
		item.items.map(subitem => {
			if (subGruppeArray.length < 1) {
				subGruppeArray.push({ navn: subitem.subGruppe, items: [subitem] })
			} else {
				let nyGruppe = true
				subGruppeArray.map(subGrupper => {
					if (subitem.subGruppe === subGrupper.navn) {
						subGrupper.items.push(subitem)
						nyGruppe = false
					}
				})
				nyGruppe && subGruppeArray.push({ navn: subitem.subGruppe, items: [subitem] })
			}
		})
		return subGruppeArray
	}

	renderFieldComponent = (item, valgteVerdier, parentObject, formikProps) => {
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

		if (item.id === 'spesreg') {
			return (
				<UtenFastBopelConnector
					key={item.key || item.id}
					item={item}
					valgteVerdier={valgteVerdier}
				/>
			)
		}

		if (item.id === 'ufb_kommunenr' || item.id.includes('utenFastBopel')) {
			return
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

	// manuellValidering = formikProps => {
	//Når flere felter blir lagt til på grunn av valg av andre felter må de legges til formik manuelt for å få validering
	// 	if (formikProps.errors.kontaktinformasjonForDoedsbo[0][item]) {

	//}
	// }

	renderFieldSubItem = (formikProps, item, subRad, parentId, idx, jdx) => {
		return (
			<Fragment>
				<div className="subitems-container">
					<h5 className="nummer">#{jdx + 1}</h5>
					{Object.keys(subRad).map(subKey => {
						return item.subItems.map(subsubItem => {
							const componentProps = this.extraComponentProps(subsubItem, formikProps.values, {
								parentId,
								idx
							})
							if (subsubItem.id === subKey) {
								const fakeItem = {
									...subsubItem,
									id: `${parentId}[${idx}]${item.id}[${jdx}]${subKey}`
								}
								const valgtVerdi = formikProps.values.arbeidsforhold[idx][item.id][jdx][subKey]
								const InputComponent = InputSelector(fakeItem.inputType)
								return (
									<Field
										key={fakeItem.key || fakeItem.id}
										name={fakeItem.id}
										label={fakeItem.label}
										component={InputComponent}
										size={fakeItem.size}
										validate={this.validationSub}
										//validate={this.validationFunction(valgtVerdi, fakeItem.inputType)}
										{...componentProps}
										{...fakeItem.inputTypeAttributes}
									/>
								)
							}
						})
					})}
				</div>
			</Fragment>
		)
	}

	// Ingvild: Forsøker å lage en mer presis validering
	// validationFunction = (valgtVerdi, inputType) => {
	// 	let error
	// 	switch (inputType) {
	// 		case 'date': {
	// 			if (valgtVerdi === '') {
	// 				error = 'Vennligst fyll inn dato'
	// 				return error
	// 			}
	// 			return
	// 		}
	// 		case 'select': {
	// 			if (valgtVerdi === '') {
	// 				error = 'Vennligst fyll inn'
	// 				return error
	// 			}
	// 			return
	// 		}
	// 	}
	// }
	validationSub = value => {
		let error
		if (value === '') {
			error = 'Vennligst fyll inn'
		}
		return error
	}

	validSwitch = item => {
		let error
		switch (item) {
			case 'permisjonOgPermittering': {
				if (value === '') {
					error = 'Fyll inn permisjonstype'
				}
			}
			default:
				error = 'Fyll inn'
		}
		return error
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
