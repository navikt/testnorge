export type Organisasjon = {
	id: number
	organisasjonsnummer: string
	enhetstype: string
	naeringskode?: string
	sektorkode: string
	formaal?: string
	organisasjonsnavn: string
	stiftelsesdato?: string
	telefon?: string
	epost?: string
	nettside?: string
	maalform?: string
	adresser?: Adresse[]
	underenheter?: Organisasjon[]
}

export type Adresse = {
	id: string
	adressetype: string
	adresselinjer?: string[]
	postnr?: string
	poststed?: string
	kommunenr?: string
	landkode?: string
	vegadresseId?: string
}

export type OrgInfoResponse = {
	data: OrgInfoData
}

type OrgInfoData = {
	driverVirksomheter?: any[]
	enhetType: string
	forretningsadresser?: OrgInfoAdresse
	juridiskEnhet: string
	navn: string
	orgnummer: string
	postadresse?: OrgInfoAdresse
	redigertnavn?: string
}

export type OrgInfoAdresse = {
	adresselinje1: string
	adresselinje2: string
	adresselinje3: string
	kommunenummer: string
	landkode: string
	postnummer: string
	poststed: string
}
