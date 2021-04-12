// Specialbehov for modifisering og sortering av kodeverk
export const SortKodeverkArray = data => {
	const kodeverk = data.koder
	if (data.name === 'Språk') {
		const spesKoder = ['ES', 'EN', 'NN', 'NB']

		spesKoder.forEach(value => {
			for (let i = 0; i < kodeverk.length - 1; i++) {
				const temp = kodeverk[i]
				if (value == temp.value) {
					if (value == 'NB') temp.label = 'Norwegian, Bokmål'
					if (value == 'NN') temp.label = 'Norwegian, Nynorsk'
					kodeverk.splice(i, 1) && kodeverk.unshift(temp)
				}
			}
		})
	}

	if (data.name === 'Sivilstander') {
		//Dolly støtter ikke GLAD fordi det er NAV-spesifikt og ikke SKD. Kan endres ved behov.
		return kodeverk.filter(kode => kode.value !== 'NULL' && kode.value !== 'GLAD')
	}

	if (data.name === 'Diskresjonskoder') {
		const diskresjonskoder = [
			...kodeverk,
			{ label: 'KODE 19 - Strengt fortrolig utland', value: 'SFU' }
		]
		return diskresjonskoder
			.map(kode => {
				if (kode.value === 'SPFO') {
					kode.label = 'KODE 07 - Sperret adresse, fortrolig'
				}
				if (kode.value === 'SPSF') {
					kode.label = 'KODE 06 - Sperret adresse, strengt fortrolig'
				}
				return kode
			})
			.filter(kode => kode.value !== 'UFB')
			.sort((first, second) => (first.label > second.label ? 1 : -1))
	}

	if (
		data.name === 'Postnummer' ||
		data.name === 'Postnummer vegadresser' ||
		data.name === 'Kommuner' ||
		data.name === 'Næringskoder' ||
		data.name === 'Sektorkoder'
	) {
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

	if (data.name === 'Arbeidsforholdstyper') {
		// Kodeverket for arbeidsforholdstyper har en type som AAREG per i dag ikke støtter
		const arbeidsforhold = kodeverk
			.filter(kode => kode.value !== 'pensjonOgAndreTyperYtelserUtenAnsettelsesforhold')
			.map(kode => {
				if (kode.value === 'frilanserOppdragstakerHonorarPersonerMm') {
					return {
						...kode,
						label: 'Frilansere/oppdragstakere, honorar, m.m.'
					}
				}
				return kode
			})
		return arbeidsforhold
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

	if (data.name === 'NAVSkjema') {
		return kodeverk.map(kode => ({
			label: `${kode.value}: ${kode.label}`,
			value: kode.value,
			data: kode.label
		}))
	}

	if (data.name === 'Tema') {
		const ugyldigeKoder = ['BII', 'KLA', 'KNA', 'KOM', 'LGA', 'MOT', 'OVR']
		return kodeverk.filter(kode => !ugyldigeKoder.includes(kode.value))
	}

	if (data.name === 'Vergemål_Mandattype') {
		return kodeverk.filter(kode => kode.value !== '<Blank>' && kode.value !== 'ADP')
	}

	if (data.name === 'EnhetstyperJuridiskEnhet' || data.name === 'EnhetstyperVirksomhet') {
		return kodeverk.map(kode => ({
			label: `${kode.label} (${kode.value})`,
			value: kode.value
		}))
	}

	return kodeverk
}
