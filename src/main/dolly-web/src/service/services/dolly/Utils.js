import DataFormatter from '~/utils/DataFormatter'

//liste over koder som må eksludert pga ingen støtte i TPSF/dolly
const _excludeList = ['NULL', 'GLAD']

export const NormalizeTeamListForDropdown = ({ data }) => ({
	options: data.map(team => ({ value: team.id, label: team.navn }))
})

// Specialbehov for modifisering og sortering av kodeverk
export const SortKodeverkArray = data => {
	const koderArray = data.koder
	if (data.name == 'Språk') {
		const spesKoder = ['ES', 'EN', 'NN', 'NB']

		spesKoder.forEach(value => {
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
	}

	if (data.name == 'Diskresjonskoder') {
		const spesKoder = [
			{ value: 'SPFO', label: 'KODE 7 - Sperret adresse, fortrolig' },
			{ value: 'SPSF', label: 'KODE 6 - Sperret adresse, strengt fortrolig' }
		]

		spesKoder.forEach(kode => {
			for (var i = 0; i < koderArray.length - 1; i++) {
				const temp = koderArray[i]
				if (kode.value == temp.value) {
					temp.label = kode.label
					koderArray.splice(i, 1) && koderArray.unshift(temp)
				}
			}
		})
	}

	return koderArray
}

export const NormalizeKodeverkForDropdown = ({ data }, showValueInLabel) => {
	const sortedArray = SortKodeverkArray(data)

	return {
		options: sortedArray.filter(val => !_excludeList.includes(val.value)).map(kode => ({
			value: kode.value,
			label: showValueInLabel ? kode.value + ' - ' + kode.label : kode.label
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
