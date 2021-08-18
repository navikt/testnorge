export type EnhetBestilling = {
	enhetstype: string
	naeringskode?: string
	sektorkode?: string
	formaal?: string
	stiftelsesdato?: string
	maalform?: string
	telefon?: string
	epost?: string
	nettside?: string
	forretningsadresse?: Adresse
	postadresse?: Adresse
	underenheter?: Array<EnhetBestilling>
	id?: number | string
	organisasjonsnavn?: string
	organisasjonNummer?: string
	status?: Array<Statuser>
}

type Statuser = {
	statuser: Array<StatusMeldinger>
}

type StatusMeldinger = {
	melding: string
}

export type EnhetData = {
	orgInfo: EnhetData
	organisasjonsnummer: string
	organisasjonsnavn: string
	enhetstype: string
	naeringskode?: string
	sektorkode?: string
	formaal?: string
	stiftelsesdato?: string
	maalform?: string
	telefon?: string
	epost?: string
	nettside?: string
	adresser?: Array<Adresse>
	underenheter?: Array<EnhetData>
	id: number
	bestillingId?: Array<number>
}

export type Adresse = {
	adresselinjer?: Array<string>
	postnr?: string
	postnummer?: string
	kommunenr?: string
	landkode?: string
	poststed?: string
	adressetype?: string
}

export type OrgStatus = {
	id?: number
}
