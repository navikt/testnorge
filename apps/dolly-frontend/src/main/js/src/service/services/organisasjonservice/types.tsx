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
