//liste over koder som må eksludert pga ingen støtte i TPSF/dolly
const _excludeList = ['NULL', 'GLAD']

// Specialbehov for modifisering og sortering av kodeverk
export const SortKodeverkArray = data => {
	const kodeverk = data.koder
	if (data.name == 'Språk') {
		const spesKoder = ['ES', 'EN', 'NN', 'NB']

		spesKoder.forEach(value => {
			for (var i = 0; i < kodeverk.length - 1; i++) {
				const temp = kodeverk[i]
				if (value == temp.value) {
					if (value == 'NB') temp.label = 'Norwegian, Bokmål'
					if (value == 'NN') temp.label = 'Norwegian, Nynorsk'
					kodeverk.splice(i, 1) && kodeverk.unshift(temp)
				}
			}
		})
	}

	if (data.name == 'Diskresjonskoder') {
		return kodeverk
			.map(kode => {
				if (kode.value === 'SPFO') {
					kode.label = 'KODE 7 - Sperret adresse, fortrolig'
				}
				if (kode.value === 'SPSF') {
					kode.label = 'KODE 6 - Sperret adresse, strengt fortrolig'
				}
				return kode
			})
			.filter(kode => kode.value !== 'UFB')
	}

	if (data.name === 'Postnummer' || data.name === 'Kommuner') {
		return kodeverk.map(kode => ({
			label: `${kode.value} - ${kode.label}`,
			value: kode.value,
			data: kode.label
		}))
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
		spesKoder.map(yrke => kodeverk.unshift(yrke))
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
		return kodeverk.filter(kode => !spesKoder.includes(kode.value))
	}

	return kodeverk
}
