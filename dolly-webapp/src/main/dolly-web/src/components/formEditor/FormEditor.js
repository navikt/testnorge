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
		return (
			<Panel
				key={hovedKategori.id}
				heading={<h3>{hovedKategori.navn}</h3>}
				startOpen={!closePanels}
			>
				{items.map((item, idx) => this.renderFieldContainer(item, idx, formikProps))}
			</Panel>
		)
	}

	renderFieldContainer = ({ subKategori, items }, uniqueId, formikProps) => {
		// TODO: Finn en bedre identifier på å skjule header hvis man er ett fieldArray
		const isAdresse = items[0].id === 'boadresse'
		return (
			<div className="subkategori" key={uniqueId}>
				{!items[0].items && <h4>{subKategori.navn}</h4>}
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
		console.log('item', item)
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

		return AttributtListe.map(hovedKategori =>
			this.renderHovedKategori(hovedKategori, FormikProps, ClosePanels)
		)
	}
}
