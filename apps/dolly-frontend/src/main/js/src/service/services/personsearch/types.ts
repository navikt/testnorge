export type Search = {
	page: number
	pageSize: number
	terminateAfter: number
	tag: string
	excludeTags: Array<string>
	kunLevende?: boolean
	kjoenn?: string
	foedsel?: {
		fom?: string
		tom?: string
	}
	nasjonalitet?: {
		statsborgerskap?: string
		utflyttingFraNorge?: boolean
		innflyttingTilNorge?: boolean
	}
	sivilstand?: {
		type?: string
	}
	alder?: {
		fra?: number
		til?: number
	}
	identer?: Array<string>
	identifikasjon?: {
		identtype?: string
		adressebeskyttelse?: string
		falskIdentitet?: boolean
		utenlandskIdentitet?: boolean
	}
	adresser?: {
		bostedsadresse?: {
			borINorge?: string
			postnummer?: string
			kommunenummer?: string
		}
		harUtenlandskAdresse?: string
		harKontaktadresse?: string
		harOppholdsadresse?: string
	}
	personstatus?: {
		status?: string
	}
	relasjoner?: {
		barn?: string
		harBarn?: string
		harDoedfoedtBarn?: string
		forelderBarnRelasjoner?: string[]
	}
}

export type Person = {
	ident: string
	fornavn?: string
	mellomnavn?: string
	etternavn?: string
	kjoenn?: string
	aktorId: string
	tags: string[]
	foedsel?: {
		foedselsdato?: string
	}
	doedsfall?: {
		doedsdato?: string
	}
	sivilstand?: {
		type?: string
	}
	statsborgerskap?: {
		land?: string[]
	}
	folkeregisterpersonstatus?: [Personstatus]
}

type Personstatus = {
	status?: string
	gyldighetstidspunkt?: string
}
