export type Pageing = {
	page: number
	pageSize: number
}

export type Search = {
	pageing: Pageing
	tag: string
	excludeTag: string
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
	ident?: {
		ident?: string
	}
	utflyttingFraNorge?: {
		utflyttet?: boolean
	}
	innflyttingTilNorge?: {
		innflyttet?: boolean
	}
	identitet?: {
		falskIdentitet?: boolean
		utenlandskIdentitet?: boolean
	}
	barn?: {
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
		land?: string
	}
}
