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
	juridiskEnhet?: string
	opprinnelse?: string
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

export type OrganisasjonFasteData = {
	orgnummer: string
	enhetstype: string
	navn: string
	redigertNavn?: string
	epost?: string
	internetAdresse?: string
	naeringskode?: string
	overenhet?: string
	forretningsAdresse?: AdresseFasteData
	postadresse?: AdresseFasteData
	opprinnelse?: string
	tags?: string[]
}

export type OrganisasjonForvalterData = {
	q1: OrganisasjonForvalterMiljoeData
	q2: OrganisasjonForvalterMiljoeData
}

export type AdresseOrgForvalter = {
	id: number
	adressetype: string
	adresselinjer: string[]
	postnr: string
	poststed: string
	kommunenr: string
	landkode: string
	vegadresseId: string
}

export type OrganisasjonForvalterMiljoeData = {
	id: number
	organisasjonsnummer: string
	juridiskEnhet: string
	enhetstype: string
	naeringskode: string
	sektorkode: string
	formaal: string
	organisasjonsnavn: string
	stiftelsesdato: string
	telefon: string
	epost: string
	nettside: string
	maalform: string
	adresser: AdresseOrgForvalter[]
	underenheter: any[]
}

export type AdresseFasteData = {
	adresselinje1?: string
	adresselinje2?: string
	adresselinje3?: string
	postnr?: string
	kommunenr?: string
	landkode?: string
	poststed?: string
}
