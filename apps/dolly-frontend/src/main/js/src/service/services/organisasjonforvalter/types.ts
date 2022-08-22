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
