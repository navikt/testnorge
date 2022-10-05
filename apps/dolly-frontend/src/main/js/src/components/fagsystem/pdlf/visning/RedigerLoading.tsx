import React from 'react'
import Loading from '~/components/ui/loading/Loading'

type LoadingProps = {
	visningModus: string
	ignoreSlett?: boolean
}

export enum Modus {
	Les = 'LES',
	Skriv = 'SKRIV',
	Loading = 'LOADING',
	LoadingPdlf = 'LOADING_PDLF',
	LoadingPdl = 'LOADING_PDL',
	LoadingPdlfSlett = 'LOADING_PDLF_SLETT',
	LoadingPdlSlett = 'LOADING_PDL_SLETT',
	LoadingSkjerming = 'LOADING_SKERMING',
}

export const RedigerLoading = ({ visningModus, ignoreSlett = false }: LoadingProps) => {
	switch (visningModus) {
		case Modus.LoadingPdlf:
			return <Loading label="Oppdaterer PDL-forvalter..." />
		case Modus.LoadingPdl:
			return <Loading label="Oppdaterer PDL..." />
		case Modus.LoadingSkjerming:
			return <Loading label="Oppdaterer skjermingsregisteret..." />
		case Modus.Loading:
			return <Loading label="Oppdaterer..." />
		case Modus.LoadingPdlfSlett:
			return ignoreSlett ? null : <Loading label="Oppdaterer PDL-forvalter..." />
		case Modus.LoadingPdlSlett:
			return ignoreSlett ? null : <Loading label="Oppdaterer PDL..." />
		default:
			return null
	}
}
