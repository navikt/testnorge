import React, { useState } from 'react'
import Hjelpetekst from '~/components/hjelpetekst'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import Icon from '~/components/ui/icon/Icon'
import { SearchField } from '~/components/searchField/SearchField'
import Loading from '~/components/ui/loading/Loading'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import OrganisasjonBestilling from './OrganisasjonBestilling'
import StatusListeConnector from '~/components/bestilling/statusListe/StatusListeConnector'
import OrganisasjonListe from './OrganisasjonListe'
import { dollySlack } from '~/components/dollySlack/dollySlack'
import TomOrgListe from './TomOrgliste'
import { useNavigate } from 'react-router-dom'
import { PopoverOrientering } from 'nav-frontend-popover'
import { useCurrentBruker } from '~/utils/hooks/useBruker'
import {
	useOrganisasjonBestilling,
	useOrganisasjonerForBruker,
} from '~/utils/hooks/useOrganisasjoner'
import { sokSelector } from '~/ducks/bestillingStatus'

type OrganisasjonerProps = {
	search?: string
	sidetall: number
}

enum BestillingType {
	NY = 'NY',
	STANDARD = 'STANDARD',
}

const VISNING_ORGANISASJONER = 'organisasjoner'
const VISNING_BESTILLINGER = 'bestillinger'

export default function Organisasjoner({ search, sidetall }: OrganisasjonerProps) {
	const {
		currentBruker: { brukerId, brukertype },
	} = useCurrentBruker()

	const [visning, setVisning] = useState(VISNING_ORGANISASJONER)
	const [antallOrg, setAntallOrg] = useState(null)
	const byttVisning = (event: React.ChangeEvent<any>) => setVisning(event.target.value)
	const navigate = useNavigate()

	const { bestillinger, bestillingerById, loading } = useOrganisasjonBestilling(brukerId)
	const { loading: loadingOrganisasjoner } = useOrganisasjonerForBruker(brukerId)

	const isFetching = loading || loadingOrganisasjoner

	const searchfieldPlaceholderSelector = () => {
		if (visning === VISNING_BESTILLINGER) return 'Søk i bestillinger'
		return 'Søk i organisasjoner'
	}

	const antallBest = bestillinger?.length

	const startBestilling = (type: string) => {
		navigate('/organisasjoner/bestilling', { state: { opprettOrganisasjon: type } })
	}

	return (
		<ErrorBoundary>
			<div className="oversikt-container">
				<div className="toolbar">
					<div className="page-header flexbox--align-center">
						<h1>Organisasjoner</h1>
						<Hjelpetekst hjelpetekstFor="Organisasjoner" type={PopoverOrientering.Under}>
							Organisasjoner i Dolly er en del av NAVs syntetiske populasjon og dekker behov for
							data knyttet til bedrifter/virksomheter (EREG). Løsningen er under utvikling, og det
							legges til ny funksjonalitet fortløpende.
							<br />
							På denne siden finner du en oversikt over dine egne organisasjoner. Du kan opprette
							nye organisasjoner ved å trykke på knappen under.
							<br />
							Kontakt oss gjerne på {dollySlack} dersom du har spørsmål eller innspill.
						</Hjelpetekst>
					</div>
				</div>

				{bestillingerById && (
					// @ts-ignore
					<StatusListeConnector brukerId={brukerId} bestillingListe={bestillingerById} />
				)}

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

					<SearchField placeholder={searchfieldPlaceholderSelector()} setText={undefined} />
				</div>

				{visning === VISNING_ORGANISASJONER &&
					(isFetching ? (
						<Loading label="Laster organisasjoner" panel />
					) : antallOrg !== 0 ? (
						<OrganisasjonListe
							// @ts-ignore
							bestillinger={bestillinger}
							search={search}
							setAntallOrg={setAntallOrg}
							sidetall={sidetall}
						/>
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
						<OrganisasjonBestilling
							sidetall={sidetall}
							brukerId={brukerId}
							brukertype={brukertype}
							bestillinger={sokSelector(bestillingerById, search)}
						/>
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
