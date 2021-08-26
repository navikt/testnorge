export type EregResponse = {
	orgnr: string
	enhetstype: string
	navn: string
	juridiskEnhet: string | null
	kanHaArbeidsforhold: boolean
	forretningsAdresse?: Adresse
	postadresse?: Adresse
	opprinnelse?: string
}

export type Adresse = {
	adresse?: string
	postnr?: string
	kommunenr?: string
	landkode?: string
	poststed?: string
}
