import DataFormatter from '~/utils/DataFormatter'

//liste over koder som må eksludert pga ingen støtte i TPSF/dolly
const _excludeList = ['NULL', 'GLAD']

const _mapperList = {
	SPFO: 'KODE 7 - ',
	SPSF: 'KODE 6 - '
}

export const NormalizeTeamListForDropdown = ({ data }) => ({
	options: data.map(team => ({ value: team.id, label: team.navn }))
})

// Special treatment for sort
const SortSpraakArray = data => {
	const fav = ['ES', 'EN', 'NN', 'NB']
	const koderArray = data.koder
	for (var i = 0; i < koderArray.length - 1; i++) {
		const temp = koderArray[i]
		fav.includes(temp.value) && koderArray.splice(i, 1) && koderArray.unshift(temp)
	}

	console.log(koderArray, 'result')

	return koderArray
}

export const NormalizeKodeverkForDropdown = ({ data }) => {
	console.log(data, 'data')
	const sorterdArray = data.name == 'Språk' && SortSpraakArray(data)

	return {
		options: data.koder.filter(val => !_excludeList.includes(val.value)).map(kode => ({
			value: kode.value,
			label: _mapperList[kode.value] ? _mapperList[kode.value] + kode.label : kode.label
		}))
	}
}

export const NormalizeBrukerListForDropdown = (data, teamMembers) => {
	const options = data.reduce((filtered, bruker) => {
		if (!teamMembers.includes(bruker.navIdent)) {
			filtered.push({ value: bruker.navIdent, label: bruker.navIdent })
		}
		return filtered
	}, [])
	return { options }
}

export default {
	NormalizeTeamListForDropdown,
	NormalizeBrukerListForDropdown,
	NormalizeKodeverkForDropdown
}
