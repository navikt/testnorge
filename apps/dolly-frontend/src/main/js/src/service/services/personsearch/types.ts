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
			postnummer?: string
			kommunenummer?: string
		}
		kontaktadresse?: {
			norskAdresse?: boolean
			utenlandskAdresse?: boolean
			kontaktadresseForDoedsbo?: boolean
		}
		oppholdsadresse?: {
			norskAdresse?: boolean
			utenlandskAdresse?: boolean
			oppholdAnnetSted?: string
		}
	}
	personstatus?: {
		status?: string
	}
	relasjoner?: {
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
