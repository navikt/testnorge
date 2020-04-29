export type Organisasjon = {
	value: string
	label: string
	juridiskEnhet?: string
	navn?: string
	forretningsAdresse?: Adresse
	postadresse?: Adresse
}

export type Adresse = {
	adresse?: string
	postnr?: string
	kommunenr?: string
	landkode?: string
	poststed?: string
}
