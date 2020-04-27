export type FormikPartner = {
	ny: boolean
	data: FormikPartnerData
}

type FormikPartnerData = {
	sivilstander?: Array<FormikSivilstandObj>
	fornavn?: string
	etternavn?: string
	ident?: string
}

export type FormikSivilstandObj = {
	ny: boolean
	data: SivilstandObj
}

export type Partner = {
	sivilstander?: Array<SivilstandObj>
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

export type SivilstandObj = {
	sivilstand?: string
	sivilstandRegdato?: string
}
