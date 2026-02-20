export const dollySoekSideStoerrelseLocalStorageKey = 'dollySoekSideStoerrelse'

const getInitialAntall = () =>
	Number(localStorage.getItem(dollySoekSideStoerrelseLocalStorageKey)) || 10

export const getDollySoekInitialValues = () => ({
	side: 0,
	antall: getInitialAntall(),
	seed: null,
	registreRequest: [],
	miljoer: [],
	personRequest: {
		ident: null,
		identtype: null,
		kjoenn: null,
		alderFom: null,
		alderTom: null,
		sivilstand: null,
		erLevende: false,
		erDoed: false,
		harBarn: false,
		harForeldre: false,
		harDoedfoedtBarn: false,
		harForeldreAnsvar: false,
		harVerge: false,
		harInnflytting: false,
		harUtflytting: false,
		harKontaktinformasjonForDoedsbo: false,
		harUtenlandskIdentifikasjonsnummer: false,
		harFalskIdentitet: false,
		harTilrettelagtKommunikasjon: false,
		harSikkerhetstiltak: false,
		harOpphold: false,
		statsborgerskap: null,
		personStatus: null,
		harNyIdentitet: false,
		harSkjerming: false,
		antallStatsborgerskap: null,
		antallRelasjoner: null,
		adresse: {
			addressebeskyttelse: null,
			kommunenummer: null,
			postnummer: null,
			bydelsnummer: null,
			harBydelsnummer: false,
			harUtenlandsadresse: false,
			harMatrikkeladresse: false,
			harUkjentAdresse: false,
			harDeltBosted: false,
			harBostedsadresse: false,
			harKontaktadresse: false,
			harOppholdsadresse: false,
			adressehistorikk: {
				antallBostedsadresser: null,
				antallKontaktadresser: null,
				antallOppholdsadresser: null,
			},
		},
	},
})

// export const dollySoekInitialValues = getDollySoekInitialValues()
