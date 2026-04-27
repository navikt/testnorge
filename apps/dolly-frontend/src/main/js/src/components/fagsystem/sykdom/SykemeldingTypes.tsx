import { UseFormReturn } from 'react-hook-form/dist/types'

export type SykmeldingType = 'VANLIG' | 'AVVENTENDE' | 'BEHANDLINGSDAGER' | 'REISETILSKUDD'

export const sykmeldingTypeOptions = [
	{ value: 'VANLIG', label: 'Vanlig' },
	{ value: 'AVVENTENDE', label: 'Avventende' },
	{ value: 'BEHANDLINGSDAGER', label: 'Behandlingsdager' },
	{ value: 'REISETILSKUDD', label: 'Reisetilskudd' },
]

export const sykmeldingTypeLabels: Record<SykmeldingType, string> = {
	VANLIG: 'Vanlig',
	AVVENTENDE: 'Avventende',
	BEHANDLINGSDAGER: 'Behandlingsdager',
	REISETILSKUDD: 'Reisetilskudd',
}

export interface SykemeldingForm {
	formMethods: UseFormReturn
}

export type SykemeldingMiljoData = {
	miljo: string
	data?: unknown
}

export type Sykemelding = {
	data: Array<SykemeldingMiljoData>
	ident: any
	loading: boolean
	bestillingIdListe: any
	tilgjengeligMiljoe: any
	bestillinger: any
}

export type SykemeldingAktivitet = {
	grad?: number
	reisetilskudd?: boolean
	fom: string
	tom: string
}

export type SykemeldingData = {
	type?: SykmeldingType
	aktivitet: Array<SykemeldingAktivitet>
}

export type SykemeldingBestilling = {
	nySykemelding?: SykemeldingData
}
