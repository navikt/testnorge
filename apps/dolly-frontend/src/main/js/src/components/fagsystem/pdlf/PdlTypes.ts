export type Person = {
	id: number
	person: PersonData
	relasjoner?: Array<Relasjon>
}

export type Relasjon = {
	id: number
	relasjonType: string
	relatertPerson: PersonData
}

export type PersonData = {
	master?: string
	ident?: string
	navn?: Array<Navn>
	kjoenn?: Array<Kjoenn>
	foedsel?: Array<Foedsel>
	statsborgerskap?: Array<Statsborgerskap>
	adressebeskyttelse?: Array<Adressebeskyttelse>
	sivilstand?: Array<Sivilstand>
	foreldreBarnRelasjon?: Array<ForeldreBarnRelasjon>
}

type Navn = {
	fornavn: string
	mellomnavn: string
	etternavn: string
}

type Kjoenn = {
	kjoenn: string
}

type Foedsel = {
	foedselsdato: string
}

type Statsborgerskap = {
	landkode: string
}

type Adressebeskyttelse = {
	gradering: string
}

export type Sivilstand = {
	relatertVedSivilstand: string
	id: number
	type: string
	sivilstandsdato?: string
	gyldigFraOgMed?: string
	bekreftelsesdato?: string
}

export type ForeldreBarnRelasjon = {
	id: number
	minRolleForPerson: string
	relatertPerson: string
	relatertPersonsIdent: string
	relatertPersonsRolle: string
}
