import { Attributt, AttributtGruppe, AttributtGruppeHovedKategori } from './Types'
import _flow from 'lodash/fp/flow'
import _groupBy from 'lodash/fp/groupBy'
import _map from 'lodash/fp/map'
import _sortBy from 'lodash/fp/sortBy'
import _reduce from 'lodash/fp/reduce'
import _chain from 'lodash/fp/chain'

export const groupListByHovedKategori = (list: Attributt[]): AttributtGruppeHovedKategori[] => {
	const outerMapper = x => ({ hovedKategori: x[0].hovedKategori, items: x })
	return group(list, 'hovedKategori.navn', outerMapper, 'hovedKategori.order')
}

export const groupList = (list: Attributt[]): AttributtGruppe[] => {
	const innerMapper = y => ({ subKategori: y[0].subKategori, items: y })
	const outerMapper = x => ({
		hovedKategori: x[0].hovedKategori,
		items: group(x, 'subKategori.navn', innerMapper, 'subKategori.order')
	})
	return group(list, 'hovedKategori.navn', outerMapper, 'hovedKategori.order')
}

//TODO: Refactor
export const groupListByParent = (list: Attributt[]) => {
	const parentMapper = x => {
		const firstItem = x[0]
		const isMultiple = firstItem.subKategori && firstItem.subKategori.multiple
		if (isMultiple) {
			const { id, parent, path, options, inputType, label, ...restProps } = firstItem
			//TEMP
			console.log('path', path)
			return {
				id: parent,
				multiple: true,
				...restProps,
				path: path.substring(0, path.lastIndexOf('.')),
				items: x
			}
		}

		return x[0]
	}

	return _flow(_groupBy('parent'), _map(parentMapper))(list)
}

const group = (list: Attributt[], groupKey: string, mapper: (x) => object, sortKey: string) => {
	return _flow(_groupBy(groupKey), _map(mapper), _sortBy(sortKey))(list)
}

export default { groupListByHovedKategori, groupList }
