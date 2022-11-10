import React, { useEffect, useRef } from 'react'
import Button from '~/components/ui/button/Button'
import { TidligereBestillinger } from './TidligereBestillinger/TidligereBestillinger'
import { PersonMiljoeinfo } from './PersonMiljoeinfo/PersonMiljoeinfo'
import {
	AaregVisning,
	ArenaVisning,
	BrregVisning,
	DokarkivVisning,
	InntektsmeldingVisning,
	InntektstubVisning,
	InstVisning,
	KrrVisning,
	PensjonVisning,
	SigrunstubVisning,
	SykemeldingVisning,
	TpsfVisning,
	TpVisning,
	UdiVisning,
} from '~/components/fagsystem'
import BeskrivelseConnector from '~/components/beskrivelse/BeskrivelseConnector'
import { SlettButton } from '~/components/ui/button/SlettButton/SlettButton'
import { BestillingSammendragModal } from '~/components/bestilling/sammendrag/BestillingSammendragModal'
import './PersonVisning.less'
import { PdlPersonMiljoeInfo } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlPersonMiljoeinfo'
import { PdlVisning } from '~/components/fagsystem/pdl/visning/PdlVisning'
import PdlfVisningConnector from '~/components/fagsystem/pdlf/visning/PdlfVisningConnector'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { FrigjoerButton } from '~/components/ui/button/FrigjoerButton/FrigjoerButton'
import { useNavigate } from 'react-router-dom'
import { getBestillingsListe } from '~/ducks/bestillingStatus'
import { RelatertPersonImportButton } from '~/components/ui/button/RelatertPersonImportButton/RelatertPersonImportButton'
import { useAsync } from 'react-use'
import { DollyApi } from '~/service/Api'
import { Alert } from '@navikt/ds-react'
import styled from 'styled-components'
import { GjenopprettPerson } from '~/components/bestilling/gjenopprett/GjenopprettPerson'
import { sjekkManglerUdiData } from '~/components/fagsystem/udistub/visning/UdiVisning'
import { sjekkManglerBrregData } from '~/components/fagsystem/brregstub/visning/BrregVisning'
import { sjekkManglerPensjonData } from '~/components/fagsystem/pensjon/visning/PensjonVisning'
import { useArbeidsforhold } from '~/utils/hooks/useOrganisasjoner'
import { useTpData } from '~/utils/hooks/useFagsystemer'
import { useBestilteMiljoer } from '~/utils/hooks/useBestilling'

const StyledAlert = styled(Alert)`
	margin-bottom: 20px;

	.navds-alert__wrapper {
		max-width: 100rem;
	}
`

const getIdenttype = (ident) => {
	if (parseInt(ident.charAt(0)) > 3) {
		return 'DNR'
	} else if (parseInt(ident.charAt(2)) % 4 >= 2) {
		return 'NPID'
	} else {
		return 'FNR'
	}
}

export const PersonVisning = ({
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
	console.log('ident: ', ident) //TODO - SLETT MEG

	const harAaregBestilling = () => {
		let aareg = false
		bestillingerFagsystemer?.forEach((i) => {
			if (i.aareg) {
				aareg = true
			}
		})
		return aareg
	}

	const { loading: loadingAareg, arbeidsforhold } = useArbeidsforhold(
		ident.ident,
		harAaregBestilling()
	)

	console.log('bestillingerFagsystemer: ', bestillingerFagsystemer) //TODO - SLETT MEG
	const harTpBestilling = () => {
		let tp = false
		bestillingerFagsystemer?.forEach((i) => {
			if (i.pensjonforvalter?.tp) {
				tp = true
			}
		})
		return tp
	}

	const { loading: loadingTpData, tpData } = useTpData(ident.ident, harTpBestilling())

	const getGruppeIdenter = () => {
		return useAsync(async () => DollyApi.getGruppeById(gruppeId), [DollyApi.getGruppeById])
	}

	const gruppeIdenter = getGruppeIdenter().value?.data?.identer?.map((person) => person.ident)

	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe)

	const mountedRef = useRef(true)
	const navigate = useNavigate()

	useEffect(() => {
		return () => {
			mountedRef.current = false
		}
	}, [])

	if (!data) {
		return null
	}

	const {
		sigrunstub,
		pensjonforvalter,
		inntektstub,
		brregstub,
		krrstub,
		instdata,
		arenaforvalteren,
		udistub,
	} = data

	const manglerFagsystemdata = () => {
		if (
			[sigrunstub, inntektstub, krrstub, instdata].some(
				(fagsystem) => Array.isArray(fagsystem) && !fagsystem.length
			)
		) {
			return true
		}

		if (pensjonforvalter && sjekkManglerPensjonData(pensjonforvalter)) {
			return true
		}

		if (brregstub && sjekkManglerBrregData(brregstub)) {
			return true
		}

		if (udistub && sjekkManglerUdiData(udistub)) {
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
					['GIFT', 'REGISTRERT_PARTNER', 'SEPARERT', 'SEPARERT_PARTNER'].includes(siv?.type)
			)
			?.forEach((person) => {
				relatertePersoner.push({
					type: 'PARTNER',
					id: person.relatertVedSivilstand,
				})
			})

		data.pdl?.hentPerson?.forelderBarnRelasjon
			?.filter(
				(forelderBarn) =>
					!forelderBarn?.metadata?.historisk &&
					['BARN', 'MOR', 'MEDMOR', 'FAR'].includes(forelderBarn?.relatertPersonsRolle)
			)
			?.forEach((person) => {
				relatertePersoner.push({
					type: person.relatertPersonsRolle,
					id: person.relatertPersonsIdent,
				})
			})

		return relatertePersoner
	}

	const harPdlRelatertPerson = pdlRelatertPerson().length > 0
	const importerteRelatertePersoner = pdlRelatertPerson().filter((ident) =>
		gruppeIdenter?.includes(ident.id)
	)

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
								leggTilPaaPerson(
									personData,
									bestillingListe,
									ident.master,
									getIdenttype(ident.ident),
									gruppeId,
									navigate
								)
							}}
							kind="add-circle"
						>
							LEGG TIL/ENDRE
						</Button>
					)}
					<GjenopprettPerson ident={ident?.ident} />
					{!iLaastGruppe && harPdlRelatertPerson && (
						<RelatertPersonImportButton
							gruppeId={gruppeId}
							relatertPersonIdenter={pdlRelatertPerson()}
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
						Forsøk å gjenopprette personen for å fikse dette, og ta eventuelt kontakt med team Dolly
						dersom problemet vedvarer.
					</StyledAlert>
				)}
				{ident.master !== 'PDL' && (
					<PdlfVisningConnector
						data={data.pdlforvalter}
						tpsfData={TpsfVisning.filterValues(data.tpsf, bestillingListe)}
						skjermingData={data.skjermingsregister}
						loading={loading.pdlforvalter}
						environments={bestilling?.environments}
						master={ident.master}
					/>
				)}
				{ident.master === 'PDL' && (
					<PdlVisning pdlData={data.pdl} environments={bestilling?.environments} />
				)}
				<AaregVisning
					ident={ident.ident}
					liste={arbeidsforhold}
					loading={loadingAareg}
					bestilteMiljoer={bestilteMiljoer}
				/>
				<SigrunstubVisning data={sigrunstub} loading={loading.sigrunstub} />
				<PensjonVisning
					data={pensjonforvalter}
					loading={loading.pensjonforvalter}
					bestilteMiljoer={bestilteMiljoer}
				/>
				<TpVisning data={tpData} loading={loadingTpData} bestilteMiljoer={bestilteMiljoer} />
				<InntektstubVisning liste={inntektstub} loading={loading.inntektstub} />
				<InntektsmeldingVisning
					liste={InntektsmeldingVisning.filterValues(bestillingListe, ident.ident)}
					ident={ident.ident}
				/>
				<SykemeldingVisning data={SykemeldingVisning.filterValues(bestillingListe, ident.ident)} />
				<BrregVisning data={brregstub} loading={loading.brregstub} />
				<KrrVisning data={krrstub} loading={loading.krrstub} />
				<InstVisning data={instdata} loading={loading.instdata} />
				<ArenaVisning
					data={arenaforvalteren}
					bestillinger={bestillingListe}
					loading={loading.arenaforvalteren}
					ident={ident}
				/>
				<UdiVisning
					data={UdiVisning.filterValues(udistub, bestilling?.bestilling.udistub)}
					loading={loading.udistub}
				/>
				<DokarkivVisning ident={ident.ident} />
				<PersonMiljoeinfo bankIdBruker={brukertype === 'BANKID'} ident={ident.ident} />
				<PdlPersonMiljoeInfo ident={ident.ident} />
				<TidligereBestillinger ids={ident.bestillingId} />
				<BeskrivelseConnector ident={ident} />
			</div>
		</ErrorBoundary>
	)
}
