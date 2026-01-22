export type BestillingStatusDetaljert = {
	miljo: string
	identer: string[]
}

export type BestillingStatus = {
	melding: string
	identer?: string[]
	detaljert?: BestillingStatusDetaljert[]
}

export type BestillingStatusGruppe = {
	id: string
	navn: string
	statuser: BestillingStatus[]
}

export type Bruker = {
	brukerId: string
	brukernavn: string
	brukertype: string
	epost: string
}

export type Bestilling = {
	id: number
	antallIdenter: number
	antallLevert: number
	bestilling: Record<string, unknown>
	ferdig: boolean
	sistOppdatert: string | Date
	opprettetFraId: number | string | null
	gjenopprettetFraIdent: string | null
	opprettetFraGruppeId: number | string | null
	bruker: Bruker | string | null
	gruppeId: number
	stoppet: boolean
	feil: string | null
	environments: string[]
	status: BestillingStatusGruppe[]
	opprettFraIdenter: string[] | null
}
