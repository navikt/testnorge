import React, { useState } from 'react'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { SearchField } from '@/components/searchField/SearchField'
import Loading from '@/components/ui/loading/Loading'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import OrganisasjonBestilling from './OrganisasjonBestilling'
import StatusListeConnector from '@/components/bestilling/statusListe/StatusListeConnector'
import OrganisasjonListe from './OrganisasjonListe'
import { dollySlack } from '@/components/dollySlack/dollySlack'
import TomOrgListe from './TomOrgliste'
import { useNavigate } from 'react-router'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { useOrganisasjonBestilling } from '@/utils/hooks/useDollyOrganisasjoner'
import { sokSelector } from '@/ducks/bestillingStatus'
import { useDispatch } from 'react-redux'
import { resetPaginering } from '@/ducks/finnPerson'
import { bottom } from '@popperjs/core'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { ToggleGroup } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { useReduxSelector } from '@/utils/hooks/useRedux'
import { useForm } from 'react-hook-form'
import OrganisasjonHeader from '@/pages/organisasjoner/OrgansisasjonHeader/OrganisasjonHeader'
import { useSearchHotkey } from '@/utils/hooks/useSearchHotkey'
import { FileCheckmarkIcon, TenancyIcon } from '@navikt/aksel-icons'

enum BestillingType {
	NY = 'NY',
	STANDARD = 'STANDARD',
}

const VISNING_ORGANISASJONER = 'organisasjoner'
const VISNING_BESTILLINGER = 'bestillinger'

export default () => {
	const { currentBruker } = useCurrentBruker()

	const [visning, setVisning] = useState(VISNING_ORGANISASJONER)
	const searchStr = useReduxSelector((state) => state.search)
	const formMethods = useForm({ mode: 'onBlur' })
	const searchInputRef = React.useRef(null)
	const shortcutKey = useSearchHotkey(searchInputRef)

	const [antallOrg, setAntallOrg] = useState(null)
	const navigate = useNavigate()
	const dispatch = useDispatch()

	const { bestillinger, bestillingerById, loading } = useOrganisasjonBestilling(
		currentBruker?.representererTeam?.brukerId ?? currentBruker?.brukerId,
	)

	const byttVisning = (value: string) => {
		dispatch(resetPaginering())
		setVisning(value)
	}

	const isFetching = loading

	const searchfieldPlaceholderSelector = () => {
		if (visning === VISNING_BESTILLINGER) {
			return 'Søk i bestillinger'
		}
		return 'Søk i organisasjoner'
	}

	const antallBest = bestillinger?.length

	const startBestilling = (values) => {
		navigate('/organisasjoner/bestilling', {
			state: { opprettOrganisasjon: BestillingType.NY, ...values },
		})
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

				<OrganisasjonHeader antallOrganisasjoner={antallOrg} />

				{bestillingerById && (
					// @ts-ignore
					<StatusListeConnector
						brukerId={currentBruker?.representererTeam?.brukerId ?? currentBruker?.brukerId}
						bestillingListe={bestillingerById}
					/>
				)}

				<div className="toolbar">
					<NavButton
						data-testid={TestComponentSelectors.BUTTON_OPPRETT_ORGANISASJON}
						variant={'primary'}
						onClick={() => startBestilling(formMethods.getValues())}
					>
						Opprett organisasjon
					</NavButton>

					<ToggleGroup size={'small'} onChange={byttVisning} defaultValue={VISNING_ORGANISASJONER}>
						<ToggleGroup.Item
							value={VISNING_ORGANISASJONER}
							icon={<TenancyIcon aria-hidden />}
							label={`Organisasjoner (${antallOrg ?? '0'})`}
						/>
						<ToggleGroup.Item
							value={VISNING_BESTILLINGER}
							icon={<FileCheckmarkIcon aria-hidden />}
							label={`Bestillinger (${antallBest ?? '0'})`}
						/>
					</ToggleGroup>

					<SearchField
						style={{ width: '280px', marginRight: '-79px' }}
						shortcutKey={shortcutKey}
						placeholder={searchfieldPlaceholderSelector()}
						setText={undefined}
						ref={searchInputRef}
					/>
				</div>

				{visning === VISNING_ORGANISASJONER &&
					(isFetching ? (
						<Loading label="Laster organisasjoner" panel />
					) : antallOrg !== 0 ? (
						<OrganisasjonListe bestillinger={bestillinger} setAntallOrg={setAntallOrg} />
					) : (
						<TomOrgListe
							startBestilling={() => startBestilling}
							bestillingstype={BestillingType.STANDARD}
						/>
					))}
				{visning === VISNING_BESTILLINGER &&
					(isFetching ? (
						<Loading label="Laster bestillinger" panel />
					) : antallBest > 0 ? (
						<OrganisasjonBestilling
							brukerId={currentBruker?.representererTeam?.brukerId ?? currentBruker?.brukerId}
							brukertype={currentBruker?.brukertype}
							bestillinger={sokSelector(bestillingerById, searchStr)}
						/>
					) : (
						<TomOrgListe
							startBestilling={() => startBestilling}
							bestillingstype={BestillingType.STANDARD}
						/>
					))}
			</div>
		</ErrorBoundary>
	)
}
