export type Person = {
	id: number
	ident?: string
	person: PersonData
	relasjoner?: Array<Relasjon>
}

export type OpprettNyPerson = {
	alder: number
	foedtEtter: string
	foedtFoer: string
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
	foedselsdato?: Array<FoedselsdatoData>
	foedested?: Array<FoedestedData>
	statsborgerskap?: Array<StatsborgerskapData>
	adressebeskyttelse?: Array<AdressebeskyttelseData>
	sivilstand?: Array<SivilstandData>
	foreldreBarnRelasjon?: Array<ForeldreBarnRelasjon>
	foreldreansvar?: Array<ForeldreansvarData>
	innflytting?: Array<Innflytting>
	utflytting?: Array<Utflytting>
	vergemaal?: Array<VergemaalValues>
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

export type NavnBestilling = {
	fornavn: string
	mellomnavn?: string
	etternavn: string
	hasMellomnavn: boolean
	gyldigFraOgMed?: string
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
	metadata?: Metadata
	id?: number
}

export type FoedselsdatoData = {
	foedselsdato: string
	foedselsaar: number
	metadata: Metadata
	id?: number
}

export type FoedestedData = {
	foedested: string
	foedekommune: string
	foedeland: string
	metadata: Metadata
	id?: number
}

export type DoedsfallData = {
	doedsdato: string
	metadata?: Metadata
	id?: number
}

export type StatsborgerskapData = {
	landkode: string
	gyldigFraOgMed: string
	gyldigTilOgMed: string
	bekreftelsesdato?: string
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
	master?: string
	metadata?: any
}

export type SivilstandData = {
	type: string
	gyldigFraOgMed?: string
	relatertVedSivilstand: string
	bekreftelsesdato?: string
	id?: number
	sivilstandsdato?: string
	borIkkeSammen?: boolean
	nyRelatertPerson?: NyIdent
	metadata: Metadata
}

export type Metadata = {
	historisk: boolean
	master?: string
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
	borIkkeSammen?: boolean
	partnerErIkkeForelder?: boolean
}

export type DoedfoedtBarnData = {
	id?: number
	dato: Date
}

export type ForeldreansvarData = {
	ansvar: string
	ansvarlig: string
	gyldigFraOgMed?: string
	gyldigTilOgMed?: string
	ansvarligUtenIdentifikator: ForeldreansvarUtenId
	nyAnsvarlig?: NyIdent
	metadata?: Metadata
}

export type ForeldreansvarUtenId = {
	foedselsdato: string
	kjoenn: string
	navn: Navn
	statsborgerskap: string
}

export type VergemaalValues = {
	vergemaalEmbete?: string
	mandatType?: string
	sakType?: string
	gyldigFraOgMed: string
	gyldigTilOgMed: string
	nyVergeIdent?: NyIdent
	vergeIdent?: string
	id: number
}

export type FullmaktValues = {
	omraader?: Array<string>
	gyldigFraOgMed?: string
	gyldigTilOgMed?: string
	motpartsPersonident?: string
	nyFullmektig?: NyIdent
	id: number
}

export type DeltBostedValues = {
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

export type FalskIdentitetData = {
	rettIdentitetVedIdentifikasjonsnummer?: string
	rettIdentitetVedOpplysninger?: {
		personnavn?: {
			fornavn?: string
			mellomnavn?: string
			etternavn?: string
		}
		foedselsdato?: string
		kjoenn?: string
		statsborgerskap?: Array<string>
	}
	rettIdentitetErUkjent?: boolean
}

export type UtenlandskIdentData = {
	identifikasjonsnummer: string
	utstederland: string
	opphoert: boolean
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
