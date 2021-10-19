export type Organisasjon = {
	navn: string
	organisasjonsnummer: string
	organisasjonsfrom?: string
	organisasjonsform?: string
	gyldigTil?: string
}

export type OrgResponse = {
	data: Organisasjon[]
}

export type Bruker = {
	id?: string
	brukernavn: string
	organisasjonsnummer: string
	opprettet?: string
	sistInnlogget?: string
}

export type BrukerResponse = {
	data: Bruker[]
}
