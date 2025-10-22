import { BestillingsveilederContextType } from '@/components/bestillingsveileder/BestillingsveilederContext'

const names: Record<string, string> = {
	AAREG: 'AAREG',
	DOKARKIV: 'Dokarkiv',
	HISTARK: 'Histark',
	POPP: 'Pensjonsgivende inntekt',
	TP: 'Tjenestepensjon',
	PEN_AP: 'Alderspensjon',
	PEN_UT: 'Uføretrygd',
	SYKEMELDING: 'Sykemelding',
	YRKESSKADE: 'Yrkesskade',
	BRREG: 'Brreg',
	INST: 'Institusjonsopphold',
	ARBEIDSPLASSENCV: 'Nav CV',
}

export const getTimeoutTitle = (
	code: string,
	ctx: BestillingsveilederContextType,
): string | undefined => {
	if (!(ctx?.is?.leggTil || ctx?.is?.leggTilPaaGruppe)) return undefined
	if (!ctx?.timedOutFagsystemer?.includes(code)) return undefined
	const display = names[code] || code
	return display + ' kan ikke nås fra Dolly, legg til/endre er midlertidig utilgjengelig'
}

export const getTimeoutAttr = (
	code: string,
	ctx: BestillingsveilederContextType,
): { disabled: boolean | undefined; title: string | undefined } => {
	const disabled =
		(ctx?.is?.leggTil || ctx?.is?.leggTilPaaGruppe) && ctx?.timedOutFagsystemer?.includes(code)
	const title = disabled ? getTimeoutTitle(code, ctx) : undefined
	return { disabled, title }
}
