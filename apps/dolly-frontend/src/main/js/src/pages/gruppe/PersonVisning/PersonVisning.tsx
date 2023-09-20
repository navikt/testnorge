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
import { PdlVisning } from '@/components/fagsystem/pdl/visning/PdlVisning'
import PdlfVisningConnector from '@/components/fagsystem/pdlf/visning/PdlfVisningConnector'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { FrigjoerButton } from '@/components/ui/button/FrigjoerButton/FrigjoerButton'
import { useNavigate } from 'react-router-dom'
import { getBestillingsListe } from '@/ducks/bestillingStatus'
import { RelatertPersonImportButton } from '@/components/ui/button/RelatertPersonImportButton/RelatertPersonImportButton'
import { useAsync } from 'react-use'
import { DollyApi } from '@/service/Api'
import { GjenopprettPerson } from '@/components/bestilling/gjenopprett/GjenopprettPerson'
import { sjekkManglerUdiData } from '@/components/fagsystem/udistub/visning/UdiVisning'
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
	usePoppData,
	useTpData,
	useTransaksjonidData,
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
	harInstBestilling,
	harMedlBestilling,
	harPoppBestilling,
	harSykemeldingBestilling,
	harTpBestilling,
	harUforetrygdBestilling,
} from '@/utils/SjekkBestillingFagsystem'
import {
	AlderspensjonVisning,
	sjekkManglerApData,
} from '@/components/fagsystem/alderspensjon/visning/AlderspensjonVisning'
import { useOrganisasjonTilgang } from '@/utils/hooks/useBruker'
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
	slettPersonOgRelatertePersoner,
	leggTilPaaPerson,
	iLaastGruppe,
	tmpPersoner,
}) => {
	const { gruppeId } = ident

	const { organisasjonTilgang } = useOrganisasjonTilgang()
	const tilgjengeligMiljoe = organisasjonTilgang?.miljoe

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

	const { loading: loadingAmelding, ameldinger } = useAmeldinger(
		ident.ident,
		harAaregBestilling(bestillingerFagsystemer) || ident?.master === 'PDL',
	)

	const { loading: loadingMedl, medl } = useMedlPerson(
		ident.ident,
		harMedlBestilling(bestillingerFagsystemer) || ident?.master === 'PDL',
	)

	const visArbeidsforhold =
		ident?.master !== 'PDL' || arbeidsforhold?.some((miljodata) => miljodata?.data?.length > 0)

	const { loading: loadingTpData, tpData } = useTpData(
		ident.ident,
		harTpBestilling(bestillingerFagsystemer),
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

	const { loading: loadingSykemeldingData, data: sykemeldingData } = useTransaksjonidData(
		ident.ident,
		'SYKEMELDING',
		harSykemeldingBestilling(bestillingerFagsystemer),
	)

	const sykemeldingBestilling = SykemeldingVisning.filterValues(bestillingListe, ident.ident)

	const getGruppeIdenter = () => {
		return useAsync(async () => DollyApi.getGruppeById(gruppeId), [DollyApi.getGruppeById])
	}

	const gruppeIdenter = getGruppeIdenter().value?.data?.identer?.map((person) => person.ident)

	const navigate = useNavigate()

	if (!data) {
		return null
	}

	const { sigrunstub, inntektstub, brregstub, krrstub, udistub } = data

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
		if (udistub && sjekkManglerUdiData(udistub)) {
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
	const importerteRelatertePersoner = relatertePersoner?.filter(
		(ident) => gruppeIdenter?.includes(ident.id),
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
					<BestillingSammendragModal bestilling={bestilling} />
					{!iLaastGruppe && ident.master !== 'PDL' && (
						<SlettButton action={slettPerson} loading={loading.slettPerson}>
							Er du sikker på at du vil slette denne personen?
						</SlettButton>
					)}
					{!iLaastGruppe && ident.master === 'PDL' && (
						<FrigjoerButton
							slettPerson={slettPerson}
							slettPersonOgRelatertePersoner={slettPersonOgRelatertePersoner}
							loading={loading.slettPerson || loading.slettPersonOgRelatertePersoner}
							importerteRelatertePersoner={
								importerteRelatertePersoner.length > 0 ? importerteRelatertePersoner : null
							}
						/>
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
					<PdlVisning pdlData={data.pdl} fagsystemData={data} loading={loading} />
				)}
				{visArbeidsforhold && (
					<AaregVisning
						ident={ident.ident}
						liste={arbeidsforhold}
						ameldinger={ameldinger}
						loading={loadingAareg || loadingAmelding}
						bestillingIdListe={bestillingIdListe}
						bestillinger={ident.bestillinger}
						tilgjengeligMiljoe={tilgjengeligMiljoe}
					/>
				)}
				<SigrunstubVisning data={sigrunstub} loading={loading.sigrunstub} />
				<InntektstubVisning liste={inntektstub} loading={loading.inntektstub} />
				<InntektsmeldingVisning
					liste={InntektsmeldingVisning.filterValues(bestillingListe, ident.ident)}
					ident={ident.ident}
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
				{/*<SykemeldingVisning data={SykemeldingVisning.filterValues(bestillingListe, ident.ident)} />*/}
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
					data={UdiVisning.filterValues(udistub, bestilling?.bestilling.udistub)}
					loading={loading.udistub}
				/>
				<DokarkivVisning
					data={dokarkivData}
					bestillingIdListe={bestillingIdListe}
					loading={loadingDokarkivData}
					tilgjengeligMiljoe={tilgjengeligMiljoe}
				/>
				<HistarkVisning data={histarkData} loading={loadingHistarkData} />
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
			</div>
		</ErrorBoundary>
	)
}
