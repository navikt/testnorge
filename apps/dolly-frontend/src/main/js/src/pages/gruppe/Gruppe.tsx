import React from 'react'
import useBoolean from '@/utils/hooks/useBoolean'
import Loading from '@/components/ui/loading/Loading'
import { useLocation, useNavigate, useParams } from 'react-router'
import { useDispatch, useSelector } from 'react-redux'
import { resetNavigering, resetPaginering, setVisning } from '@/ducks/finnPerson'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { useGruppeById } from '@/utils/hooks/useGruppe'
import { useIkkeFerdigBestillingerGruppe } from '@/utils/hooks/useBestilling'
import { TestComponentSelectors } from '#/mocks/Selectors'
import './Gruppe.less'
import { GruppeFeil, GruppeFeilmelding } from '@/pages/gruppe/GruppeFeil/GruppeFeilmelding'
import GruppeHeaderConnector from '@/pages/gruppe/GruppeHeader/GruppeHeaderConnector'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import StatusListeConnector from '@/components/bestilling/statusListe/StatusListeConnector'
import BestillingsveilederModal from '@/components/bestillingsveileder/startModal/StartModal'
import FinnPersonBestillingConnector from '@/pages/gruppeOversikt/FinnPersonBestillingConnector'
import { GruppeToggle } from '@/pages/gruppe/GruppeToggle'
import { GruppeVisning } from '@/pages/gruppe/GruppeVisning'

export type GruppeProps = {
	sidetall: number
	sideStoerrelse: number
	sorting: { kolonne: string; retning: string }
	update: string
}

export enum VisningType {
	VISNING_PERSONER = 'personer',
	VISNING_BESTILLING = 'bestilling',
}

export default ({ sidetall, sideStoerrelse, sorting, update }: GruppeProps) => {
	const { gruppeId } = useParams<{ gruppeId: string }>()
	const { currentBruker, loading: loadingBruker } = useCurrentBruker()
	const location = useLocation()
	const dispatch = useDispatch()
	const navigate = useNavigate()
	const visning = useSelector((state: any) => state.finnPerson.visning)

	const { bestillingerById: ikkeFerdigBestillinger } = useIkkeFerdigBestillingerGruppe(
		gruppeId,
		'personer',
		sidetall,
		sideStoerrelse,
		update,
	)

	const { bestillingerById, loading: loadingBestillinger } = useIkkeFerdigBestillingerGruppe(
		gruppeId,
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
		gruppeId,
		location?.state?.sidetall || sidetall,
		sideStoerrelse,
		false,
		sorting?.kolonne || '',
		sorting?.retning || '',
	)

	const [startBestillingAktiv, visStartBestilling, skjulStartBestilling] = useBoolean(false)

	const bankIdBruker = currentBruker?.brukertype === 'BANKID'

	if (loadingBruker || loadingGruppe) {
		return <Loading label="Laster personer" panel />
	}

	if (!gruppe) {
		return <GruppeFeilmelding feil={GruppeFeil.FETCH_FAILED} />
	}

	if (bankIdBruker && !gruppe?.erEierAvGruppe) {
		return <GruppeFeilmelding feil={GruppeFeil.ACCESS_DENIED} />
	}

	const byttVisning = (value: VisningType) => {
		dispatch(resetPaginering())
		dispatch(resetNavigering())
		dispatch(setVisning(value))
	}

	const startBestilling = (values: Record<string, unknown>) =>
		navigate(`/gruppe/${gruppeId}/bestilling`, { state: values })

	const erLaast = gruppe.erLaast

	return (
		<div className="gruppe-container">
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
						title={erLaast ? 'Denne gruppen er låst, og du kan ikke legge til flere personer.' : ''}
						className="margin-top-5 margin-bottom-5"
					>
						Importer personer
					</NavButton>
					<div style={{ flexGrow: '2' }}></div>
					{!bankIdBruker && <FinnPersonBestillingConnector />}
				</div>
				<div className="gruppe--flex-column-center margin-top-20 margin-bottom-10">
					<GruppeToggle
						visning={visning}
						byttVisning={byttVisning}
						antallIdenter={gruppe.antallIdenter || 0}
						antallBestillinger={gruppe.antallBestillinger || 0}
					/>
				</div>
			</div>
			{startBestillingAktiv && (
				<BestillingsveilederModal
					onSubmit={startBestilling}
					onAvbryt={skjulStartBestilling}
					brukernavn={currentBruker?.brukernavn}
				/>
			)}
			<GruppeVisning
				visning={visning}
				erLaast={erLaast}
				brukertype={currentBruker?.brukertype}
				gruppeId={gruppeId!}
				identer={identer}
				bestillingerById={bestillingerById}
				lasterBestillinger={loadingBestillinger}
			/>
		</div>
	)
}
