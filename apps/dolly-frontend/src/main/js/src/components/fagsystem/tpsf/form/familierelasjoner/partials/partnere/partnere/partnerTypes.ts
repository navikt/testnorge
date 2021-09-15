export enum Relasjonstyper {
	Partner = 'PARTNER',
}

export type Partner = {
	ny?: boolean
	sivilstander?: Array<Sivilstand>
	sivilstand?: string
	fornavn?: string
	etternavn?: string
	ident?: string
}
export type PersonHentet = {
	relasjoner?: Array<Relasjon>
}

export type Relasjon = {
	person: PersonHentet
	personRelasjonMed: Partner
	relasjonTypeNavn: string
}

export type Sivilstand = {
	ny?: boolean
	sivilstand?: string
	sivilstandRegdato?: string
}
