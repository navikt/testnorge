import { AttributtGruppe, AttributtGruppeHovedKategori, Attributt } from './Types'
import * as yup from 'yup'
import { FormikValues } from 'formik'
import AttributtListe from './Attributter'
import { groupList, groupListByHovedKategori, groupListByParent } from './GroupList'
import _set from 'lodash/set'
import _get from 'lodash/get'
import _mapValues from 'lodash/mapValues'

export default class AttributtManager {
	listSelectedWithChildNodes(selectedIds: string[]): Attributt[] {
		return AttributtListe.filter(f => {
			if (f.inputType === 'multifield') return false
			if (f.parent) {
				if (selectedIds.includes(f.parent)) return true

				return false
			}
			return selectedIds.includes(f.id)
		})
	}

	//TODO: FIX
	listSelectedGroupedByParent(selectedIds: string[]) {
		return groupListByParent(
			AttributtListe.filter(f => {
				if (f.inputType === 'multifield') return false
				if (f.parent) {
					if (selectedIds.includes(f.parent)) return true

					return false
				}
				return selectedIds.includes(f.id)
			})
		)
	}

	listSelectedWithParents(selectedIds: string[]): Attributt[] {
		return AttributtListe.filter(f => !f.parent && selectedIds.includes(f.id))
	}

	searchListWithParents(list: Attributt[], searchTerm: string): Attributt[] {
		return searchTerm
			? list.filter(f => !f.parent && f.label.toLowerCase().includes(searchTerm.toLowerCase()))
			: list.filter(f => !f.parent)
	}

	listAllByGroup(searchTerm: string): AttributtGruppe[] {
		const list = this.searchListWithParents(AttributtListe, searchTerm)
		return groupList(list)
	}

	listSelectedAttributesForValueSelection(selectedIds: string[]): AttributtGruppe[] {
		// multifield consist of several fields - only children fields should be rendered
		let list = AttributtListe.filter(f => f.inputType !== 'multifield')
		if (selectedIds.length > 0) {
			list = list.filter(f => {
				if (f.parent) {
					if (selectedIds.includes(f.parent)) return true
					return false
				}

				return selectedIds.includes(f.id)
			})
		}
		return groupList(list)
	}

	listSelectedByHovedKategori(selectedIds: string[]): AttributtGruppeHovedKategori[] {
		return groupListByHovedKategori(this.listSelectedWithParents(selectedIds))
	}

	listSelectedByHovedKategoriWithChildNodes(selectedIds: string[]): AttributtGruppe[] {
		return groupList(this.listSelectedWithChildNodes(selectedIds))
	}

	listEditable(): AttributtGruppe[] {
		return groupList(AttributtListe.filter(attr => attr.kanRedigeres))
	}

	//TODO: CLEANUP
	getValidations(selectedIds: string[]): yup.MixedSchema {
		// Get all selected attributes that has validations
		const list = this.listSelectedWithChildNodes(selectedIds).filter(s => s.validation)
		const groupedList = groupListByParent(list)
		// Reduce to item.id and validation to create a validation object
		const validationObject = groupedList.reduce((prev, currentObject) => {
			if (currentObject.multiple) {
				const mapItemsToObject = this._mapArrayToObjectWithValidation(currentObject.items)
				return {
					...prev,
					[currentObject.id]: yup.array().of(yup.object(mapItemsToObject))
				}
			}
			return {
				...prev,
				[currentObject.id]: currentObject.validation
			}
		}, {})

		return yup.object().shape(validationObject)
	}

	getInitialValues(selectedIds: string[], values: object): FormikValues {
		const list = this.listSelectedWithChildNodes(selectedIds)
		const groupedList = groupListByParent(list)
		return this._getListOfInitialValues(groupedList, values)
	}

	getInitialValuesForEditableItems(values: object): FormikValues {
		const editableAttributes = AttributtListe.filter(attr => attr.kanRedigeres)

		return this._getListOfInitialValues(editableAttributes, values)
	}

	//TODO: CLEANUP
	_getListOfInitialValues(list, values) {
		return list.reduce((prev, item) => {
			if (item.multiple) {
				const mapItemsToObject = this._mapArrayToObjectWithEmptyValues(item.items)
				return this._setInitialArrayValue(prev, item.id, values, [mapItemsToObject])
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

	_setInitialArrayValue(currentObject, itemId, stateValues, array) {
		let initialValue = array
		const fromState = _get(stateValues, itemId)
		if (fromState) initialValue = fromState

		return _set(currentObject, itemId, initialValue)
	}
	//TODO: CLEANUP
	_mapArrayToObjectWithEmptyValues = list => {
		return list.reduce((accumulator, item) => {
			return _set(accumulator, item.id, '')
		}, {})
	}

	_mapArrayToObjectWithValidation = list => {
		return list.reduce((accumulator, item) => {
			return _set(accumulator, item.id, item.validation)
		}, {})
	}
}
