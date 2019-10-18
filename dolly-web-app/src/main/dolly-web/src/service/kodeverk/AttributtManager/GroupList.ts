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

const group = (list: Attributt[], groupKey: string, mapper: (x) => object, sortKey: string) => {
	return _flow(_groupBy(groupKey), _map(mapper), _sortBy(sortKey))(list)
}

export default { groupListByHovedKategori, groupList }
