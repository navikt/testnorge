export type Pageing = {
	page: number
	pageSize: number
}

export type Search = {
	pageing: Pageing
	tag: string
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
}

export type Person = {
	ident: string
	fornavn?: string
	mellomnavn?: string
	etternavn?: string
	kjoenn?: string
	aktorId: string
	tag: string
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
