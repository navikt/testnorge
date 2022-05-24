import React, { BaseSyntheticEvent, useState } from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import StatusListeConnector from '~/components/bestilling/statusListe/StatusListeConnector'
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
import { resetNavigering } from '~/ducks/finnPerson'
import GruppeHeaderConnector from '~/pages/gruppe/GruppeHeader/GruppeHeaderConnector'
import { useCurrentBruker } from '~/utils/hooks/useBruker'
import { useGruppeAlle } from '~/utils/hooks/useGruppe'
import { useBestillingerGruppe } from '~/utils/hooks/useBestilling'

export type GruppeProps = {
	visning: string
	setVisning: Function
	antallSlettet: number
}

export enum VisningType {
	VISNING_PERSONER = 'personer',
	VISNING_BESTILLING = 'bestilling',
}

export default function Gruppe({ visning, setVisning, antallSlettet }: GruppeProps) {
	const { gruppeId } = useParams()
	const {
		currentBruker: { brukernavn, brukertype },
	} = useCurrentBruker()
	const { grupperById, loading } = useGruppeAlle()
	const { bestillingerById, loading: loadingBestillinger } = useBestillingerGruppe(Number(gruppeId))

	const [startBestillingAktiv, visStartBestilling, skjulStartBestilling] = useBoolean(false)
	const [redirectToSoek, setRedirectToSoek] = useState(false)

	const dispatch = useDispatch()
	const navigate = useNavigate()

	if (loading || loadingBestillinger) {
		return <Loading label="Laster personer" panel />
	}
	// @ts-ignore
	const gruppe = grupperById[gruppeId]

	const byttVisning = (event: BaseSyntheticEvent) => {
		dispatch(resetNavigering())
		setVisning(typeof event === 'string' ? event : event.target.value)
	}

	const startBestilling = (values: {}) =>
		navigate(`/gruppe/${gruppeId}/bestilling`, { state: values })

	if (redirectToSoek) {
		return navigate(`/soek`)
	}

	const erLaast = gruppe.erLaast

	return (
		<div className="gruppe-container">
			<GruppeHeaderConnector gruppe={gruppe} />

			<StatusListeConnector gruppeId={gruppe.id} />

			<div className="toolbar">
				{brukertype === 'AZURE' && (
					<NavButton
						type="hoved"
						onClick={visStartBestilling}
						disabled={erLaast}
						title={erLaast ? 'Denne gruppen er låst, og du kan ikke legge til flere personer.' : ''}
						style={{ marginTop: '4px' }}
					>
						Opprett personer
					</NavButton>
				)}

				{brukertype === 'BANKID' && (
					<NavButton
						type="hoved"
						onClick={() => setRedirectToSoek(true)}
						disabled={erLaast}
						title={erLaast ? 'Denne gruppen er låst, og du kan ikke legge til flere personer.' : ''}
						style={{ marginTop: '4px' }}
					>
						Importer personer
					</NavButton>
				)}

				<div style={{ marginTop: '9px' }}>
					<ToggleGruppe onChange={byttVisning} name="toggler">
						<ToggleKnapp
							value={VisningType.VISNING_PERSONER}
							checked={visning === VisningType.VISNING_PERSONER}
						>
							<Icon
								size={13}
								kind={visning === VisningType.VISNING_PERSONER ? 'manLight' : 'man'}
							/>
							{`Personer (${gruppe.antallIdenter - antallSlettet})`}
						</ToggleKnapp>
						<ToggleKnapp
							value={VisningType.VISNING_BESTILLING}
							checked={visning === VisningType.VISNING_BESTILLING}
						>
							<Icon
								size={13}
								kind={visning === VisningType.VISNING_BESTILLING ? 'bestillingLight' : 'bestilling'}
							/>
							{`Bestillinger (${Object.keys(bestillingerById).length})`}
						</ToggleKnapp>
					</ToggleGruppe>
				</div>

				<FinnPersonBestillingConnector />
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
