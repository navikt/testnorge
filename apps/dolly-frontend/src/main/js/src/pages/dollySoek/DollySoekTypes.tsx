export type ResponseIdenter = {
	data: {
		totalHits: number
		pageNumber: number
		pageSize: number
		windowSize: number
		identer: Array<string>
		error: string
	}
}

export type SoekRequest = {
	registreRequest: Array<string>
	personRequest: {
		identtype: string
		kjoenn: string
		alderFom: number
		alderTom: number
		sivilstand: string
		erLevende: boolean
		erDoed: boolean
		harBarn: boolean
		harForeldre: boolean
		harDoedfoedtBarn: boolean
		harForeldreAnsvar: boolean
		harVerge: boolean
		harInnflytting: boolean
		harUtflytting: boolean
		harKontaktinformasjonForDoedsbo: boolean
		harUtenlandskIdentifikasjonsnummer: boolean
		harFalskIdentitet: boolean
		harTilrettelagtKommunikasjon: boolean
		harSikkerhetstiltak: boolean
		harOpphold: boolean
		statsborgerskap: string
		personStatus: string
		harNyIdentitet: boolean
		adresse: {
			addressebeskyttelse: string
			kommunenummer: string
			postnummer: string
			bydelsnummer: string
			harBydelsnummer: boolean
			harUtenlandsadresse: boolean
			harMatrikkeladresse: boolean
			harUkjentAdresse: boolean
			harDeltBosted: boolean
			harBostedsadresse: boolean
			harKontaktadresse: boolean
			harOppholdsadresse: boolean
		}
	}
}
