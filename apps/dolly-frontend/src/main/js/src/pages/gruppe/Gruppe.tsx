import React, { Suspense } from 'react'
import useBoolean from '@/utils/hooks/useBoolean'
import Loading from '@/components/ui/loading/Loading'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import { useDispatch } from 'react-redux'
import { resetNavigering, resetPaginering } from '@/ducks/finnPerson'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { useGruppeById } from '@/utils/hooks/useGruppe'
import { useIkkeFerdigBestillingerGruppe } from '@/utils/hooks/useBestilling'
import { ToggleGroup } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import './Gruppe.less'
import { GruppeFeil, GruppeFeilmelding } from '@/pages/gruppe/GruppeFeil/GruppeFeilmelding'
import GruppeHeaderConnector from '@/pages/gruppe/GruppeHeader/GruppeHeaderConnector'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import Icon from '@/components/ui/icon/Icon'
import { lazyWithPreload } from '@/utils/lazyWithPreload'

const PersonListeConnector = lazyWithPreload(() => import('./PersonListe/PersonListeConnector'))
const BestillingListeConnector = lazyWithPreload(
	() => import('./BestillingListe/BestillingListeConnector'),
)
const BestillingsveilederModal = lazyWithPreload(
	() => import('@/components/bestillingsveileder/startModal/StartModal'),
)

const FinnPersonBestillingConnector = lazyWithPreload(
	() => import('@/pages/gruppeOversikt/FinnPersonBestillingConnector'),
)
const StatusListeConnector = lazyWithPreload(
	() => import('@/components/bestilling/statusListe/StatusListeConnector'),
)

export type GruppeProps = {
	visning: string
	setVisning: (value: VisningType) => void
	sidetall: number
	sideStoerrelse: number
	sorting: { kolonne: string; retning: string }
	update: string
}

export enum VisningType {
	VISNING_PERSONER = 'personer',
	VISNING_BESTILLING = 'bestilling',
}

export default ({
	visning,
	setVisning,
	sidetall,
	sideStoerrelse,
	sorting,
	update,
}: GruppeProps) => {
	const { gruppeId } = useParams<{ gruppeId: string }>()
	const { currentBruker, loading: loadingBruker } = useCurrentBruker()
	const location = useLocation()
	const dispatch = useDispatch()
	const navigate = useNavigate()

	const { bestillingerById: ikkeFerdigBestillinger } = useIkkeFerdigBestillingerGruppe(
		gruppeId!,
		'personer',
		sidetall,
		sideStoerrelse,
		update,
	)

	const { bestillingerById, loading: loadingBestillinger } = useIkkeFerdigBestillingerGruppe(
		gruppeId!,
		visning,
		sidetall,
		sideStoerrelse,
		update,
	)

	const {
		gruppe,
		identer,
		loading: loadingGruppe,
	} = useGruppeById(
		gruppeId!,
		location?.state?.sidetall || sidetall,
		sideStoerrelse,
		false,
		sorting?.kolonne || '',
		sorting?.retning || '',
	)

	const [startBestillingAktiv, visStartBestilling, skjulStartBestilling] = useBoolean(false)

	const bankIdBruker = currentBruker?.brukertype === 'BANKID'

	if (loadingBruker || loadingGruppe || loadingBestillinger) {
		return <Loading label="Laster personer" panel />
	}

	if (!gruppe) {
		return <GruppeFeilmelding feil={GruppeFeil.FETCH_FAILED} />
	}

	if (bankIdBruker && !gruppe?.erEierAvGruppe) {
		return <GruppeFeilmelding feil={GruppeFeil.ACCESS_DENIED} />
	}

	const byttVisning = (value: VisningType) => {
		dispatch(resetNavigering())
		dispatch(resetPaginering())
		setVisning(value)
	}

	const startBestilling = (values: Record<string, unknown>) =>
		navigate(`/gruppe/${gruppeId}/bestilling`, { state: values })

	const erLaast = gruppe.erLaast

	return (
		<div className="gruppe-container">
			<Suspense fallback={<Loading label="Laster personer..." />}>
				<GruppeHeaderConnector gruppeId={gruppe.id} />
				{ikkeFerdigBestillinger && (
					<StatusListeConnector
						gruppeId={gruppe.id}
						bestillingListe={Object.values(ikkeFerdigBestillinger)}
					/>
				)}
				<div className="gruppe-toolbar">
					<div className="gruppe--full gruppe--flex-row-center">
						{!bankIdBruker && (
							<NavButton
								data-testid={TestComponentSelectors.BUTTON_OPPRETT_PERSONER}
								variant={'primary'}
								onClick={visStartBestilling}
								onMouseOver={() => {
									BestillingsveilederModal?.preload?.()
									StatusListeConnector?.preload?.()
								}}
								disabled={erLaast}
								title={
									erLaast ? 'Denne gruppen er låst, og du kan ikke legge til flere personer.' : ''
								}
								className="margin-top-5 margin-bottom-5 margin-right-10"
							>
								Opprett personer
							</NavButton>
						)}
						<NavButton
							data-testid={TestComponentSelectors.BUTTON_IMPORTER_PERSONER}
							variant={bankIdBruker ? 'primary' : 'secondary'}
							onClick={() =>
								navigate(`/tenor/personer`, {
									state: {
										gruppe: gruppe,
									},
								})
							}
							disabled={erLaast}
							title={
								erLaast ? 'Denne gruppen er låst, og du kan ikke legge til flere personer.' : ''
							}
							className="margin-top-5 margin-bottom-5"
						>
							Importer personer
						</NavButton>
						<div style={{ flexGrow: '2' }}></div>
						{!bankIdBruker && <FinnPersonBestillingConnector />}
					</div>
					<div className="gruppe--flex-column-center margin-top-20 margin-bottom-10">
						<ToggleGroup
							value={visning}
							onChange={byttVisning}
							style={{ backgroundColor: '#ffffff' }}
						>
							<ToggleGroup.Item
								data-testid={TestComponentSelectors.TOGGLE_VISNING_PERSONER}
								key={VisningType.VISNING_PERSONER}
								value={VisningType.VISNING_PERSONER}
							>
								<Icon
									key={VisningType.VISNING_PERSONER}
									size={13}
									kind={visning === VisningType.VISNING_PERSONER ? 'man-light' : 'man'}
								/>
								{`Personer (${gruppe.antallIdenter || 0})`}
							</ToggleGroup.Item>
							<ToggleGroup.Item
								data-testid={TestComponentSelectors.TOGGLE_VISNING_BESTILLINGER}
								key={VisningType.VISNING_BESTILLING}
								value={VisningType.VISNING_BESTILLING}
							>
								<Icon
									key={VisningType.VISNING_BESTILLING}
									size={13}
									kind={
										visning === VisningType.VISNING_BESTILLING ? 'bestilling-light' : 'bestilling'
									}
								/>
								{`Bestillinger (${gruppe.antallBestillinger || 0})`}
							</ToggleGroup.Item>
						</ToggleGroup>
					</div>
				</div>
				{startBestillingAktiv && (
					<BestillingsveilederModal
						onSubmit={startBestilling}
						onAvbryt={skjulStartBestilling}
						brukernavn={currentBruker?.brukernavn}
					/>
				)}
				{visning === VisningType.VISNING_PERSONER && (
					<PersonListeConnector
						iLaastGruppe={erLaast}
						brukertype={currentBruker?.brukertype}
						gruppeId={gruppeId!}
						identer={identer}
						bestillingerById={bestillingerById}
					/>
				)}
				{visning === VisningType.VISNING_BESTILLING && (
					<BestillingListeConnector
						iLaastGruppe={erLaast}
						brukertype={currentBruker?.brukertype}
						bestillingerById={bestillingerById}
						gruppeId={gruppeId!}
					/>
				)}
			</Suspense>
		</div>
	)
}
