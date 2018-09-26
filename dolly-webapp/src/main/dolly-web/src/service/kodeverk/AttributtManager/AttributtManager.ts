import { AttributtGruppe, AttributtGruppeHovedKategori, Attributt } from './Types'
import * as yup from 'yup'
import { FormikValues } from 'formik'
import AttributtListe from './Attributter'
import { groupList, groupListByHovedKategori } from './GroupList'
import _set from 'lodash/set'
import _get from 'lodash/get'
import _mapValues from 'lodash/mapValues'

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

	getValidations(selectedIds: string[]): yup.MixedSchema {
		// Get all selected attributes that has validations
		const list = this.listSelected(selectedIds).filter(s => s.validation)

		// Reduce to item.id and validation to create a validation object
		const validationObject = list.reduce((prev, currentObject) => {
			if (currentObject.inputType === 'multifield') {
				const nestedValidation = currentObject.items.reduce((prevNestedItem, nestedItem) => {
					if (nestedItem.validation) {
						const idArray = nestedItem.id.split('.')
						const fieldId = idArray[idArray.length - 1]
						return prevNestedItem.shape({ [fieldId]: nestedItem.validation })
					}

					return prevNestedItem
				}, yup.object())

				//TODO: FINN MER GENERISK MÅTE Å LØSE DETTE PÅ
				const idList = currentObject.id.split('.')
				let completeValidation = { [idList[0]]: nestedValidation }
				if (idList.length > 1) {
					completeValidation[idList[0]] = yup.object().shape({ barn: nestedValidation })
				}

				return { ...prev, ...completeValidation }
			}

			return {
				...prev,
				[currentObject.id]: currentObject.validation
			}
		}, {})

		console.log(validationObject)
		return yup.object().shape(validationObject)
	}

	getInitialValues(selectedIds: string[], values: object): FormikValues {
		const list = this.listSelected(selectedIds)

		return this._getListOfInitialValues(list, values)
	}

	getInitialValuesForEditableItems(values: object): FormikValues {
		const editableAttributes = AttributtListe.filter(attr => attr.kanRedigeres)

		return this._getListOfInitialValues(editableAttributes, values)
	}

	_getListOfInitialValues(list, values) {
		return list.reduce((prev, item) => {
			if (item.inputType === 'multifield') {
				const nestedInitialValues = item.items.reduce((prevNestedItem, nestedItem) => {
					return this._setInitialValue(prevNestedItem, nestedItem.id, values)
				}, {})

				return {
					...prev,
					...nestedInitialValues
				}
			}

			return this._setInitialValue(prev, item.id, values)
		}, {})
	}
	_setInitialValue(currentObject, itemId, stateValues) {
		let initialValue = ''
		const fromState = _get(stateValues, itemId)
		if (fromState) initialValue = fromState

		return _set(currentObject, itemId, initialValue)
	}
}
