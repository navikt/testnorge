import React, { PureComponent, Fragment } from 'react'
import { Field } from 'formik'
import _intersection from 'lodash/intersection'
import { DollyApi } from '~/service/Api'
import { AttributtType } from '~/service/kodeverk/AttributtManager/Types'
import Panel from '~/components/panel/Panel'
import InputSelector from '~/components/fields/InputSelector'
import FormEditorFieldArray from './FormEditorFieldArray'
import AutofillAddress from '~/components/autofillAddress/AutofillAddress'

import './FormEditor.less'

export default class FormEditor extends PureComponent {
	renderHovedKategori = ({ hovedKategori, items }, formikProps, closePanels) => {
		const { getAttributtListByHovedkategori } = this.props
		const hovedKategoriAttributes = getAttributtListByHovedkategori(hovedKategori)
		const errorIds = Object.keys(formikProps.errors)
		const touchedIds = Object.keys(formikProps.touched)
		const errorsInHovedkategori = _intersection(hovedKategoriAttributes, errorIds)
		const intersectTouchedAndError = _intersection(errorsInHovedkategori, touchedIds)
		return (
			<Panel
				key={hovedKategori.id}
				heading={<h3>{hovedKategori.navn}</h3>}
				startOpen={!closePanels}
				errors={intersectTouchedAndError.length > 0}
			>
				{items.map((item, idx) => {
					return this.renderFieldContainer(item, idx, formikProps)
				})}
			</Panel>
		)
	}

	renderFieldContainer = ({ subKategori, items }, uniqueId, formikProps) => {
		// TODO: Finn en bedre identifier på å skjule header hvis man er ett fieldArray
		const isAdresse = 'boadresse' === (items[0].parent || items[0].id)
		const isFieldarray = Boolean(items[0].items)
		return (
			<div className="subkategori" key={uniqueId}>
				{!isFieldarray && <h4>{subKategori.navn}</h4>}
				<div className="subkategori-field-group">
					{items.map(
						item =>
							isFieldarray
								? FormEditorFieldArray(
										item,
										formikProps,
										this.renderFieldComponent,
										this.props.editMode
								  )
								: this.renderFieldComponent(item, formikProps.values)
					)}
				</div>
				{isAdresse && <AutofillAddress formikProps={formikProps} />}
			</div>
		)
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
			return (
				<div className="skjemaelement" key={item.key || item.id}>
					<label className="skjemaelement__label">{item.label}</label>
					<div className="skjemaelement__staticvalue">{valgtVerdi}</div>
				</div>
			)
		}

		return (
			<Field
				key={item.key || item.id}
				name={item.id}
				label={item.label}
				component={InputComponent}
				size={item.size}
				{...componentProps}
			/>
		)
	}

	extraComponentProps = (item, valgteVerdier, parentObject) => {
		switch (item.inputType) {
			case 'select': {
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
					return {
						loadOptions: () =>
							DollyApi.getKodeverkByNavn(item.apiKodeverkId).then(
								DollyApi.Utils.NormalizeKodeverkForDropdown
							)
					}
				} else {
					return {
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

	render() {
		const { AttributtListe, FormikProps, ClosePanels } = this.props

		return AttributtListe.map(hovedKategori =>
			// Ikke vis kategori som har default ikke-valgt radio button
			this.renderHovedKategori(hovedKategori, FormikProps, ClosePanels)
		)
	}
}
