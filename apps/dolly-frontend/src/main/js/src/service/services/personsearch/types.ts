export type Pageing = {
	page: number
	pageSize: number
}

export type Search = {
	page: number
	pageSize: number
	terminateAfter: number
	tag: string
	excludeTags: Array<string>
	kjoenn?: string
	foedsel?: {
		fom?: string
		tom?: string
	}
	statsborgerskap?: {
		land?: string
	}
	sivilstand?: {
		type?: string
	}
	alder?: {
		fra?: number
		til?: number
	}
	identer?: Array<string>
	utflyttingFraNorge?: {
		utflyttet?: boolean
	}
	innflyttingTilNorge?: {
		innflytting?: boolean
	}
	identitet?: {
		falskIdentitet?: boolean
		utenlandskIdentitet?: boolean
	}
	relasjoner?: {
		barn?: boolean
		doedfoedtBarn?: boolean
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
	sivilstand?: {
		type?: string
	}
	statsborgerskap?: {
		land?: string[]
	}
}
