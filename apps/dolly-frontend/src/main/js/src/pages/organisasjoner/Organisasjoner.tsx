import React, { useState } from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
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
import { useCurrentBruker } from '~/utils/hooks/useBruker'
import {
	useOrganisasjonBestilling,
	useOrganisasjonerForBruker,
} from '~/utils/hooks/useOrganisasjoner'
import { sokSelector } from '~/ducks/bestillingStatus'
import { useDispatch } from 'react-redux'
import { resetPaginering } from '~/ducks/finnPerson'
import { bottom } from '@popperjs/core'
import { Hjelpetekst } from '~/components/hjelpetekst/Hjelpetekst'
import { ToggleGroup } from '@navikt/ds-react'

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

export default ({ search, sidetall }: OrganisasjonerProps) => {
	const {
		currentBruker: { brukerId, brukertype },
	} = useCurrentBruker()

	const [visning, setVisning] = useState(VISNING_ORGANISASJONER)
	const [antallOrg, setAntallOrg] = useState(null)
	const navigate = useNavigate()
	const dispatch = useDispatch()

	const { bestillinger, bestillingerById, loading } = useOrganisasjonBestilling(brukerId)
	const { loading: loadingOrganisasjoner } = useOrganisasjonerForBruker(brukerId)

	const byttVisning = (value: string) => {
		dispatch(resetPaginering())
		setVisning(value)
	}

	const isFetching = loading || loadingOrganisasjoner

	const searchfieldPlaceholderSelector = () => {
		if (visning === VISNING_BESTILLINGER) {
			return 'Søk i bestillinger'
		}
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
						<Hjelpetekst placement={bottom}>
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

					<ToggleGroup size={'small'} onChange={byttVisning} defaultValue={VISNING_ORGANISASJONER}>
						<ToggleGroup.Item value={VISNING_ORGANISASJONER} defaultChecked={true}>
							<Icon
								size={13}
								kind={visning === VISNING_ORGANISASJONER ? 'organisasjonLight' : 'organisasjon'}
							/>
							{`Organisasjoner (${antallOrg ? antallOrg : 0})`}
						</ToggleGroup.Item>
						<ToggleGroup.Item value={VISNING_BESTILLINGER}>
							<Icon
								size={13}
								kind={visning === VISNING_BESTILLINGER ? 'bestillingLight' : 'bestilling'}
							/>
							{`Bestillinger (${antallBest ? antallBest : 0})`}
						</ToggleGroup.Item>
					</ToggleGroup>

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
