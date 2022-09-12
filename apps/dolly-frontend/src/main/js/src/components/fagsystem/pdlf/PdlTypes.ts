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
	statsborgerskap?: Array<StatsborgerskapData>
	adressebeskyttelse?: Array<AdressebeskyttelseData>
	sivilstand?: Array<Sivilstand>
	foreldreBarnRelasjon?: Array<ForeldreBarnRelasjon>
	foreldreansvar?: Array<Foreldreansvar>
	innflytting?: Array<Innflytting>
	utflytting?: Array<Utflytting>
	vergemaal?: Array<Vergemaal>
	doedsfall?: Array<DoedsfallData>
}

export type PersonUtenIdData = {
	foedselsdato?: string
	kjoenn?: string
	navn?: Navn
	statsborgerskap: string
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
	id?: number
}

export type DoedsfallData = {
	doedsdato: string
	metadata: Metadata
	id?: number
}

export type StatsborgerskapData = {
	landkode: string
	gyldigFraOgMed: string
	gyldigTilOgMed: string
	id?: number
}

export type Innflytting = {
	fraflyttingsland: string
	fraflyttingsstedIUtlandet: string
	innflyttingsdato: string
	id?: number
}

export type Utflytting = {
	tilflyttingsland: string
	tilflyttingsstedIUtlandet: string
	utflyttingsdato: string
	id?: number
}

export type AdressebeskyttelseData = {
	gradering: string
	id?: number
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

export enum TypeAnsvarlig {
	EKSISTERENDE = 'EKSISTERENDE',
	UTEN_ID = 'UTEN_ID',
	NY = 'NY',
}

export type ForeldreBarnRelasjon = {
	id?: number
	minRolleForPerson: Rolle
	relatertPerson?: string
	relatertPersonUtenFolkeregisteridentifikator?: PersonUtenIdData
	nyRelatertPerson?: NyIdent
	relatertPersonsIdent: string
	relatertPersonsRolle: Rolle
	deltBosted?: any
	typeForelderBarn?: string
}

export type DoedfoedtBarn = {
	id?: number
	dato: Date
}

export type Foreldreansvar = {
	ansvar: string
	ansvarlig: string
	ansvarligUtenIdentifikator: ForeldreansvarUtenId
	metadata?: Metadata
}

export type ForeldreansvarUtenId = {
	foedselsdato: string
	kjoenn: string
	navn: Navn
	statsborgerskap: string
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
		omfang?: string
	}
	id: number
}

export type NyIdent = {
	identtype?: string
	kjoenn?: string
	foedtEtter?: string
	foedtFoer?: string
	alder?: string
	syntetisk?: boolean
	nyttNavn?: {
		hasMellomnavn: boolean
	}
}

export type SelectedValue = {
	value: string
	label: string
}

export enum Adressetype {
	Veg = 'VEGADRESSE',
	Matrikkel = 'MATRIKKELADRESSE',
	Postboks = 'POSTBOKSADRESSE',
	Utenlandsk = 'UTENLANDSK_ADRESSE',
	Ukjent = 'UKJENT_BOSTED',
	Annet = 'OPPHOLD_ANNET_STED',
}
