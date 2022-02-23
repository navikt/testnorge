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
	foedsel?: Array<FoedselData>
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

export type FoedselData = {
	foedselsdato: string
	foedselsaar: number
	foedested: string
	foedekommune: string
	foedeland: string
	metadata: Metadata
}

type Statsborgerskap = {
	landkode: string
}

type Adressebeskyttelse = {
	gradering: string
}

export type Sivilstand = {
	type: string
	gyldigFraOgMed?: string
	relatertVedSivilstand: string
	bekreftelsesdato?: string
	id?: number
	sivilstandsdato?: string
	metadata: Metadata
}

export type Metadata = {
	historisk: boolean
}

export enum Rolle {
	BARN = 'BARN',
	MOR = 'MOR',
	FAR = 'FAR',
	FORELDER = 'FORELDER',
	MEDMOR = 'MEDMOR',
}

export type ForeldreBarnRelasjon = {
	id?: number
	minRolleForPerson: Rolle
	relatertPerson?: string
	relatertPersonsIdent: string
	relatertPersonsRolle: Rolle
}

export type DoedfoedtBarn = {
	id?: number
	dato: Date
}

export type Vergemaal = {
	vergemaalEmbete?: string
	embete?: string
	mandatType?: string
	sakType?: string
	type?: string
	gyldigFraOgMed: string
	gyldigTilOgMed: string
	vergeIdent?: string
	vergeEllerFullmektig?: {
		motpartsPersonident: string
	}
	id: number
}
