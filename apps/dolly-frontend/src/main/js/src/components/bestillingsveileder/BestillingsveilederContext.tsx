import React from 'react'

export const BestillingsveilederContext = __preserveRef(
	'BestillingsveilederContext',
	React.createContext(undefined),
)

export interface BestillingsveilederContextType {
	gruppeId?: string
	gruppe?: { id?: string; navn?: string }
	personFoerLeggTil?: string
	identMaster?: string
	antall?: number
	identtype?: string
	opprettFraIdenter?: string[]
	mal?: { malNavn?: string }
	is?: {
		nyOrganisasjon?: boolean
		nyStandardOrganisasjon?: boolean
		nyOrganisasjonFraMal?: boolean
		opprettFraIdenter?: boolean
		leggTilPaaGruppe?: boolean
		nyBestillingFraMal?: boolean
		importTestnorge?: boolean
		leggTil?: boolean
	}
}
