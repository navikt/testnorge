import React, { useEffect, useState } from 'react'
import { sokSelector } from '~/ducks/bestillingStatus'
import Hjelpetekst from '~/components/hjelpetekst'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import Icon from '~/components/ui/icon/Icon'
import { SearchField } from '~/components/searchField/SearchField'
import Loading from '~/components/ui/loading/Loading'
import { History } from 'history'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import OrganisasjonBestilling from './OrganisasjonBestilling'
import StatusListeConnector from '~/components/bestilling/statusListe/StatusListeConnector'
import OrganisasjonListe from './OrganisasjonListe'
import { EnhetBestilling } from '~/components/fagsystem/organisasjoner/types'
import _isEmpty from 'lodash/isEmpty'
import { dollySlack } from '~/components/dollySlack/dollySlack'
import TomOrgListe from './TomOrgliste'

type Organisasjoner = {
	history: History
	state: any
	isFetching: boolean
	bestillinger: Array<EnhetBestilling>
	organisasjoner: Array<Organisasjon>
	brukerId: string
	getOrganisasjonBestillingStatus: Function
	getOrganisasjonBestilling: Function
	fetchOrganisasjoner: Function
	search?: string
}

type Organisasjon = {
	organisasjonsnummer: string
	id: string
	organisasjonsnavn: string
	enhetstype: string
}

enum BestillingType {
	NY = 'NY',
	STANDARD = 'STANDARD'
}

const VISNING_ORGANISASJONER = 'organisasjoner'
const VISNING_BESTILLINGER = 'bestillinger'

export default function Organisasjoner({
	history,
	state,
	search,
	isFetching,
	bestillinger,
	organisasjoner,
	brukerId,
	getOrganisasjonBestillingStatus,
	getOrganisasjonBestilling,
	fetchOrganisasjoner
}: Organisasjoner) {
	const [visning, setVisning] = useState(VISNING_ORGANISASJONER)
	const byttVisning = (event: React.ChangeEvent<any>) => setVisning(event.target.value)

	useEffect(() => {
		getOrganisasjonBestillingStatus(brukerId)
		getOrganisasjonBestilling(brukerId)
	}, [organisasjoner?.length])

	useEffect(() => {
		fetchOrganisasjoner(brukerId)
	}, [])

	const searchfieldPlaceholderSelector = () => {
		if (visning === VISNING_BESTILLINGER) return 'Søk i bestillinger'
		return 'Søk i organisasjoner'
	}

	const antallOrg = organisasjoner?.length
	const antallBest = bestillinger?.length

	const startBestilling = (type: string) => {
		history.push('/organisasjoner/bestilling', { opprettOrganisasjon: type })
	}

	const sokSelectorOrg = (items: Array<Organisasjon>, searchStr: string) => {
		if (!items) return []
		if (!searchStr) return items

		const query = searchStr.toLowerCase()
		return items.filter(item =>
			Object.values(item).some(v =>
				(v || '')
					.toString()
					.toLowerCase()
					.includes(query)
			)
		)
	}

	const hentOrgStatus = (bestillinger: Array<EnhetBestilling>, bestillingId: string) => {
		if (!bestillinger) return null
		let orgStatus = 'Ferdig'
		const bestilling = bestillinger.find(obj => {
			return obj.id === bestillingId
		})
		if (!bestilling?.status) orgStatus = 'Feilet'
		bestilling?.status?.[0].statuser?.forEach(status => {
			if (status?.melding !== 'OK') orgStatus = 'Avvik'
		})
		return orgStatus
	}

	function getBestillingIdFromOrgnummer(
		bestillinger: Array<EnhetBestilling>,
		organisasjonsnummer: string
	) {
		return bestillinger
			.filter(org => org.organisasjonNummer === organisasjonsnummer)
			.map(org => org.id)
			.sort(function(a: number, b: number) {
				return b - a
			})
	}

	const mergeList = (organisasjoner: Array<Organisasjon>, bestillinger: Array<EnhetBestilling>) => {
		if (_isEmpty(organisasjoner)) return null

		return organisasjoner.map(orgInfo => {
			const bestillingId = getBestillingIdFromOrgnummer(bestillinger, orgInfo.organisasjonsnummer)
			return {
				orgInfo,
				id: orgInfo.id,
				organisasjonsnummer: orgInfo.organisasjonsnummer,
				organisasjonsnavn: orgInfo.organisasjonsnavn,
				enhetstype: orgInfo.enhetstype,
				status: hentOrgStatus(bestillinger, bestillingId[0]),
				bestillingId: bestillingId
			}
		})
	}

	const filterOrg = () => sokSelectorOrg(mergeList(organisasjoner, bestillinger), search)
	const filterBest = () => sokSelector(state, search)

	return (
		<ErrorBoundary>
			<div className="oversikt-container">
				<div className="toolbar">
					<div className="page-header flexbox--align-center">
						<h1>Testorganisasjoner</h1>
						{/* @ts-ignore */}
						<Hjelpetekst hjelpetekstFor="Testorganisasjoner" type="under">
							Organisasjoner i Dolly er en del av NAVs syntetiske testpopulasjon og dekker behov for
							testdata knyttet til bedrifter/virksomheter (EREG). Løsningen er under utvikling, og
							det legges til ny funksjonalitet fortløpende.
							<br />
							På denne siden finner du en oversikt over dine egne testorganisasjoner. Du kan
							opprette nye organisasjoner ved å trykke på knappen under.
							<br />
							Kontakt oss gjerne på {dollySlack} dersom du har spørsmål eller innspill.
						</Hjelpetekst>
					</div>
				</div>

				<StatusListeConnector brukerId={brukerId} />

				<div className="toolbar">
					<NavButton type="hoved" onClick={() => startBestilling(BestillingType.NY)}>
						Opprett organisasjon
					</NavButton>

					<ToggleGruppe onChange={byttVisning} name="toggler">
						<ToggleKnapp
							value={VISNING_ORGANISASJONER}
							checked={visning === VISNING_ORGANISASJONER}
						>
							<Icon
								size={13}
								kind={visning === VISNING_ORGANISASJONER ? 'organisasjonLight' : 'organisasjon'}
							/>
							{`Organisasjoner (${antallOrg ? antallOrg : 0})`}
						</ToggleKnapp>
						<ToggleKnapp value={VISNING_BESTILLINGER} checked={visning === VISNING_BESTILLINGER}>
							<Icon
								size={13}
								kind={visning === VISNING_BESTILLINGER ? 'bestillingLight' : 'bestilling'}
							/>
							{`Bestillinger (${antallBest ? antallBest : 0})`}
						</ToggleKnapp>
					</ToggleGruppe>

					<SearchField placeholder={searchfieldPlaceholderSelector()} />
				</div>

				{visning === VISNING_ORGANISASJONER &&
					(isFetching || antallOrg === undefined ? (
						<Loading label="Laster organisasjoner" panel />
					) : antallOrg > 0 ? (
						<OrganisasjonListe bestillinger={bestillinger} organisasjoner={filterOrg()} />
					) : (
						<TomOrgListe
							startBestilling={startBestilling}
							bestillingstype={BestillingType.STANDARD}
						/>
					))}
				{visning === VISNING_BESTILLINGER &&
					(isFetching ? (
						<Loading label="Laster bestillinger" panel />
					) : antallBest > 0 ? (
						<OrganisasjonBestilling brukerId={brukerId} bestillinger={filterBest()} />
					) : (
						<TomOrgListe
							startBestilling={startBestilling}
							bestillingstype={BestillingType.STANDARD}
						/>
					))}
			</div>
		</ErrorBoundary>
	)
}
