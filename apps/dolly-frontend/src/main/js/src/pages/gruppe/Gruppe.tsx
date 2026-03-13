import React, { useEffect } from 'react'
import Loading from '@/components/ui/loading/Loading'
import { useLocation, useNavigate, useParams } from 'react-router'
import { useDispatch, useSelector } from 'react-redux'
import {
	resetNavigering,
	resetPaginering,
	setGruppeNavigerTil,
	setVisning,
} from '@/ducks/finnPerson'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { useGruppeById, useGruppeInfo } from '@/utils/hooks/useGruppe'
import { useIkkeFerdigBestillingerGruppe } from '@/utils/hooks/useBestilling'
import { TestComponentSelectors } from '#/mocks/Selectors'
import './Gruppe.less'
import { GruppeFeil, GruppeFeilmelding } from '@/pages/gruppe/GruppeFeil/GruppeFeilmelding'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import StatusListeConnector from '@/components/bestilling/statusListe/StatusListeConnector'
import { GruppeToggle } from '@/pages/gruppe/GruppeToggle'
import { GruppeVisning } from '@/pages/gruppe/GruppeVisning'
import Navigering from '@/pages/gruppeOversikt/Navigering'
import GruppeHeader from '@/pages/gruppe/GruppeHeader/GruppeHeader'
import { useSideStoerrelse } from '@/utils/hooks/useSideStoerrelse'

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

export default ({ sidetall, sorting, update }: GruppeProps) => {
	const { gruppeId } = useParams<{ gruppeId: string }>()
	const { currentBruker, loading: loadingBruker } = useCurrentBruker()
	const location = useLocation()
	const dispatch = useDispatch()
	const navigate = useNavigate()
	const visning = useSelector((state: any) => state.finnPerson.visning)
	const { sideStoerrelse } = useSideStoerrelse()

	useEffect(() => {
		dispatch(setGruppeNavigerTil(null))
	}, [])

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

	const { gruppeInfo, loading: loadingGruppeInfo } = useGruppeInfo(gruppeId)

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

	const bankIdBruker = currentBruker?.brukertype === 'BANKID'

	if (loadingGruppeInfo) {
		return <Loading label="Laster gruppe ..." panel />
	}

	if (!gruppe && !loadingGruppe && !loadingBruker) {
		return <GruppeFeilmelding feil={GruppeFeil.FETCH_FAILED} />
	}

	const byttVisning = (value: VisningType) => {
		dispatch(resetPaginering())
		dispatch(resetNavigering())
		dispatch(setVisning(value))
	}

	const startBestilling = () => navigate(`/gruppe/${gruppeId}/bestilling`)

	const erLaast = gruppeInfo?.erLaast

	return (
		<div className="gruppe-container">
			<GruppeHeader gruppeId={gruppeInfo?.id} />
			{ikkeFerdigBestillinger && (
				<StatusListeConnector
					gruppeId={gruppeInfo?.id}
					bestillingListe={Object.values(ikkeFerdigBestillinger)}
				/>
			)}
			<div className="gruppe-toolbar">
				<div className="gruppe--full gruppe--flex-row-center">
					{!bankIdBruker && (
						<NavButton
							data-testid={TestComponentSelectors.BUTTON_OPPRETT_PERSONER}
							variant={'primary'}
							onClick={startBestilling}
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
							navigate(`/tenorpersoner`, {
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
					{!bankIdBruker && <Navigering />}
				</div>
				<div className="gruppe--flex-column-center margin-top-20 margin-bottom-10">
					<GruppeToggle
						visning={visning}
						byttVisning={byttVisning}
						antallIdenter={gruppeInfo?.antallIdenter || 0}
						antallBestillinger={gruppeInfo?.antallBestillinger || 0}
					/>
				</div>
			</div>
			<GruppeVisning
				visning={visning}
				erLaast={erLaast}
				brukertype={currentBruker?.brukertype}
				gruppeId={gruppeId!}
				identer={identer}
				bestillingerById={bestillingerById}
				lasterBestillinger={loadingBestillinger}
				lasterGruppe={loadingGruppe || loadingBruker}
			/>
		</div>
	)
}
