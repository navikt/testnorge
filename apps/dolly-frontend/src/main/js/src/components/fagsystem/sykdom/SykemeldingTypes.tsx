import { UseFormReturn } from 'react-hook-form/dist/types'

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
	fom: string
	tom: string
}

export type SykemeldingData = {
	aktivitet: Array<SykemeldingAktivitet>
}

export type SykemeldingBestilling = {
	nySykemelding?: SykemeldingData
}
