export type HodejegerenResponse = {
	data: ResponseData[]
}

export type ResponseData = {
	id?: string
	datoRekvirert?: Date
	kilder: Kilder[]
}

type Kilder = {
	navn?: string
	data: KilderData[]
}

type KilderData = {
	datoOpprettet?: Date
	datoEndret?: Date
	innhold: Innhold[]
}

export type Innhold = {
	personIdent: PersonIdent
	personInfo: PersonInfo
	navn: Navn
	sivilstand: Sivilstand
	statsborger: Statsborger
	doedshistorikk: Doedshistorikk
	telefonPrivat: Telefon
	telefonMobil: Telefon
	telefonJobb: Telefon
	boadresse: Boadresse
	prioritertAdresse: PrioritertAdresse
	foreldreansvar: Foreldeansvar[]
	oppholdstillatelse: Oppholdstillatelse
	giro: Giro
	tillegg: Tillegg
	post: Post
	migrasjon: Migrasjon
	relasjoner: Relasjon[]
}

type PersonIdent = {
	id: string
	type: string
	fraDato?: Date
	status?: string
}

type PersonInfo = {
	kjoenn: string
	datoFoedt: Date
	foedtLand?: string
	foedtKommune?: string
	status?: string
}

type Navn = {
	forkortet: string
	slektsnavn: string
	fornavn: string
	mellomnavn?: string
	slektsnavnUgift?: string
	fraDato?: Date
	tilDato?: Date
}

type Sivilstand = {
	type: string
	fraDato?: Date
	tilDato?: Date
}

export type Statsborger = {
	land: string
	fraDato?: Date
	tilDato?: Date
}

type Doedshistorikk = {
	dato?: Date
	regDato?: Date
}

type Telefon = {
	retningslinje?: string
	nummer?: string
	fraDato?: Date
	tilDato?: Date
}

type Boadresse = {
	adresse?: string
	land?: string
	kommune?: string
	postnr?: string
	bydel?: string
	offentligGateKode?: string
	offentligHusnr?: string
	offentligBokstav?: string
	offentligBolignr?: string
	matrikkelGardsnr?: string
	matrikkelBruksnr?: string
	matrikkelFestenr?: string
	matrikkelUndernr?: string
	fraDato?: Date
	tilDato?: Date
}

type PrioritertAdresse = {
	type?: string
	fraDato?: Date
	tilDato?: Date
}

type Foreldeansvar = {
	ident?: string
	fraDato?: Date
	tilDato?: Date
}

export type Oppholdstillatelse = {
	status?: string
	fraDato?: Date
	tilDato?: Date
}

type Giro = {
	nummer?: string
	fraDato?: Date
	tilDato?: Date
}

type Tillegg = {
	adresse1?: string
	adresse2?: string
	adresse3?: string
	postnr?: string
	kommunenr?: string
	gateKode?: string
	husnummer?: string
	husbokstav?: string
	bolignummer?: string
	bydel?: string
	postboksnr?: string
	postboksAnlegg?: string
	fraDato?: Date
	tilDato?: Date
}

type Post = {
	adresse1?: string
	adresse2?: string
	adresse3?: string
	postnr?: string
	postland?: string
	fraDato?: Date
	tilDato?: Date
}

type Migrasjon = {
	type?: string
	land?: string
	fraDato?: Date
	tilDato?: Date
}

type Relasjon = {
	ident?: string
	type?: string
	status?: string
	rolle?: string
	fraDato?: string
	tilDato?: string
}
