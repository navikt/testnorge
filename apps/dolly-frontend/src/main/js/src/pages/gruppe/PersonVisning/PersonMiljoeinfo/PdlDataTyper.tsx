import {
	DoedfoedtBarnData,
	FoedestedData,
	FoedselData,
	FoedselsdatoData,
	ForeldreansvarData,
	ForeldreBarnRelasjon,
	Metadata,
	SivilstandData,
} from '@/components/fagsystem/pdlf/PdlTypes'

export type PdlDataWrapper = {
	errors: any
	data: {
		data: PdlData
		errors: Array<PdlError>
	}
}

export type PdlDataBolk = {
	errors: any
	data: {
		hentPersonBolk: Array<{ person: HentPerson; ident: string }>
		hentIdenterBolk: Array<{ identer: Array<Ident>; ident: string }>
	}
}

export type PdlData = {
	hentIdenter: { identer: [Ident] }
	hentPerson: HentPerson
	hentGeografiskTilknytning: object
	ident?: string
}

export type PdlError = {
	path?: Array<string>
	message?: string
}

export type Ident = {
	gruppe: string
	ident: string
	historisk: boolean
}

export type HentPerson = {
	foedsel?: [FoedselData]
	foedselsdato?: [FoedselsdatoData]
	foedested?: [FoedestedData]
	bostedsadresse: Array<BostedData>
	deltBosted: Array<DeltBostedData>
	oppholdsadresse: Array<OppholdsadresseData>
	kontaktadresse: Array<KontaktadresseData>
	adressebeskyttelse: Array<AdressebeskyttelseData>
	fullmakt: [FullmaktData]
	telefonnummer: Array<TelefonData>
	vergemaalEllerFremtidsfullmakt: Array<VergemaalData>
	tilrettelagtKommunikasjon: Array<TilrettelagtKommunikasjonData>
	sikkerhetstiltak: [SikkerhetstiltakData]
	sivilstand: Array<SivilstandData>
	forelderBarnRelasjon: Array<ForeldreBarnRelasjon>
	doedfoedtBarn: Array<DoedfoedtBarnData>
	foreldreansvar: Array<ForeldreansvarData>
	kontaktinformasjonForDoedsbo: Array<any>
	utenlandskIdentifikasjonsnummer: Array<{}>
	falskIdentitet: FalskIdentitet
	opphold: Array<OppholdData>
	statsborgerskap: [Statsborgerskap]
	innflyttingTilNorge: [InnflyttingTilNorge]
	utflyttingFraNorge: [UtflyttingFraNorge]
	doedsfall: [Doedsfall]
	folkeregisterpersonstatus?: [Folkeregisterpersonstatus]
	folkeregisterPersonstatus?: [Folkeregisterpersonstatus]
	kjoenn: [KjoennValues]
	navn: [Navn]
}

export type BostedData = {
	adressetype?: string
	angittFlyttedato?: Date
	coAdressenavn?: string
	gyldigFraOgMed?: Date
	gyldigTilOgMed?: Date
	vegadresse?: VegadresseData
	matrikkeladresse?: MatrikkeladresseData
	ukjentBosted?: UkjentBostedData
	utenlandskAdresse?: UtenlandskAdresseData
	metadata?: Metadata
	id?: number
}

export type DeltBostedData = {
	startdatoForKontrakt?: Date
	sluttdatoForKontrakt?: Date
	coAdressenavn?: string
	vegadresse?: VegadresseData
	matrikkeladresse?: MatrikkeladresseData
	ukjentBosted?: UkjentBostedData
	metadata?: Metadata
	adressetype?: string
	master?: string
}

export type OppholdsadresseData = {
	adressetype?: string
	utenlandskAdresse?: UtenlandskAdresseData
	vegadresse?: VegadresseData
	matrikkeladresse?: MatrikkeladresseData
	oppholdAnnetSted?: string
	coAdressenavn?: string
	gyldigFraOgMed?: Date
	metadata?: Metadata
	id?: number
}

export type KontaktadresseData = {
	adressetype?: string
	gyldigFraOgMed?: Date
	gyldigTilOgMed?: Date
	type?: string
	coAdressenavn?: string
	postboksadresse?: PostboksadresseData
	vegadresse?: VegadresseData
	postadresseIFrittFormat?: PostadresseIFrittFormatData
	utenlandskAdresse?: UtenlandskAdresseData
	utenlandskAdresseIFrittFormat?: UtenlandskAdresseIFrittFormatData
	metadata?: Metadata
	id?: number
}

export type PostboksadresseData = {
	postbokseier?: string
	postboks?: string
	postnummer?: string
}

export type PostadresseIFrittFormatData = {
	adresselinjer?: string[]
	postnummer?: string
}

export type UtenlandskAdresseIFrittFormatData = {
	adresselinjer?: string[]
	postkode?: string
	byEllerStedsnavn?: string
	landkode?: string
}

export type VegadresseData = {
	matrikkelId?: string
	husbokstav?: string
	husnummer?: string
	adressenavn?: string
	bruksenhetsnummer?: string
	tilleggsnavn?: string
	postnummer?: string
	kommunenummer?: string
	bydelsnummer?: string
	koordinater?: Koordinater
	adressekode?: string
}

export type MatrikkeladresseData = {
	matrikkelId?: string
	bruksenhetsnummer?: string
	tilleggsnavn?: string
	postnummer?: string
	kommunenummer?: string
	koordinater?: Koordinater
	gaardsnummer?: string
	bruksnummer?: string
}

type Koordinater = {
	x?: string
	y?: string
	z?: string
	kvalitet?: string
}

export type UkjentBostedData = {
	bostedskommune?: string
}

export type UtenlandskAdresseData = {
	adressenavnNummer?: string
	postboksNummerNavn?: string
	postkode?: string
	bySted?: string
	landkode?: string
	bygningEtasjeLeilighet?: string
	regionDistriktOmraade?: string
}

export type FullmaktData = {
	gyldigFraOgMed: Date
	gyldigTilOgMed: Date
	motpartsPersonident: string
	vergeEllerFullmektig?: {
		motpartsPersonident: string
	}
	motpartsRolle: string
	omraader: Array<string>
	id: number
}

export type OppholdData = {
	oppholdFra: Date
	oppholdTil: Date
	type: string
	id: number
}

export type TelefonData = {
	landskode?: string
	landkode?: string
	nummer?: string
	telefonnummer?: string
	prioritet?: number
	telefontype?: string
	id?: number
	master?: string
}

export type TilrettelagtKommunikasjonData = {
	talespraaktolk: { spraak: string }
	tegnspraaktolk: { spraak: string }
	spraakForTaletolk: string
	spraakForTegnspraakTolk: string
	master?: string
}

export type SikkerhetstiltakData = {
	gyldigFraOgMed: Date
	gyldigTilOgMed: Date
	tiltakstype: string
	beskrivelse: string
	kontaktperson: Kontaktperson
	omraader: []
	master?: string
}

export type Kontaktperson = {
	personident: string
	enhet: string
}

export type Relasjon = {
	relasjonType: string
	relatertPerson: {
		ident: string
		navn: Array<{
			fornavn: string
			mellomnavn?: string
			etternavn: string
		}>
		kjoenn: Array<{
			kjoenn: string
		}>
	}
}
export type AdressebeskyttelseData = {
	gradering: string
	master?: string
}

export type Kodeverk = {
	values: Array<Array<KodeverkValues>>
}

export type KodeverkValues = {
	data: string
	label: string
	value: string
}

type FalskIdentitet = {
	erFalsk: boolean
}

export type Statsborgerskap = {
	land: string
	gyldigFraOgMed: Date
	gyldigTilOgMed?: Date
	metadata: Metadata
}

export type InnflyttingTilNorge = {
	fraflyttingsland: string
	fraflyttingsstedIUtlandet: string
	metadata: Metadata
	folkeregistermetadata?: {
		gyldighetstidspunkt: string
	}
}

export type UtflyttingFraNorge = {
	tilflyttingsland: string
	tilflyttingsstedIUtlandet: string
	utflyttingsdato: string
	metadata: Metadata
}

type Doedsfall = {
	doedsdato: string
	metadata: Metadata
}

export type InnvandringValues = {
	fraflyttingsland: string
	fraflyttingsstedIUtlandet: string
	innflyttingsdato: string
	id?: number
	master?: string
}

export type UtvandringValues = {
	tilflyttingsland: string
	tilflyttingsstedIUtlandet: string
	utflyttingsdato: string
	id?: number
	master?: string
}

export type Folkeregisterpersonstatus = {
	status: string
	forenkletStatus: string
	metadata: Metadata
}

export type KjoennValues = {
	kjoenn: string
	metadata: Metadata
	master?: string
}

type Navn = {
	fornavn: string
	mellomnavn?: string
	etternavn: string
	forkortetNavn?: string
	metadata: Metadata
}

export type VergemaalData = {
	type: string
	embete: string
	vergeEllerFullmektig: VergeEllerFullmektig
	folkeregistermetadata: {
		gyldighetstidspunkt: string
		opphoerstidspunkt?: string
	}
	metadata: Metadata
}

export type TjenesteomraadeData = {
	tjenesteoppgave?: string[]
	tjenestevirksomhet?: string
}

export type VergeEllerFullmektig = {
	navn: {
		fornavn: string
		mellomnavn?: string
		etternavn: string
	}
	motpartsPersonident: string
	omfang: string
	omfangetErInnenPersonligOmraade: string
	tjenesteomraade?: Array<TjenesteomraadeData>
}
