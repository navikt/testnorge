import React, { useEffect } from 'react'
import Button from '@/components/ui/button/Button'
import { TidligereBestillinger } from '@/pages/gruppe/PersonVisning/TidligereBestillinger/TidligereBestillinger'
import { PersonMiljoeinfo } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PersonMiljoeinfo'
import BeskrivelseConnector from '@/components/beskrivelse/BeskrivelseConnector'
import { SlettButton } from '@/components/ui/button/SlettButton/SlettButton'
import { BestillingSammendragModal } from '@/components/bestilling/sammendrag/BestillingSammendragModal'
import './PersonVisning.less'
import { PdlPersonMiljoeInfo } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlPersonMiljoeinfo'
import PdlfVisningConnector from '@/components/fagsystem/pdlf/visning/PdlfVisningConnector'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { FrigjoerButton } from '@/components/ui/button/FrigjoerButton/FrigjoerButton'
import { useNavigate } from 'react-router'
import { getBestillingsListe } from '@/ducks/bestillingStatus'
import { RelatertPersonImportButton } from '@/components/ui/button/RelatertPersonImportButton/RelatertPersonImportButton'
import { useAsync } from 'react-use'
import { DollyApi } from '@/service/Api'
import { GjenopprettPerson } from '@/components/bestilling/gjenopprett/GjenopprettPerson'
import {
	BrregVisning,
	sjekkManglerBrregData,
} from '@/components/fagsystem/brregstub/visning/BrregVisning'
import {
	PensjonVisning,
	sjekkManglerPensjonData,
} from '@/components/fagsystem/pensjon/visning/PensjonVisning'
import { AaregVisning, sjekkManglerAaregData } from '@/components/fagsystem/aareg/visning/Visning'
import { useArbeidsforhold } from '@/utils/hooks/useDollyOrganisasjoner'
import {
	useArbeidsplassencvData,
	useArenaData,
	useDokarkivData,
	useHistarkData,
	useInstData,
	usePensjonsavtaleData,
	usePoppData,
	useTpDataForhold,
	useTransaksjonIdData,
	useTransaksjonIdPensjon,
} from '@/utils/hooks/useFagsystemer'
import {
	sjekkManglerTpData,
	TpVisning,
} from '@/components/fagsystem/tjenestepensjon/visning/TpVisning'
import { InstVisning, sjekkManglerInstData } from '@/components/fagsystem/inst/visning/InstVisning'
import {
	harAaregBestilling,
	harAfpOffentligBestilling,
	harApBestilling,
	harArbeidsplassenBestilling,
	harArbeidssoekerregisteretBestilling,
	harArenaBestilling,
	harDokarkivBestilling,
	harHistarkBestilling,
	harInntektsmeldingBestilling,
	harInstBestilling,
	harMedlBestilling,
	harPensjonavtaleBestilling,
	harPoppBestilling,
	harSigrunstubPensjonsgivendeInntekt,
	harSigrunstubSummertSkattegrunnlag,
	harSkattekortBestilling,
	harSykemeldingBestilling,
	harTpBestilling,
	harUdistubBestilling,
	harUforetrygdBestilling,
	harYrkesskaderBestilling,
} from '@/utils/SjekkBestillingFagsystem'
import {
	AlderspensjonVisning,
	sjekkManglerApData,
} from '@/components/fagsystem/alderspensjon/visning/AlderspensjonVisning'
import { ArbeidsplassenVisning } from '@/components/fagsystem/arbeidsplassen/visning/Visning'
import * as _ from 'lodash-es'
import { MedlVisning } from '@/components/fagsystem/medl/visning'
import { useMedlPerson } from '@/utils/hooks/useMedl'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import {
	sjekkManglerSykemeldingBestilling,
	sjekkManglerSykemeldingData,
	SykemeldingVisning,
} from '@/components/fagsystem/sykdom/visning/Visning'
import {
	sjekkManglerUforetrygdData,
	UforetrygdVisning,
} from '@/components/fagsystem/uforetrygd/visning/UforetrygdVisning'
import { usePensjonEnvironments } from '@/utils/hooks/useEnvironments'
import { SigrunstubPensjonsgivendeVisning } from '@/components/fagsystem/sigrunstubPensjonsgivende/visning/Visning'
import { useUdistub } from '@/utils/hooks/useUdistub'
import useBoolean from '@/utils/hooks/useBoolean'
import { MalModal, malTyper } from '@/pages/minSide/maler/MalModal'
import { useTenorIdent } from '@/utils/hooks/useTenorSoek'
import { SkatteetatenVisning } from '@/components/fagsystem/skatteetaten/visning/SkatteetatenVisning'
import PdlVisningConnector from '@/components/fagsystem/pdl/visning/PdlVisningConnector'
import { useOrganisasjonMiljoe } from '@/utils/hooks/useOrganisasjonTilgang'
import { useSkattekort } from '@/utils/hooks/useSkattekort'
import { SkattekortVisning } from '@/components/fagsystem/skattekort/visning/Visning'
import { PensjonsavtaleVisning } from '@/components/fagsystem/pensjonsavtale/visning/PensjonsavtaleVisning'
import { useMockOppsett } from '@/utils/hooks/usePensjon'
import { AfpOffentligVisning } from '@/components/fagsystem/afpOffentlig/visning/AfpOffentligVisning'
import {
	sjekkManglerYrkesskadeData,
	YrkesskaderVisning,
} from '@/components/fagsystem/yrkesskader/visning/YrkesskaderVisning'
import { InntektsmeldingVisning } from '@/components/fagsystem/inntektsmelding/visning/Visning'
import { InntektstubVisning } from '@/components/fagsystem/inntektstub/visning/Visning'
import { ArenaVisning } from '@/components/fagsystem/arena/visning/ArenaVisning'
import { KrrVisning } from '@/components/fagsystem/krrstub/visning/KrrVisning'
import { sjekkManglerUdiData, UdiVisning } from '@/components/fagsystem/udistub/visning/UdiVisning'
import DokarkivVisning from '@/components/fagsystem/dokarkiv/visning/Visning'
import HistarkVisning from '@/components/fagsystem/histark/visning/Visning'
import { useArbeidssoekerregistrering } from '@/utils/hooks/useArbeidssoekerregisteret'
import { ArbeidssoekerregisteretVisning } from '@/components/fagsystem/arbeidssoekerregisteret/visning/ArbeidssoekerregisteretVisning'
import { usePensjonsgivendeInntekt, useSummertSkattegrunnlag } from '@/utils/hooks/useSigrunstub'
import { SigrunstubSummertSkattegrunnlagVisning } from '@/components/fagsystem/sigrunstubSummertSkattegrunnlag/visning/Visning'
import { useNomData } from '@/utils/hooks/useNom'
import { NavAnsattVisning } from '@/components/fagsystem/nom/visning/Visning'
import { useTimedOutFagsystemer } from '@/utils/hooks/useTimedOutFagsystemer'
import { useSkjerming } from '@/utils/hooks/useSkjerming'

const getIdenttype = (ident: string) => {
	if (parseInt(ident.charAt(0)) > 3) {
		return 'DNR'
	} else if (parseInt(ident.charAt(2)) % 4 >= 2) {
		return 'NPID'
	} else {
		return 'FNR'
	}
}

export const DEFAULT_RETRY_COUNT = 8

interface PersonVisningProps {
	fetchDataFraFagsystemer: any
	data: any
	bestillingIdListe: any
	ident: any
	brukertype: any
	loading: any
	slettPerson: any
	leggTilPaaPerson: any
	iLaastGruppe: any
	tmpPersoner: any
}

export default (props: PersonVisningProps) => {
	const {
		fetchDataFraFagsystemer,
		data,
		bestillingIdListe,
		ident,
		brukertype,
		loading,
		slettPerson,
		leggTilPaaPerson,
		iLaastGruppe,
		tmpPersoner,
	} = props
	const { gruppeId } = ident
	const [isMalModalOpen, openMalModal, closeMalModal] = useBoolean(false)
	const { organisasjonMiljoe } = useOrganisasjonMiljoe()
	const tilgjengeligMiljoe = organisasjonMiljoe?.miljoe
	const bestillinger: any[] = []
	if (ident.bestillinger) {
		ident.bestillinger.map((b: any) => {
			bestillinger[b.id] = b
		})
	}
	const bestillingListe = getBestillingsListe(bestillinger, bestillingIdListe)
	const bestilling = bestillinger?.[bestillingIdListe?.[0]]

	useEffect(() => {
		fetchDataFraFagsystemer(bestillinger)
	}, [])

	const bestillingerFagsystemer = ident?.bestillinger?.map((i: any) => i.bestilling) || []

	const {
		loading: loadingAareg,
		arbeidsforhold,
		error: aaregError,
	} = useArbeidsforhold(
		ident.ident,
		harAaregBestilling(bestillingerFagsystemer) || ident?.master === 'PDL',
	)

	const visArbeidsforhold =
		ident?.master !== 'PDL' || arbeidsforhold?.some((miljodata) => miljodata?.data?.length > 0)

	const { loading: loadingSkattekort, skattekortData } = useSkattekort(
		ident.ident,
		harSkattekortBestilling(bestillingerFagsystemer),
	)

	const { medl, error: medlError } = useMedlPerson(
		ident.ident,
		harMedlBestilling(bestillingerFagsystemer) || ident?.master === 'PDL',
	)

	const {
		loading: loadingUdistub,
		udistub,
		error: udistubError,
	} = useUdistub(
		ident.ident,
		harUdistubBestilling(bestillingerFagsystemer) || ident?.master === 'PDL',
		harUdistubBestilling(bestillingerFagsystemer) ? DEFAULT_RETRY_COUNT : 0,
	)

	const {
		loading: loadingSigrunstubPensjonsgivendeInntekt,
		data: sigrunstubPensjonsgivendeInntekt,
	} = usePensjonsgivendeInntekt(
		ident.ident,
		harSigrunstubPensjonsgivendeInntekt(bestillingerFagsystemer) || ident?.master === 'PDL',
		harSigrunstubPensjonsgivendeInntekt(bestillingerFagsystemer) ? DEFAULT_RETRY_COUNT : 0,
	)

	const { loading: loadingSigrunstubSummertSkattegrunnlag, data: sigrunstubSummertSkattegrunnlag } =
		useSummertSkattegrunnlag(
			ident.ident,
			harSigrunstubSummertSkattegrunnlag(bestillingerFagsystemer) || ident?.master === 'PDL',
			harSigrunstubSummertSkattegrunnlag(bestillingerFagsystemer) ? DEFAULT_RETRY_COUNT : 0,
		)

	const { loading: loadingTpDataForhold, tpDataForhold } = useTpDataForhold(
		ident.ident,
		harTpBestilling(bestillingerFagsystemer),
	)

	const { loading: loadingPensjonsavtaleData, pensjonsavtaleData } = usePensjonsavtaleData(
		ident.ident,
		harPensjonavtaleBestilling(bestillingerFagsystemer),
	)

	const { loading: loadingPoppData, poppData } = usePoppData(
		ident.ident,
		harPoppBestilling(bestillingerFagsystemer),
	)

	const {
		loading: loadingDokarkivData,
		dokarkivData,
		error: dokarkivError,
	} = useDokarkivData(ident.ident, harDokarkivBestilling(bestillingerFagsystemer))

	const {
		loading: loadingHistarkData,
		histarkData,
		error: histarkError,
	} = useHistarkData(ident.ident, harHistarkBestilling(bestillingerFagsystemer))

	const { loading: loadingInstData, instData } = useInstData(
		ident.ident,
		harInstBestilling(bestillingerFagsystemer),
	)

	const { loading: loadingArbeidssoekerregisteret, data: arbeidssoekerregisteretData } =
		useArbeidssoekerregistrering(
			ident.ident,
			harArbeidssoekerregisteretBestilling(bestillingerFagsystemer),
		)

	const {
		loading: loadingArbeidsplassencvData,
		arbeidsplassencvData,
		error: arbeidsplassencvError,
	} = useArbeidsplassencvData(ident.ident, harArbeidsplassenBestilling(bestillingerFagsystemer))

	const { loading: loadingArenaData, arenaData } = useArenaData(
		ident.ident,
		harArenaBestilling(bestillingerFagsystemer) ||
			(harAaregBestilling(bestillingerFagsystemer) &&
				harSykemeldingBestilling(bestillingerFagsystemer)),
	)

	const { pensjonEnvironments } = usePensjonEnvironments()

	const { loading: loadingApData, data: apData } = useTransaksjonIdPensjon(
		ident.ident,
		harApBestilling(bestillingerFagsystemer),
	)

	const { loading: loadingUforetrygdData, data: uforetrygdData } = useTransaksjonIdData(
		ident.ident,
		'PEN_UT',
		harUforetrygdBestilling(bestillingerFagsystemer),
		pensjonEnvironments as any,
	)

	const { mockOppsett: afpOffentligData, loading: afpOffentligLoading } = useMockOppsett(
		pensjonEnvironments as any,
		ident.ident,
		harAfpOffentligBestilling(bestillingerFagsystemer),
	)

	const { loading: loadingSykemeldingData, data: sykemeldingData } = useTransaksjonIdData(
		ident.ident,
		'SYKEMELDING',
		harSykemeldingBestilling(bestillingerFagsystemer),
	)

	const sykemeldingBestilling = SykemeldingVisning.filterValues(
		bestillingListe as any,
		ident.ident,
	) as any

	const { loading: loadingYrkesskadeData, data: yrkesskadeData } = useTransaksjonIdData(
		ident.ident,
		'YRKESSKADE',
		harYrkesskaderBestilling(bestillingerFagsystemer),
	)

	const { loading: loadingInntektsmeldingData, data: inntektsmeldingData } = useTransaksjonIdData(
		ident.ident,
		'INNTKMELD',
		harInntektsmeldingBestilling(bestillingerFagsystemer),
	)

	const inntektsmeldingBestilling = InntektsmeldingVisning.filterValues(
		bestillingListe as any,
		ident.ident,
	) as any

	const { person: tenorData, loading: loadingTenorData } = useTenorIdent(
		ident?.master === 'PDL' ? ident.ident : null,
	)

	const { nomData, loading: loadingNom } = useNomData(ident.ident)

	const { skjerming: skjermingData } = useSkjerming(ident.ident)

	const getGruppeIdenter = () => {
		return useAsync(async () => DollyApi.getGruppeById(gruppeId), [DollyApi.getGruppeById])
	}

	const gruppeIdenter = getGruppeIdenter().value?.data?.identer?.map((person: any) => person.ident)

	const navigate = useNavigate()

	if (!data) {
		return null
	}

	const { inntektstub, brregstub, krrstub } = data

	const manglerFagsystemdata = (): boolean => {
		const checks: Array<{ condition: boolean; reason: string }> = [
			{
				condition: [inntektstub, krrstub].some((fs) => Array.isArray(fs) && fs.length === 0),
				reason: 'Tomt inntektstub eller krrstub',
			},
			{
				condition: !!(arbeidsforhold && sjekkManglerAaregData(arbeidsforhold) && visArbeidsforhold),
				reason: 'Aareg mangler data',
			},
			{
				condition: !!(poppData && sjekkManglerPensjonData(poppData)),
				reason: 'Pensjon (POPP) mangler data',
			},
			{
				condition: !!(tpDataForhold && sjekkManglerTpData(tpDataForhold)),
				reason: 'Tjenestepensjon mangler data',
			},
			{
				condition: !!(apData && sjekkManglerApData(apData)),
				reason: 'Alderspensjon mangler data',
			},
			{
				condition: !!(uforetrygdData && sjekkManglerUforetrygdData(uforetrygdData)),
				reason: 'Uføretrygd mangler data',
			},
			{
				condition: !!(brregstub && sjekkManglerBrregData(brregstub)),
				reason: 'Brreg mangler data',
			},
			{
				condition: !!(instData && sjekkManglerInstData(instData)),
				reason: 'Inst mangler data',
			},
			{
				condition: !!(
					sykemeldingData &&
					!_.isEmpty(sykemeldingData) &&
					sjekkManglerSykemeldingData(sykemeldingData) &&
					harSykemeldingBestilling(bestillingerFagsystemer) &&
					sjekkManglerSykemeldingBestilling(sykemeldingBestilling)
				),
				reason: 'Sykemelding mangler data eller feilet',
			},
			{
				condition: !!(yrkesskadeData && sjekkManglerYrkesskadeData(yrkesskadeData)),
				reason: 'Yrkesskade mangler data eller feilet',
			},
			{
				condition: !!(
					(udistub && sjekkManglerUdiData(udistub)) ||
					(harUdistubBestilling(bestillingerFagsystemer) && !udistub && udistubError)
				),
				reason: 'UDI mangler data eller feilet',
			},
			{
				condition: !!(
					(harMedlBestilling(bestillingerFagsystemer) && medlError) ||
					(harMedlBestilling(bestillingerFagsystemer) &&
						medl?.response &&
						Array.isArray(medl.response) &&
						medl.response.length === 0)
				),
				reason: 'MEDL mangler data eller feilet',
			},
			{
				condition: !!(
					harArbeidsplassenBestilling(bestillingerFagsystemer) &&
					!arbeidsplassencvData &&
					arbeidsplassencvError
				),
				reason: 'Arbeidsplassen CV mangler data eller feilet',
			},
		]

		for (const { condition, reason } of checks) {
			if (condition) {
				console.warn('manglerFagsystemdata:', reason)
				return true
			}
		}
		return false
	}

	const pdlRelatertPerson = () => {
		const relatertePersoner: any[] = []
		data.pdl?.hentPerson?.sivilstand
			?.filter((siv: any) =>
				['GIFT', 'REGISTRERT_PARTNER', 'SEPARERT', 'SEPARERT_PARTNER'].includes(siv?.type),
			)
			?.forEach((person: any) => {
				relatertePersoner.push({
					type: 'PARTNER',
					id: person.relatertVedSivilstand,
				})
			})
		data.pdlforvalter?.person?.sivilstand
			?.filter((siv: any) => siv?.type === 'SAMBOER')
			?.forEach((person: any) => {
				relatertePersoner.push({
					type: 'SAMBOER',
					id: person.relatertVedSivilstand,
				})
			})
		data.pdl?.hentPerson?.forelderBarnRelasjon
			?.filter((forelderBarn: any) =>
				['BARN', 'MOR', 'MEDMOR', 'FAR'].includes(forelderBarn?.relatertPersonsRolle),
			)
			?.forEach((person: any) => {
				relatertePersoner.push({
					type: person.relatertPersonsRolle,
					id: person.relatertPersonsIdent,
				})
			})
		data.pdl?.hentPerson?.foreldreansvar
			?.filter((foreldreansvar: any) => foreldreansvar.ansvarlig)
			?.forEach((person: any) => {
				relatertePersoner.push({
					type: 'ANSVARLIG',
					id: person.ansvarlig,
				})
			})
		return relatertePersoner
	}

	const relatertePersoner = pdlRelatertPerson()?.filter((ident) => ident.id)
	const harPdlRelatertPerson = relatertePersoner?.length > 0

	const getArbeidsplassencvHjemmel = () => {
		if (!harArbeidsplassenBestilling(bestillingerFagsystemer)) return null
		const arbeidsplassenBestillinger = bestillingListe.filter((bestilling) =>
			_.has(bestilling.data, 'arbeidsplassenCV'),
		)
		return arbeidsplassenBestillinger?.[0]?.data?.arbeidsplassenCV?.harHjemmel
	}

	const isLoadingFagsystemer =
		loadingNom ||
		loadingAareg ||
		loadingArbeidssoekerregisteret ||
		loadingArbeidsplassencvData ||
		loadingArenaData ||
		loadingApData

	const timedOutFagsystemer = useTimedOutFagsystemer({
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
	})

	return (
		<ErrorBoundary>
			<div className="person-visning">
				<div className="person-visning_actions">
					{!iLaastGruppe && (
						<Button
							onClick={() => {
								let personData = data
								if (tmpPersoner?.pdlforvalter?.hasOwnProperty(ident.ident)) {
									personData.pdlforvalter = tmpPersoner.pdlforvalter[ident.ident]
								}
								if (skjermingData) {
									personData.skjermingsregister = skjermingData
								}
								if (nomData) {
									personData.nomdata = nomData
								}
								if (arbeidsforhold) {
									personData.aareg = arbeidsforhold
								}
								if (arbeidssoekerregisteretData) {
									personData.arbeidssoekerregisteret = arbeidssoekerregisteretData
								}
								if (arbeidsplassencvData) {
									personData.arbeidsplassenCV = { harHjemmel: getArbeidsplassencvHjemmel() }
								}
								if (arenaData) {
									personData.arenaforvalteren = arenaData
								}
								if (apData) {
									personData.alderspensjon = apData
								}
								personData.timedOutFagsystemer = timedOutFagsystemer
								leggTilPaaPerson(
									personData,
									bestillingListe,
									ident.master,
									getIdenttype(ident.ident),
									gruppeId,
									navigate,
								)
							}}
							kind="add-circle"
							loading={isLoadingFagsystemer}
						>
							LEGG TIL/ENDRE
						</Button>
					)}
					<GjenopprettPerson ident={ident} />
					{!iLaastGruppe && harPdlRelatertPerson && (
						<RelatertPersonImportButton
							gruppeId={gruppeId}
							relatertPersonIdenter={relatertePersoner}
							gruppeIdenter={gruppeIdenter}
							master={ident?.master}
						/>
					)}
					{bestillingIdListe?.length > 0 && (
						<>
							<Button onClick={openMalModal} kind={'maler'} className="svg-icon-blue">
								OPPRETT MAL
							</Button>
							<BestillingSammendragModal bestillinger={ident?.bestillinger} />
						</>
					)}
					{!iLaastGruppe && ident.master !== 'PDL' && (
						<SlettButton action={slettPerson} loading={loading.slettPerson}>
							Er du sikker på at du vil slette denne personen?
						</SlettButton>
					)}
					{!iLaastGruppe && ident.master === 'PDL' && (
						<FrigjoerButton slettPerson={slettPerson} loading={loading.slettPerson} />
					)}
				</div>
				{manglerFagsystemdata() && (
					<StyledAlert variant={'info'} size={'small'}>
						Det ser ut til at denne personen har ufullstendige data fra ett eller flere fagsystemer.
						Forsøk å gjenopprette personen for å fikse dette, og ta eventuelt kontakt med Team Dolly
						dersom problemet vedvarer.
					</StyledAlert>
				)}
				{ident.master === 'PDLF' && <PdlfVisningConnector fagsystemData={data} loading={loading} />}
				{ident.master === 'PDL' && (
					<PdlVisningConnector pdlData={data.pdl} fagsystemData={data} loading={loading} />
				)}
				<NavAnsattVisning nomData={nomData} nomLoading={loadingNom} ident={ident.ident} />
				{visArbeidsforhold && (
					<AaregVisning
						liste={arbeidsforhold}
						loading={loadingAareg}
						bestillingIdListe={bestillingIdListe}
						bestillinger={bestillingListe}
						tilgjengeligMiljoe={tilgjengeligMiljoe}
						timedOutFagsystemer={timedOutFagsystemer}
					/>
				)}
				<SigrunstubPensjonsgivendeVisning
					data={sigrunstubPensjonsgivendeInntekt}
					loading={loadingSigrunstubPensjonsgivendeInntekt}
				/>
				<SigrunstubSummertSkattegrunnlagVisning
					data={sigrunstubSummertSkattegrunnlag}
					loading={loadingSigrunstubSummertSkattegrunnlag}
				/>
				<InntektstubVisning liste={inntektstub} loading={loading.inntektstub} />
				<InntektsmeldingVisning
					liste={inntektsmeldingData as any}
					ident={ident.ident}
					loading={loadingInntektsmeldingData}
					bestillingIdListe={bestillingIdListe}
					tilgjengeligMiljoe={tilgjengeligMiljoe || ''}
					bestillinger={
						harInntektsmeldingBestilling(bestillingerFagsystemer)
							? (inntektsmeldingBestilling as any)
							: null
					}
				/>
				<SkattekortVisning liste={skattekortData} loading={loadingSkattekort} />
				<ArbeidssoekerregisteretVisning
					data={arbeidssoekerregisteretData}
					loading={loadingArbeidssoekerregisteret}
				/>
				<ArbeidsplassenVisning
					data={arbeidsplassencvData}
					loading={loadingArbeidsplassencvData}
					error={arbeidsplassencvError}
					hjemmel={getArbeidsplassencvHjemmel()}
				/>
				<PensjonVisning
					data={poppData}
					loading={loadingPoppData}
					bestillingIdListe={bestillingIdListe}
					tilgjengeligMiljoe={tilgjengeligMiljoe}
				/>
				<PensjonsavtaleVisning
					data={pensjonsavtaleData}
					loading={loadingPensjonsavtaleData}
					bestillingIdListe={bestillingIdListe}
					tilgjengeligMiljoe={tilgjengeligMiljoe}
				/>
				<TpVisning
					data={tpDataForhold}
					loading={loadingTpDataForhold}
					bestillingIdListe={bestillingIdListe}
					tilgjengeligMiljoe={tilgjengeligMiljoe}
					ident={ident.ident}
				/>
				<AlderspensjonVisning
					data={apData}
					loading={loadingApData}
					bestillingIdListe={bestillingIdListe}
					tilgjengeligMiljoe={tilgjengeligMiljoe}
					ident={ident.ident}
				/>
				<UforetrygdVisning
					data={uforetrygdData}
					loading={loadingUforetrygdData}
					bestillingIdListe={bestillingIdListe}
					tilgjengeligMiljoe={tilgjengeligMiljoe}
				/>
				<AfpOffentligVisning
					data={afpOffentligData}
					loading={afpOffentligLoading}
					bestillingIdListe={bestillingIdListe}
					tilgjengeligMiljoe={tilgjengeligMiljoe || ''}
				/>
				<ArenaVisning
					data={arenaData}
					bestillingIdListe={bestillingIdListe}
					loading={loadingArenaData}
					ident={ident}
					tilgjengeligMiljoe={tilgjengeligMiljoe}
					harArenaBestilling={harArenaBestilling(bestillingerFagsystemer)}
				/>
				<SykemeldingVisning
					ident={ident}
					data={(sykemeldingData as any) || ([] as any)}
					loading={loadingSykemeldingData}
					bestillingIdListe={bestillingIdListe}
					tilgjengeligMiljoe={tilgjengeligMiljoe || ''}
					bestillinger={
						harSykemeldingBestilling(bestillingerFagsystemer)
							? (sykemeldingBestilling as any)
							: null
					}
				/>
				<YrkesskaderVisning data={yrkesskadeData as any} loading={loadingYrkesskadeData} />
				<BrregVisning data={brregstub} loading={loading.brregstub} />
				<InstVisning
					data={instData}
					loading={loadingInstData}
					bestillingIdListe={bestillingIdListe}
					tilgjengeligMiljoe={tilgjengeligMiljoe}
				/>
				<KrrVisning data={krrstub} loading={loading.krrstub} />
				<MedlVisning data={medl?.response as any} timedOutFagsystemer={timedOutFagsystemer} />
				<UdiVisning
					data={UdiVisning.filterValues(udistub, bestilling?.bestilling?.udistub)}
					loading={loadingUdistub}
					timedOutFagsystemer={timedOutFagsystemer}
				/>
				<DokarkivVisning
					data={dokarkivData}
					bestillingIdListe={bestillingIdListe}
					loading={loadingDokarkivData}
					tilgjengeligMiljoe={tilgjengeligMiljoe}
				/>
				<HistarkVisning data={histarkData} loading={loadingHistarkData} ident={ident.ident} />
				<SkatteetatenVisning
					data={tenorData?.data?.data?.dokumentListe?.find((dokument: any) =>
						dokument.identifikator?.includes(ident.ident),
					)}
					loading={loadingTenorData}
				/>
				<PersonMiljoeinfo
					bankIdBruker={brukertype === 'BANKID'}
					ident={ident.ident}
					miljoe={tilgjengeligMiljoe || ''}
				/>
				<PdlPersonMiljoeInfo
					bankIdBruker={brukertype === 'BANKID'}
					ident={ident.ident}
					miljoe={tilgjengeligMiljoe || ''}
				/>
				<TidligereBestillinger ids={ident.bestillingId} erOrg={false} />
				<BeskrivelseConnector ident={ident} closeModal={() => {}} />
				{isMalModalOpen && (
					<MalModal id={ident.ident} malType={malTyper.PERSON} closeModal={closeMalModal} />
				)}
			</div>
		</ErrorBoundary>
	)
}
