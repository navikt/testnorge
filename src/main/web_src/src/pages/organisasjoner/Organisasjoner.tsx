import React, { useState, useEffect } from 'react'
import Hjelpetekst from '~/components/hjelpetekst'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import Icon from '~/components/ui/icon/Icon'
import { SearchField } from '~/components/searchField/SearchField'
import OrganisasjonListeConnector from './OrganisasjonListeConnector'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import Loading from '~/components/ui/loading/Loading'
import { History } from 'history'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import OrganisasjonBestillingConnector from '~/pages/organisasjoner/OrganisasjonBestillingConnector'
import StatusListeConnector from '~/components/bestilling/statusListe/StatusListeConnector'

type Organisasjoner = {
	history: History
	isFetchingBestillinger: boolean
	isFetchingOrg: boolean
	bestillinger: Array<Bestilling>
	organisasjoner: Array<Organisasjon>
	brukerId: string
	getOrganisasjonBestillingStatus: Function
	getOrganisasjonBestilling: Function
	fetchOrganisasjoner: Function
}

type Bestilling = {
	id: number
}

type Organisasjon = {
	organisasjonsnummer: string
}

enum BestillingType {
	NY = 'NY',
	STANDARD = 'STANDARD'
}

const VISNING_ORGANISASJONER = 'organisasjoner'
const VISNING_BESTILLINGER = 'bestillinger'

export default function Organisasjoner({
	history,
	isFetchingBestillinger,
	isFetchingOrg,
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
		fetchOrganisasjoner(brukerId)
	}, [])

	const searchfieldPlaceholderSelector = () => {
		if (visning === VISNING_BESTILLINGER) return 'Søk i bestillinger'
		return 'Søk i organisasjoner'
	}

	const antallOrg = organisasjoner ? organisasjoner.length : 0
	const antallBest = bestillinger ? bestillinger.length : 0

	const startBestilling = (type: string) => {
		history.push('/organisasjoner/bestilling', { opprettOrganisasjon: type })
	}

	const dollySlack = (
		<a href="https://nav-it.slack.com/archives/CA3P9NGA2" target="_blank">
			#dolly
		</a>
	)

	const tomOrgListe = () => (
		<ContentContainer>
			<p>
				Du har for øyeblikket ingen testorganisasjoner. Trykk på knappen under for å opprette en
				testorganisasjon med standard oppsett.
			</p>
			<NavButton
				type="standard"
				onClick={() => startBestilling(BestillingType.STANDARD)}
				style={{ marginTop: '10px' }}
			>
				Opprett standard organisasjon
			</NavButton>
		</ContentContainer>
	)

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
							Kontakt oss gjerne på {dollySlack} dersom du har spørsmål eller innspill
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
							{`Organisasjoner (${antallOrg})`}
						</ToggleKnapp>
						<ToggleKnapp value={VISNING_BESTILLINGER} checked={visning === VISNING_BESTILLINGER}>
							<Icon
								size={13}
								kind={visning === VISNING_BESTILLINGER ? 'bestillingLight' : 'bestilling'}
							/>
							{`Bestillinger (${antallBest})`}
						</ToggleKnapp>
					</ToggleGruppe>

					<SearchField placeholder={searchfieldPlaceholderSelector()} />
				</div>

				{visning === VISNING_ORGANISASJONER &&
					(isFetchingOrg !== false ? (
						<Loading label="laster organisasjoner" panel />
					) : antallOrg > 0 ? (
						<OrganisasjonListeConnector />
					) : (
						tomOrgListe()
					))}
				{visning === VISNING_BESTILLINGER &&
					(isFetchingBestillinger !== false ? (
						<Loading label="laster bestillinger" panel />
					) : antallBest > 0 ? (
						<OrganisasjonBestillingConnector />
					) : (
						tomOrgListe()
					))}
			</div>
		</ErrorBoundary>
	)
}
