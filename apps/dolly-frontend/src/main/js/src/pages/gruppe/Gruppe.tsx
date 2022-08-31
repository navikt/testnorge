import React, { BaseSyntheticEvent } from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import Loading from '~/components/ui/loading/Loading'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import PersonListeConnector from './PersonListe/PersonListeConnector'
import BestillingListeConnector from './BestillingListe/BestillingListeConnector'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { BestillingsveilederModal } from '~/components/bestillingsveileder/startModal/StartModal'
import Icon from '~/components/ui/icon/Icon'
import { useNavigate, useParams } from 'react-router-dom'
import { useDispatch } from 'react-redux'
import FinnPersonBestillingConnector from '~/pages/gruppeOversikt/FinnPersonBestillingConnector'
import { resetNavigering, resetPaginering } from '~/ducks/finnPerson'
import GruppeHeaderConnector from '~/pages/gruppe/GruppeHeader/GruppeHeaderConnector'
import { useCurrentBruker } from '~/utils/hooks/useBruker'
import { useGruppeById } from '~/utils/hooks/useGruppe'
import { useBestillingerGruppe } from '~/utils/hooks/useBestilling'
import StatusListeConnector from '~/components/bestilling/statusListe/StatusListeConnector'
import './Gruppe.less'

export type GruppeProps = {
	visning: string
	setVisning: Function
}

export enum VisningType {
	VISNING_PERSONER = 'personer',
	VISNING_BESTILLING = 'bestilling',
}

export default function Gruppe({ visning, setVisning }: GruppeProps) {
	const { gruppeId } = useParams()
	const {
		currentBruker: { brukernavn, brukertype },
	} = useCurrentBruker()
	const { bestillingerById, loading: loadingBestillinger } = useBestillingerGruppe(Number(gruppeId))
	const { gruppe, loading: loadingGruppe } = useGruppeById(Number(gruppeId))

	const [startBestillingAktiv, visStartBestilling, skjulStartBestilling] = useBoolean(false)

	const dispatch = useDispatch()
	const navigate = useNavigate()

	if (loadingGruppe || loadingBestillinger) {
		return <Loading label="Laster personer" panel />
	}

	const byttVisning = (event: BaseSyntheticEvent) => {
		dispatch(resetNavigering())
		dispatch(resetPaginering())
		setVisning(typeof event === 'string' ? event : event.target.value)
	}

	const startBestilling = (values: {}) =>
		navigate(`/gruppe/${gruppeId}/bestilling`, { state: values })

	const erLaast = gruppe.erLaast
	const bankIdBruker = brukertype === 'BANKID'
	return (
		<div className="gruppe-container">
			<GruppeHeaderConnector gruppeId={gruppe.id} />

			{bestillingerById && (
				// @ts-ignore
				<StatusListeConnector gruppeId={gruppe.id} bestillingListe={bestillingerById} />
			)}

			<div className="gruppe-toolbar">
				<div className="gruppe--full gruppe--flex-row-center">
					{!bankIdBruker && (
						<NavButton
							type="hoved"
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
						type={bankIdBruker ? 'hoved' : 'standard'}
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
					<ToggleGruppe onChange={byttVisning} name="toggler">
						<ToggleKnapp
							key={visning}
							value={VisningType.VISNING_PERSONER}
							checked={visning === VisningType.VISNING_PERSONER}
						>
							<Icon
								key={visning}
								size={13}
								kind={visning === VisningType.VISNING_PERSONER ? 'manLight' : 'man'}
							/>
							{`Personer (${gruppe.antallIdenter})`}
						</ToggleKnapp>
						<ToggleKnapp
							key={visning}
							value={VisningType.VISNING_BESTILLING}
							checked={visning === VisningType.VISNING_BESTILLING}
						>
							<Icon
								key={visning}
								size={13}
								kind={visning === VisningType.VISNING_BESTILLING ? 'bestillingLight' : 'bestilling'}
							/>
							{`Bestillinger (${Object.keys(bestillingerById).length})`}
						</ToggleKnapp>
					</ToggleGruppe>
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
				<PersonListeConnector iLaastGruppe={erLaast} brukertype={brukertype} gruppeId={gruppeId} />
			)}
			{visning === VisningType.VISNING_BESTILLING && (
				<BestillingListeConnector
					iLaastGruppe={erLaast}
					brukertype={brukertype}
					gruppeId={gruppeId}
				/>
			)}
		</div>
	)
}
