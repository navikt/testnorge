import { AttributtGruppe, AttributtGruppeHovedKategori, Attributt } from './Types'
import * as yup from 'yup'
import { FormikValues } from 'formik'
import AttributtListe from './Attributter'
import { groupList, groupListByHovedKategori } from './GroupList'
import _set from 'lodash/set'
import _get from 'lodash/get'

export default class AttributtManager {
	listSelected(selectedIds: string[]): Attributt[] {
		return AttributtListe.filter(f => selectedIds.includes(f.id))
	}

	searchList(list: Attributt[], searchTerm: string): Attributt[] {
		return searchTerm
			? list.filter(f => f.label.toLowerCase().includes(searchTerm.toLowerCase()))
			: list
	}

	listAllByGroup(searchTerm: string): AttributtGruppe[] {
		const list = this.searchList(AttributtListe, searchTerm)
		return groupList(list)
	}

	listSelectedByGroup(selectedIds: string[], searchTerm: string): AttributtGruppe[] {
		let list = this.searchList(AttributtListe, searchTerm)
		if (selectedIds.length > 0) list = list.filter(f => selectedIds.includes(f.id))
		return groupList(list)
	}

	listSelectedByHovedKategori(selectedIds: string[]): AttributtGruppeHovedKategori[] {
		return groupListByHovedKategori(this.listSelected(selectedIds))
	}

	listEditable(): AttributtGruppe[] {
		return groupList(AttributtListe.filter(attr => attr.kanRedigeres))
	}

	getInitialValuesForEditableItems(values: Object): Object {
		const editableAttributes = AttributtListe.filter(attr => attr.kanRedigeres)

		return editableAttributes.reduce((prev, item) => {
			return _set(prev, item.id, _get(values, item.id))
		}, {})
	}

	getValidations(selectedIds: string[]): yup.MixedSchema {
		// Get all selected attributes that has validations
		const list = this.listSelected(selectedIds).filter(s => s.validation)

		// Reduce to item.id and validation to create a validation object
		const validationObject = list.reduce(
			(prev, { id, validation }) => ({
				...prev,
				[id]: validation
			}),
			{}
		)

		return yup.object(validationObject)
	}

	getInitialValues(selectedIds: string[], values: object): FormikValues {
		// Setter alle id'er til default value empty string
		// Formik krever at form'et har initialValues
		return selectedIds.reduce((prev, curr) => ({ ...prev, [curr]: values[curr] || '' }), {})
	}
}
