import React, { PureComponent, Fragment } from 'react'
import { Field } from 'formik'
import { DollyApi } from '~/service/Api'
import Panel from '~/components/panel/Panel'
import InputSelector from '~/components/fields/InputSelector'
import FormEditorFieldArray from './FormEditorFieldArray'
import AutofillAddress from '~/components/autofillAddress/AutofillAddress'

import './FormEditor.less'

export default class FormEditor extends PureComponent {
	renderHovedKategori = ({ hovedKategori, items }, formikProps, closePanels) => {
		// !item.items[0].hasNoValue &&
		return (
			<Panel
				key={hovedKategori.id}
				heading={<h3>{hovedKategori.navn}</h3>}
				startOpen={!closePanels}
			>
				{console.log('items', items)}
				{items.map((item, idx) => {
					console.log('item', !item.items[0].hasNoValue)
					return this.renderFieldContainer(item, idx, formikProps)
				})}
			</Panel>
		)
	}

	renderFieldContainer = ({ subKategori, items }, uniqueId, formikProps) => {
		// TODO: Finn en bedre identifier på å skjule header hvis man er ett fieldArray
		const isAdresse = items[0].id === 'boadresse'
		return (
			<div className="subkategori" key={uniqueId}>
				{!items[0].items && !items[0].hasNoValue && <h4>{subKategori.navn}</h4>}
				<div className="subkategori-field-group">
					{items.map(
						item =>
							item.items
								? FormEditorFieldArray(item, formikProps, this.renderFieldComponent)
								: this.renderFieldComponent(item)
					)}
				</div>
				{isAdresse && <AutofillAddress formikProps={formikProps} />}
			</div>
		)
	}

	renderFieldComponent = item => {
		if (!item.inputType) return null
		const InputComponent = InputSelector(item.inputType)
		const componentProps = this.extraComponentProps(item)

		return (
			<Field
				key={item.id}
				name={item.id}
				label={item.label}
				component={InputComponent}
				size={item.size}
				{...componentProps}
			/>
		)
	}

	extraComponentProps = item => {
		switch (item.inputType) {
			case 'select': {
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

		return AttributtListe.map(
			hovedKategori =>
				// Ikke vis kategori som har default ikke-valgt radio button
				!hovedKategori.items[0].items[0].hasNoValue &&
				this.renderHovedKategori(hovedKategori, FormikProps, ClosePanels)
		)
	}
}
