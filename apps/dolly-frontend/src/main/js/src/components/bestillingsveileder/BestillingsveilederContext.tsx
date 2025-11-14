import React, { Context, useContext } from 'react'

export interface BestillingsveilederContextType {
	gruppeId?: number | null
	initialValues: any
	gruppe?: { id?: string; navn?: string }
	personFoerLeggTil?: any
	identMaster?: string
	antall?: number
	identtype?: string
	setIdenttype?: (identtype: string) => void
	setGruppeId?: (gruppeId: number) => void
	setMal?: (mal: any | undefined) => void
	updateContext?: (patch: Partial<BestillingsveilederContextType>) => void
	id2032?: boolean
	importPersoner?: any[]
	idligereBestillinger?: any[]
	opprettFraIdenter?: string[]
	mal?: { malNavn?: string; [key: string]: any }
	is?: {
		nyBestilling?: boolean
		nyBestillingFraMal?: boolean
		opprettFraIdenter?: boolean
		leggTil?: boolean
		leggTilPaaGruppe?: boolean
		nyOrganisasjon?: boolean
		nyOrganisasjonFraMal?: boolean
		nyStandardOrganisasjon?: boolean
		importTestnorge?: boolean
	}
	timedOutFagsystemer?: string[]
}

const defaultContextValue: BestillingsveilederContextType = {
	initialValues: {},
	gruppeId: null,
	setIdenttype: () => {},
	setGruppeId: () => {},
	setMal: () => {},
	updateContext: () => {},
}

export const BestillingsveilederContext: Context<BestillingsveilederContextType> =
	React.createContext(defaultContextValue)

export const useBestillingsveileder = (): BestillingsveilederContextType =>
	useContext(BestillingsveilederContext)
