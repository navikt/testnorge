import { UseFormReturn } from 'react-hook-form/dist/types'

export type KdiMeldingFieldsProps = {
	path: string
	formMethods?: UseFormReturn
	erEksisterendeMelding: boolean
	fengselOptions?: Array<any>
	onSort?: () => void
	sortVersjon?: number
}

export type KdiMeldingProps = {
	meldingId?: string
	hendelseId?: string
	publiseringstidspunkt: Date
	kategori?: string
	organisasjonsnummer?: string
	tidspunkt?: Date
	erOverfoertTilUtlandskfengsel?: boolean
	erOverfoertTilVaretektMedElektroniskKontroll?: boolean
	forventetAvbruddSluttTidspunkt?: Date
	innmeldingHendelseId?: string
	annullertMeldingId?: string
	type?: string
}

export type KdiProps = {
	innsettelse?: Array<KdiMeldingProps>
	loeslatelse?: Array<KdiMeldingProps>
	avbruddStart?: Array<KdiMeldingProps>
	avbruddSlutt?: Array<KdiMeldingProps>
	forventetLoeslatelse?: Array<KdiMeldingProps>
	annullering?: Array<KdiMeldingProps>
}
