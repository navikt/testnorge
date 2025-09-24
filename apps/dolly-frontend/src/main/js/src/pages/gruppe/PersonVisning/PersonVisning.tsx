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
import { UdiVisning } from '@/components/fagsystem/udistub/visning/UdiVisning'

import DokarkivVisning from '@/components/fagsystem/dokarkiv/visning/Visning'
import HistarkVisning from '@/components/fagsystem/histark/visning/Visning'
import { useArbeidssoekerregistrering } from '@/utils/hooks/useArbeidssoekerregisteret'
import { ArbeidssoekerregisteretVisning } from '@/components/fagsystem/arbeidssoekerregisteret/visning/ArbeidssoekerregisteretVisning'
import { usePensjonsgivendeInntekt, useSummertSkattegrunnlag } from '@/utils/hooks/useSigrunstub'
import { SigrunstubSummertSkattegrunnlagVisning } from '@/components/fagsystem/sigrunstubSummertSkattegrunnlag/visning/Visning'
import { useNomData } from '@/utils/hooks/useNom'
import { NavAnsattVisning } from '@/components/fagsystem/nom/visning/Visning'

const getIdenttype = (ident) => {
	if (parseInt(ident.charAt(0)) > 3) {
		return 'DNR'
	} else if (parseInt(ident.charAt(2)) % 4 >= 2) {
		return 'NPID'
	} else {
		return 'FNR'
	}
}

export default ({
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
}) => {
	const { gruppeId } = ident

	const [isMalModalOpen, openMalModal, closeMalModal] = useBoolean(false)

	const { organisasjonMiljoe } = useOrganisasjonMiljoe()
	const tilgjengeligMiljoe = organisasjonMiljoe?.miljoe

	const bestillinger = []

	if (ident.bestillinger) {
		ident.bestillinger.map((b) => {
			bestillinger[b.id] = b
		})
	}
	const bestillingListe = getBestillingsListe(bestillinger, bestillingIdListe)
	const bestilling = bestillinger?.[bestillingIdListe?.[0]]

	useEffect(() => {
		fetchDataFraFagsystemer(bestillinger)
	}, [])

	const bestillingerFagsystemer = ident?.bestillinger?.map((i) => i.bestilling)

	const { loading: loadingAareg, arbeidsforhold } = useArbeidsforhold(
		ident.ident,
		harAaregBestilling(bestillingerFagsystemer) || ident?.master === 'PDL',
	)

	const visArbeidsforhold =
		ident?.master !== 'PDL' || arbeidsforhold?.some((miljodata) => miljodata?.data?.length > 0)

	const { loading: loadingSkattekort, skattekortData } = useSkattekort(
		ident.ident,
		harSkattekortBestilling(bestillingerFagsystemer),
	)

	const { loading: loadingMedl, medl } = useMedlPerson(
		ident.ident,
		harMedlBestilling(bestillingerFagsystemer) || ident?.master === 'PDL',
	)

	const { loading: loadingUdistub, udistub } = useUdistub(
		ident.ident,
		harUdistubBestilling(bestillingerFagsystemer) || ident?.master === 'PDL',
	)

	const {
		loading: loadingSigrunstubPensjonsgivendeInntekt,
		data: sigrunstubPensjonsgivendeInntekt,
	} = usePensjonsgivendeInntekt(
		ident.ident,
		harSigrunstubPensjonsgivendeInntekt(bestillingerFagsystemer) || ident?.master === 'PDL',
	)

	const { loading: loadingSigrunstubSummertSkattegrunnlag, data: sigrunstubSummertSkattegrunnlag } =
		useSummertSkattegrunnlag(
			ident.ident,
			harSigrunstubSummertSkattegrunnlag(bestillingerFagsystemer) || ident?.master === 'PDL',
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

	const { loading: loadingDokarkivData, dokarkivData } = useDokarkivData(
		ident.ident,
		harDokarkivBestilling(bestillingerFagsystemer),
	)

	const { loading: loadingHistarkData, histarkData } = useHistarkData(
		ident.ident,
		harHistarkBestilling(bestillingerFagsystemer),
	)

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

	const { loading: loadingApData, data: apData } = useTransaksjonIdData(
		ident.ident,
		'PEN_AP',
		harApBestilling(bestillingerFagsystemer),
		pensjonEnvironments,
	)

	const { loading: loadingUforetrygdData, data: uforetrygdData } = useTransaksjonIdData(
		ident.ident,
		'PEN_UT',
		harUforetrygdBestilling(bestillingerFagsystemer),
		pensjonEnvironments,
	)

	const { mockOppsett: afpOffentligData, loading: afpOffentligLoading } = useMockOppsett(
		pensjonEnvironments,
		ident.ident,
		harAfpOffentligBestilling(bestillingerFagsystemer),
	)

	const { loading: loadingSykemeldingData, data: sykemeldingData } = useTransaksjonIdData(
		ident.ident,
		'SYKEMELDING',
		harSykemeldingBestilling(bestillingerFagsystemer),
	)

	const sykemeldingBestilling = SykemeldingVisning.filterValues(bestillingListe, ident.ident)

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
		bestillingListe,
		ident.ident,
	)

	const { person: tenorData, loading: loadingTenorData } = useTenorIdent(
		ident?.master === 'PDL' ? ident.ident : null,
	)

	const { nomData, loading: loadingNom } = useNomData(ident.ident)

	const getGruppeIdenter = () => {
		return useAsync(async () => DollyApi.getGruppeById(gruppeId), [DollyApi.getGruppeById])
	}

	const gruppeIdenter = getGruppeIdenter().value?.data?.identer?.map((person) => person.ident)

	const navigate = useNavigate()

	if (!data) {
		return null
	}

	const { inntektstub, brregstub, krrstub } = data

	const manglerFagsystemdata = () => {
		if ([inntektstub, krrstub].some((fagsystem) => Array.isArray(fagsystem) && !fagsystem.length)) {
			return true
		}
		if (arbeidsforhold && sjekkManglerAaregData(arbeidsforhold) && visArbeidsforhold) {
			return true
		}
		if (poppData && sjekkManglerPensjonData(poppData)) {
			return true
		}
		if (tpDataForhold && sjekkManglerTpData(tpDataForhold)) {
			return true
		}
		if (apData && sjekkManglerApData(apData)) {
			return true
		}
		if (uforetrygdData && sjekkManglerUforetrygdData(uforetrygdData)) {
			return true
		}
		if (brregstub && sjekkManglerBrregData(brregstub)) {
			return true
		}
		if (instData && sjekkManglerInstData(instData)) {
			return true
		}
		if (
			sykemeldingData &&
			sjekkManglerSykemeldingData(sykemeldingData) &&
			harSykemeldingBestilling(bestillingerFagsystemer) &&
			sjekkManglerSykemeldingBestilling(sykemeldingBestilling)
		) {
			return true
		}
		if (yrkesskadeData && sjekkManglerYrkesskadeData(yrkesskadeData)) {
			return true
		}
		return false
	}

	const pdlRelatertPerson = () => {
		const relatertePersoner = []

		data.pdl?.hentPerson?.sivilstand
			?.filter((siv) =>
				['GIFT', 'REGISTRERT_PARTNER', 'SEPARERT', 'SEPARERT_PARTNER'].includes(siv?.type),
			)
			?.forEach((person) => {
				relatertePersoner.push({
					type: 'PARTNER',
					id: person.relatertVedSivilstand,
				})
			})

		data.pdlforvalter?.person?.sivilstand
			?.filter((siv) => siv?.type === 'SAMBOER')
			?.forEach((person) => {
				relatertePersoner.push({
					type: 'SAMBOER',
					id: person.relatertVedSivilstand,
				})
			})

		data.pdl?.hentPerson?.forelderBarnRelasjon
			?.filter((forelderBarn) =>
				['BARN', 'MOR', 'MEDMOR', 'FAR'].includes(forelderBarn?.relatertPersonsRolle),
			)
			?.forEach((person) => {
				relatertePersoner.push({
					type: person.relatertPersonsRolle,
					id: person.relatertPersonsIdent,
				})
			})

		data.pdl?.hentPerson?.foreldreansvar
			?.filter((foreldreansvar) => foreldreansvar.ansvarlig)
			?.forEach((person) => {
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

	const isLoadingFagsystemer = loadingAareg || loadingArbeidsplassencvData || loadingArenaData

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
								if (tmpPersoner?.skjermingsregister?.hasOwnProperty(ident.ident)) {
									personData.skjermingsregister = tmpPersoner.skjermingsregister[ident.ident]
								}
								if (nomData) {
									personData.nomdata = nomData
								}
								if (arbeidsforhold) {
									personData.aareg = arbeidsforhold
								}
								if (arbeidsplassencvData) {
									personData.arbeidsplassenCV = { harHjemmel: getArbeidsplassencvHjemmel() }
								}
								if (arenaData) {
									personData.arenaforvalteren = arenaData
								}
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
				<NavAnsattVisning
					nomData={nomData}
					nomLoading={loadingNom}
					skjermingData={data.skjermingsregister}
					skjermingLoading={loading}
				/>
				{visArbeidsforhold && (
					<AaregVisning
						liste={arbeidsforhold}
						loading={loadingAareg}
						bestillingIdListe={bestillingIdListe}
						bestillinger={ident.bestillinger}
						tilgjengeligMiljoe={tilgjengeligMiljoe}
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
					data={inntektsmeldingData}
					loading={loadingInntektsmeldingData}
					bestillingIdListe={bestillingIdListe}
					tilgjengeligMiljoe={tilgjengeligMiljoe}
					bestillinger={
						harInntektsmeldingBestilling(bestillingerFagsystemer) ? inntektsmeldingBestilling : null
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
					tilgjengeligMiljoe={tilgjengeligMiljoe}
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
					data={sykemeldingData}
					loading={loadingSykemeldingData}
					bestillingIdListe={bestillingIdListe}
					tilgjengeligMiljoe={tilgjengeligMiljoe}
					bestillinger={
						harSykemeldingBestilling(bestillingerFagsystemer) ? sykemeldingBestilling : null
					}
				/>
				<YrkesskaderVisning data={yrkesskadeData} loading={loadingYrkesskadeData} />
				<BrregVisning data={brregstub} loading={loading.brregstub} />
				<InstVisning
					data={instData}
					loading={loadingInstData}
					bestillingIdListe={bestillingIdListe}
					tilgjengeligMiljoe={tilgjengeligMiljoe}
				/>
				<KrrVisning data={krrstub} loading={loading.krrstub} />
				<MedlVisning data={medl} loading={loadingMedl} />
				<UdiVisning
					data={UdiVisning.filterValues(udistub, bestilling?.bestilling?.udistub)}
					loading={loadingUdistub}
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
					miljoe={tilgjengeligMiljoe}
				/>
				<PdlPersonMiljoeInfo
					bankIdBruker={brukertype === 'BANKID'}
					ident={ident.ident}
					miljoe={tilgjengeligMiljoe}
				/>
				<TidligereBestillinger ids={ident.bestillingId} />
				<BeskrivelseConnector ident={ident} />
				{isMalModalOpen && (
					<MalModal id={ident.ident} malType={malTyper.PERSON} closeModal={closeMalModal} />
				)}
			</div>
		</ErrorBoundary>
	)
}
