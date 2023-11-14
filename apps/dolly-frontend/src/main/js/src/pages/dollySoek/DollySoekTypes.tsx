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
	typer: Array<string>
	personRequest: {
		sivilstand: string
		addressebeskyttelse: string
		harBarn: boolean
		harForeldre: boolean
		harDoedfoedtBarn: boolean
		harForeldreAnsvar: boolean
		harVerge: boolean
		harFullmakt: boolean
		harDoedsfall: boolean
		harInnflytting: boolean
		harUtflytting: boolean
		harKontaktinformasjonForDoedsbo: boolean
		harUtenlandskIdentifikasjonsnummer: boolean
		harFalskIdentitet: boolean
		harTilrettelagtKommunikasjon: boolean
		harSikkerhetstiltak: boolean
		harOpphold: boolean
		statsborgerskap: string
		nyIdentitet: boolean
		bostedsadresse: {
			kommunenummer: string
			postnummer: string
			bydelsnummer: string
			harUtenlandsadresse: boolean
			harMatrikkelAdresse: boolean
			harUkjentAdresse: boolean
		}
		harDeltBosted: boolean
		harKontaktadresse: boolean
		harOppholdsadresse: boolean
	}
}
