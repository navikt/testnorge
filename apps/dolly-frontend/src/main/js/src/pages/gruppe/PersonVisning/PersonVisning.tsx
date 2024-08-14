import React, { useEffect } from 'react'
import Button from '@/components/ui/button/Button'
import { TidligereBestillinger } from '@/pages/gruppe/PersonVisning/TidligereBestillinger/TidligereBestillinger'
import { PersonMiljoeinfo } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PersonMiljoeinfo'
import {
	AaregVisning,
	ArenaVisning,
	BrregVisning,
	DokarkivVisning,
	HistarkVisning,
	InntektsmeldingVisning,
	InntektstubVisning,
	InstVisning,
	KrrVisning,
	PensjonVisning,
	SigrunstubVisning,
	SykemeldingVisning,
	TpVisning,
	UdiVisning,
} from '@/components/fagsystem'
import BeskrivelseConnector from '@/components/beskrivelse/BeskrivelseConnector'
import { SlettButton } from '@/components/ui/button/SlettButton/SlettButton'
import { BestillingSammendragModal } from '@/components/bestilling/sammendrag/BestillingSammendragModal'
import './PersonVisning.less'
import { PdlPersonMiljoeInfo } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlPersonMiljoeinfo'
import PdlfVisningConnector from '@/components/fagsystem/pdlf/visning/PdlfVisningConnector'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { FrigjoerButton } from '@/components/ui/button/FrigjoerButton/FrigjoerButton'
import { useNavigate } from 'react-router-dom'
import { getBestillingsListe } from '@/ducks/bestillingStatus'
import { RelatertPersonImportButton } from '@/components/ui/button/RelatertPersonImportButton/RelatertPersonImportButton'
import { useAsync } from 'react-use'
import { DollyApi } from '@/service/Api'
import { GjenopprettPerson } from '@/components/bestilling/gjenopprett/GjenopprettPerson'
import { sjekkManglerBrregData } from '@/components/fagsystem/brregstub/visning/BrregVisning'
import { sjekkManglerPensjonData } from '@/components/fagsystem/pensjon/visning/PensjonVisning'
import { sjekkManglerAaregData } from '@/components/fagsystem/aareg/visning/Visning'
import { useAmeldinger, useArbeidsforhold } from '@/utils/hooks/useOrganisasjoner'
import {
	useArbeidsplassencvData,
	useArenaData,
	useDokarkivData,
	useHistarkData,
	useInstData,
	usePensjonsavtaleData,
	usePoppData,
	useTpData,
	useTransaksjonIdData,
} from '@/utils/hooks/useFagsystemer'
import { sjekkManglerTpData } from '@/components/fagsystem/tjenestepensjon/visning/TpVisning'
import { sjekkManglerInstData } from '@/components/fagsystem/inst/visning/InstVisning'
import {
	harAaregBestilling,
	harApBestilling,
	harArbeidsplassenBestilling,
	harArenaBestilling,
	harDokarkivBestilling,
	harHistarkBestilling,
	harInntektsmeldingBestilling,
	harInstBestilling,
	harMedlBestilling,
	harPensjonavtaleBestilling,
	harPoppBestilling,
	harSykemeldingBestilling,
	harTpBestilling,
	harUdistubBestilling,
	harUforetrygdBestilling,
} from '@/utils/SjekkBestillingFagsystem'
import {
	AlderspensjonVisning,
	sjekkManglerApData,
} from '@/components/fagsystem/alderspensjon/visning/AlderspensjonVisning'
import { ArbeidsplassenVisning } from '@/components/fagsystem/arbeidsplassen/visning/Visning'
import _has from 'lodash/has'
import { MedlVisning } from '@/components/fagsystem/medl/visning'
import { useMedlPerson } from '@/utils/hooks/useMedl'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import {
	sjekkManglerSykemeldingBestilling,
	sjekkManglerSykemeldingData,
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
import { PensjonsavtaleVisning } from '@/components/fagsystem/pensjonsavtale/visning/PensjonsavtaleVisning'

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

	const { loading: loadingAmelding, ameldinger } = useAmeldinger(
		ident.ident,
		harAaregBestilling(bestillingerFagsystemer) || ident?.master === 'PDL',
	)

	const { loading: loadingMedl, medl } = useMedlPerson(
		ident.ident,
		harMedlBestilling(bestillingerFagsystemer) || ident?.master === 'PDL',
	)

	const { loading: loadingUdistub, udistub } = useUdistub(
		ident.ident,
		harUdistubBestilling(bestillingerFagsystemer) || ident?.master === 'PDL',
	)

	const { loading: loadingTpData, tpData } = useTpData(
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

	const { loading: loadingSykemeldingData, data: sykemeldingData } = useTransaksjonIdData(
		ident.ident,
		'SYKEMELDING',
		harSykemeldingBestilling(bestillingerFagsystemer),
	)

	const sykemeldingBestilling = SykemeldingVisning.filterValues(bestillingListe, ident.ident)

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

	const getGruppeIdenter = () => {
		return useAsync(async () => DollyApi.getGruppeById(gruppeId), [DollyApi.getGruppeById])
	}

	const gruppeIdenter = getGruppeIdenter().value?.data?.identer?.map((person) => person.ident)

	const navigate = useNavigate()

	if (!data) {
		return null
	}

	const { sigrunstub, sigrunstubPensjonsgivende, inntektstub, brregstub, krrstub } = data

	const manglerFagsystemdata = () => {
		if (
			[sigrunstub, inntektstub, krrstub].some(
				(fagsystem) => Array.isArray(fagsystem) && !fagsystem.length,
			)
		) {
			return true
		}
		if (arbeidsforhold && sjekkManglerAaregData(arbeidsforhold) && visArbeidsforhold) {
			return true
		}
		if (poppData && sjekkManglerPensjonData(poppData)) {
			return true
		}
		if (tpData && sjekkManglerTpData(tpData)) {
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
		return false
	}

	const pdlRelatertPerson = () => {
		const relatertePersoner = []

		data.pdl?.hentPerson?.sivilstand
			?.filter(
				(siv) =>
					!siv?.metadata?.historisk &&
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
			?.filter(
				(forelderBarn) =>
					!forelderBarn?.metadata?.historisk &&
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
	const importerteRelatertePersoner = relatertePersoner?.filter((ident) =>
		gruppeIdenter?.includes(ident.id),
	)

	const getArbeidsplassencvHjemmel = () => {
		if (!harArbeidsplassenBestilling(bestillingerFagsystemer)) return null
		const arbeidsplassenBestillinger = bestillingListe.filter((bestilling) =>
			_has(bestilling.data, 'arbeidsplassenCV'),
		)
		return arbeidsplassenBestillinger?.[0]?.data?.arbeidsplassenCV?.harHjemmel
	}
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
				{visArbeidsforhold && (
					<AaregVisning
						ident={ident.ident}
						master={ident.master}
						liste={arbeidsforhold}
						ameldinger={ameldinger}
						loading={loadingAareg || loadingAmelding}
						bestillingIdListe={bestillingIdListe}
						bestillinger={ident.bestillinger}
						tilgjengeligMiljoe={tilgjengeligMiljoe}
					/>
				)}
				<SigrunstubVisning data={sigrunstub} loading={loading.sigrunstub} />
				<SigrunstubPensjonsgivendeVisning
					data={sigrunstubPensjonsgivende}
					loading={loading.sigrunstubPensjonsgivende}
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
					data={tpData}
					loading={loadingTpData}
					bestillingIdListe={bestillingIdListe}
					tilgjengeligMiljoe={tilgjengeligMiljoe}
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
				<ArenaVisning
					data={arenaData}
					bestillingIdListe={bestillingIdListe}
					loading={loadingArenaData}
					ident={ident}
					tilgjengeligMiljoe={tilgjengeligMiljoe}
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
				<HistarkVisning data={histarkData} loading={loadingHistarkData} />
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
