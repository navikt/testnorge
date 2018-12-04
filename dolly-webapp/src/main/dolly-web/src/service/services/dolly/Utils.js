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

// Special function for spraakvisning
const SortSpraakArray = data => {
	const fav = ['ES', 'EN', 'NN', 'NB']
	const koderArray = data.koder

	fav.forEach(value => {
		for (var i = 0; i < koderArray.length - 1; i++) {
			const temp = koderArray[i]
			// TODO: Fjern dette etter kodeverk har fjernet typo
			if (value == temp.value) {
				if (value == 'NB') temp.label = 'Norwegian, Bokmål'
				if (value == 'NN') temp.label = 'Norwegian, Nynorsk'
				koderArray.splice(i, 1) && koderArray.unshift(temp)
			}
		}
	})
	return koderArray
}

export const NormalizeKodeverkForDropdown = ({ data }) => {
	let sortedArray
	data.name == 'Språk' ? (sortedArray = SortSpraakArray(data)) : null

	if (sortedArray) {
		return {
			options: sortedArray.map(kode => ({
				value: kode.value,
				label: kode.label
			}))
		}
	}

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
