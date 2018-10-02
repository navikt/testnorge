import { AttributtGruppe, AttributtGruppeHovedKategori, Attributt } from './Types'
import * as yup from 'yup'
import { FormikValues } from 'formik'
import AttributtListe from './Attributter'
import { groupList, groupListByHovedKategori, groupListByParent } from './GroupList'
import _set from 'lodash/set'
import _get from 'lodash/get'
import _mapValues from 'lodash/mapValues'

export default class AttributtManager {
	// BASE FUNCTIONS
	listAllSelected(selectedIds: string[]): Attributt[] {
		return AttributtListe.filter(f => selectedIds.includes(f.parent || f.id))
	}

	listSelectedExcludingParent(selectedIds: string[]): Attributt[] {
		return AttributtListe.filter(f => {
			if (f.harBarn) return false
			return selectedIds.includes(f.parent || f.id)
		})
	}

	listAllExcludingChildren(): Attributt[] {
		return AttributtListe.filter(f => !f.parent)
	}

	listSelectedExcludingChildren(selectedIds: string[]): Attributt[] {
		return AttributtListe.filter(f => !f.parent && selectedIds.includes(f.id))
	}

	//STEP 1
	listSelectableAttributes(searchTerm: string): AttributtGruppe[] {
		const list = this.listAllExcludingChildren()
		return groupList(
			searchTerm ? list.filter(f => f.label.toLowerCase().includes(searchTerm.toLowerCase())) : list
		)
	}

	listUtvalg(selectedIds: string[]): AttributtGruppeHovedKategori[] {
		return groupListByHovedKategori(this.listSelectedExcludingChildren(selectedIds))
	}

	//STEP 2 + 3
	listSelectedAttributesForValueSelection(selectedIds: string[]): AttributtGruppe[] {
		return groupList(this.listAllSelected(selectedIds))
	}

	listEditable(): AttributtGruppe[] {
		return groupList(AttributtListe.filter(attr => attr.kanRedigeres))
	}

	getValidations(selectedIds: string[]): yup.MixedSchema {
		// Get all selected attributes that has validations
		const list = this.listAllSelected(selectedIds).filter(s => s.validation)
		// Reduce to item.id and validation to create a validation object
		const validationObject = list.reduce((accumulator, currentObject) => {
			if (currentObject.items) {
				const mapItemsToObject = this._mapArrayToObjectWithValidation(currentObject.items)
				return {
					...accumulator,
					[currentObject.id]: yup.array().of(yup.object().shape(mapItemsToObject))
				}
			}
			return _set(accumulator, currentObject.id, currentObject.validation)
		}, {})
		return yup.object().shape(validationObject)
	}

	getInitialValues(selectedIds: string[], values: object): FormikValues {
		return this._getListOfInitialValues(this.listAllSelected(selectedIds), values)
	}

	getInitialValuesForEditableItems(values: object): FormikValues {
		const editableAttributes = AttributtListe.filter(attr => attr.kanRedigeres)

		return this._getListOfInitialValues(editableAttributes, values)
	}

	_getListOfInitialValues(list, values) {
		return list.reduce((prev, item) => {
			// Array
			if (item.items) {
				const mapItemsToObject = this._mapArrayToObjectWithEmptyValues(item.items)
				return this._setInitialArrayValue(prev, item.id, values, [mapItemsToObject])
			}
			// Flattened object -> Ignore parent that has no inputType
			if (!item.inputType) return prev

			// Initvalue based on key-value
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
