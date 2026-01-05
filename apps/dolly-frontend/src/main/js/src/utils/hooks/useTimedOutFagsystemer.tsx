import { sjekkManglerAaregData } from '@/components/fagsystem/aareg/visning/Visning'
import { sjekkManglerPensjonData } from '@/components/fagsystem/pensjon/visning/PensjonVisning'
import { sjekkManglerTpData } from '@/components/fagsystem/tjenestepensjon/visning/TpVisning'
import { sjekkManglerApData } from '@/components/fagsystem/alderspensjon/visning/AlderspensjonVisning'
import { sjekkManglerUforetrygdData } from '@/components/fagsystem/uforetrygd/visning/UforetrygdVisning'
import { sjekkManglerBrregData } from '@/components/fagsystem/brregstub/visning/BrregVisning'
import { sjekkManglerInstData } from '@/components/fagsystem/inst/visning/InstVisning'
import {
	sjekkManglerSykemeldingBestilling,
	sjekkManglerSykemeldingData,
} from '@/components/fagsystem/sykdom/visning/Visning'
import { sjekkManglerYrkesskadeData } from '@/components/fagsystem/yrkesskader/visning/YrkesskaderVisning'
import { sjekkManglerUdiData } from '@/components/fagsystem/udistub/visning/UdiVisning'
import {
	harArbeidsplassenBestilling,
	harDokarkivBestilling,
	harHistarkBestilling,
	harMedlBestilling,
	harSykemeldingBestilling,
	harUdistubBestilling,
} from '@/utils/SjekkBestillingFagsystem'

interface UseTimedOutParams {
	data: any
	ident: any
	arbeidsforhold: any
	poppData: any
	tpDataForhold: any
	apData: any
	uforetrygdData: any
	brregstub: any
	instData: any
	sykemeldingData: any
	sykemeldingBestilling: any
	yrkesskadeData: any
	arbeidsplassencvData: any
	arbeidsplassencvError: any
	dokarkivData: any
	dokarkivError: any
	histarkData: any
	histarkError: any
	udistub: any
	udistubError: any
	medl: any
	medlError: any
	loadingAareg: any
	aaregError: any
}

export function useTimedOutFagsystemer(params: UseTimedOutParams): string[] {
	const {
		data,
		ident,
		arbeidsforhold,
		poppData,
		tpDataForhold,
		apData,
		uforetrygdData,
		brregstub,
		instData,
		sykemeldingData,
		sykemeldingBestilling,
		yrkesskadeData,
		arbeidsplassencvData,
		arbeidsplassencvError,
		dokarkivData,
		dokarkivError,
		histarkData,
		histarkError,
		udistub,
		udistubError,
		medl,
		medlError,
		loadingAareg,
		aaregError,
	} = params
	if (!data) return []
	const list: string[] = []
	const bestillingerFagsystemer = ident?.bestillinger?.map((i: any) => i.bestilling) || []
	const harAareg = bestillingerFagsystemer?.some((b: any) => b?.aareg)
	if (
		harAareg &&
		(!arbeidsforhold ||
			sjekkManglerAaregData(arbeidsforhold) ||
			(loadingAareg === false && typeof arbeidsforhold === 'undefined') ||
			aaregError)
	)
		list.push('AAREG')
	if (poppData && sjekkManglerPensjonData(poppData)) list.push('POPP')
	if (tpDataForhold && sjekkManglerTpData(tpDataForhold)) list.push('TP')
	if (apData && sjekkManglerApData(apData)) list.push('PEN_AP')
	if (uforetrygdData && sjekkManglerUforetrygdData(uforetrygdData)) list.push('PEN_UT')
	if (brregstub && sjekkManglerBrregData(brregstub)) list.push('BRREG')
	if (instData && sjekkManglerInstData(instData)) list.push('INST')
	if (
		sykemeldingData &&
		sjekkManglerSykemeldingData(sykemeldingData) &&
		harSykemeldingBestilling(bestillingerFagsystemer) &&
		sjekkManglerSykemeldingBestilling(sykemeldingBestilling)
	)
		list.push('SYKEMELDING')
	if (yrkesskadeData && sjekkManglerYrkesskadeData(yrkesskadeData)) list.push('YRKESSKADE')
	if (
		harArbeidsplassenBestilling(bestillingerFagsystemer) &&
		!arbeidsplassencvData &&
		arbeidsplassencvError
	)
		list.push('ARBEIDSPLASSENCV')
	if (harDokarkivBestilling(bestillingerFagsystemer) && !dokarkivData && dokarkivError)
		list.push('DOKARKIV')
	if (harHistarkBestilling(bestillingerFagsystemer) && !histarkData && histarkError)
		list.push('HISTARK')
	if (
		(udistub && sjekkManglerUdiData(udistub)) ||
		(harUdistubBestilling(bestillingerFagsystemer) && !udistub && udistubError)
	)
		list.push('UDI')
	if (
		(harMedlBestilling(bestillingerFagsystemer) && medlError) ||
		(harMedlBestilling(bestillingerFagsystemer) && medl && Array.isArray(medl) && medl.length === 0)
	)
		list.push('MEDL')
	return list
}
