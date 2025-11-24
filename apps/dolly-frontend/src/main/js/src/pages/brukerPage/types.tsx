export type Organisasjon = {
	navn: string
	organisasjonsnummer: string
	organisasjonsform?: string
}

export type OrgResponse = {
	data: Organisasjon[]
}

export type Bruker = {
	id?: string
	brukernavn: string
	epost?: string
	organisasjonsnummer: string
	opprettet?: string
	sistInnlogget?: string
}
