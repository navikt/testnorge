import React from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import Loading from '~/components/ui/loading/Loading'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import PersonListeConnector from './PersonListe/PersonListeConnector'
import BestillingListeConnector from './BestillingListe/BestillingListeConnector'
import { BestillingsveilederModal } from '~/components/bestillingsveileder/startModal/StartModal'
import Icon from '~/components/ui/icon/Icon'
import { useNavigate, useParams } from 'react-router-dom'
import { useDispatch } from 'react-redux'
import FinnPersonBestillingConnector from '~/pages/gruppeOversikt/FinnPersonBestillingConnector'
import { resetNavigering, resetPaginering } from '~/ducks/finnPerson'
import GruppeHeaderConnector from '~/pages/gruppe/GruppeHeader/GruppeHeaderConnector'
import { useCurrentBruker } from '~/utils/hooks/useBruker'
import { useGruppeById } from '~/utils/hooks/useGruppe'
import { useIkkeFerdigBestillingerGruppe } from '~/utils/hooks/useBestilling'
import StatusListeConnector from '~/components/bestilling/statusListe/StatusListeConnector'
import './Gruppe.less'
import { GruppeFeil, GruppeFeilmelding } from '~/pages/gruppe/GruppeFeil/GruppeFeilmelding'
import { ToggleGroup } from '@navikt/ds-react'

export type GruppeProps = {
	visning: string
	setVisning: Function
	sidetall: number
	sideStoerrelse: number
	sorting: object
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
	const { gruppeId } = useParams()
	const {
		currentBruker: { brukernavn, brukertype },
		loading: loadingBruker,
	} = useCurrentBruker()

	const { bestillingerById: ikkeFerdigBestillinger } = useIkkeFerdigBestillingerGruppe(
		gruppeId,
		'personer',
		sidetall,
		sideStoerrelse,
		update
	)

	const { bestillingerById, loading: loadingBestillinger } = useIkkeFerdigBestillingerGruppe(
		gruppeId,
		visning,
		sidetall,
		sideStoerrelse,
		update
	)

	const {
		gruppe,
		identer,
		loading: loadingGruppe,
		// @ts-ignore
	} = useGruppeById(gruppeId, sidetall, sideStoerrelse, false, sorting?.kolonne, sorting?.retning)
	const [startBestillingAktiv, visStartBestilling, skjulStartBestilling] = useBoolean(false)

	const dispatch = useDispatch()
	const navigate = useNavigate()

	const bankIdBruker = brukertype === 'BANKID'

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
			<GruppeHeaderConnector gruppe={gruppe} />

			{ikkeFerdigBestillinger && (
				// @ts-ignore
				<StatusListeConnector gruppeId={gruppe.id} bestillingListe={ikkeFerdigBestillinger} />
			)}

			<div className="gruppe-toolbar">
				<div className="gruppe--full gruppe--flex-row-center">
					{!bankIdBruker && (
						<NavButton
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
						variant={bankIdBruker ? 'primary' : 'secondary'}
						onClick={() =>
							navigate(`/testnorge`, {
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
					<ToggleGroup size={'small'} value={visning} onChange={byttVisning}>
						<ToggleGroup.Item
							key={VisningType.VISNING_PERSONER}
							value={VisningType.VISNING_PERSONER}
						>
							<Icon
								key={VisningType.VISNING_PERSONER}
								size={13}
								kind={visning === VisningType.VISNING_PERSONER ? 'manLight' : 'man'}
							/>
							{`Personer (${gruppe.antallIdenter})`}
						</ToggleGroup.Item>
						<ToggleGroup.Item
							key={VisningType.VISNING_BESTILLING}
							value={VisningType.VISNING_BESTILLING}
						>
							<Icon
								key={VisningType.VISNING_BESTILLING}
								size={13}
								kind={visning === VisningType.VISNING_BESTILLING ? 'bestillingLight' : 'bestilling'}
							/>
							{`Bestillinger (${gruppe.antallBestillinger})`}
						</ToggleGroup.Item>
					</ToggleGroup>
				</div>
			</div>

			{startBestillingAktiv && (
				<BestillingsveilederModal
					onSubmit={startBestilling}
					onAvbryt={skjulStartBestilling}
					brukernavn={brukernavn}
				/>
			)}

			{visning === VisningType.VISNING_PERSONER && (
				<PersonListeConnector
					iLaastGruppe={erLaast}
					brukertype={brukertype}
					gruppeInfo={gruppe}
					identer={identer}
				/>
			)}
			{visning === VisningType.VISNING_BESTILLING && (
				<BestillingListeConnector
					iLaastGruppe={erLaast}
					brukertype={brukertype}
					bestillingerById={bestillingerById}
					gruppeInfo={gruppe}
				/>
			)}
		</div>
	)
}
