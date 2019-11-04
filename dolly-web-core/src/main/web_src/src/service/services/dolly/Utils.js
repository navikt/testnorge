//liste over koder som må eksludert pga ingen støtte i TPSF/dolly
const _excludeList = ['NULL', 'GLAD']

// Specialbehov for modifisering og sortering av kodeverk
export const SortKodeverkArray = data => {
	const koderArray = data.koder
	if (data.name == 'Språk') {
		const spesKoder = ['ES', 'EN', 'NN', 'NB']

		spesKoder.forEach(value => {
			for (var i = 0; i < koderArray.length - 1; i++) {
				const temp = koderArray[i]
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

	if (data.name === 'Yrker') {
		// Noen utvalgte yrker der koden fra yrkeskodeverk tilsvarer STYRK-kode
		const spesKoder = [
			{ value: '3231109', label: 'SYKEPLEIER' },
			{ value: '7233108', label: 'SPESIALARBEIDER (LANDBRUKS- OG ANLEGGSMASKINMEKANIKK)' },
			{ value: '7421118', label: 'SNEKKER' },
			{ value: '2320102', label: 'LÆRER (VIDEREGÅENDE SKOLE)' },
			{ value: '7216108', label: 'KAMMEROPERATØR' },
			{ value: '2310114', label: 'HØYSKOLELÆRER' },
			{ value: '5141103', label: 'FRISØR' },
			{ value: '7125102', label: 'BYGNINGSSNEKKER' },
			{ value: '5221126', label: 'BUTIKKMEDARBEIDER' },
			{ value: '7217102', label: 'BILSKADEREPARATØR' },
			{ value: '3310101', label: 'ALLMENNLÆRER' },
			{ value: '2521106', label: 'ADVOKAT' }
		]
		spesKoder.map(yrke => koderArray.unshift(yrke))
	}

	if (data.name === 'Landkoder') {
		// Filtrerer bort land som har begrenset gyldighet. Fjernes hvis det oppstår behov for test med nye land.
		const spesKoder = [
			'ATF',
			'BES',
			'CUW',
			'JEY',
			'MNE',
			'SCG',
			'SDN',
			'SRB',
			'SXM',
			'TLS',
			'WAK',
			'YUG'
		]
		return koderArray.filter(kode => !spesKoder.includes(kode.value))
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

export const NormalizeKodeverkForDropdownUtenUfb = ({ data }, showValueInLabel) => {
	const sortedArray = SortKodeverkArray(data)
	let filteredSortedArray = []
	sortedArray.map(
		diskresjonskode =>
			diskresjonskode.value !== 'UFB' &&
			diskresjonskode.value !== 'SPSF' &&
			filteredSortedArray.push(diskresjonskode)
	)
	return {
		options: filteredSortedArray.filter(val => !_excludeList.includes(val.value)).map(kode => ({
			value: kode.value,
			label: showValueInLabel ? kode.value + ' - ' + kode.label : kode.label
		}))
	}
}

export const NormalizeBrukerListForDropdown = data => {
	const options = data.reduce((filtered, bruker) => {
		return filtered
	}, [])
	return { options }
}

export default {
	NormalizeBrukerListForDropdown,
	NormalizeKodeverkForDropdown,
	NormalizeKodeverkForDropdownUtenUfb
}
