import React, { Fragment } from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import Overskrift from '~/components/ui/overskrift/Overskrift'
import GruppeDetaljer from '~/pages/gruppe/GruppeDetaljer/GruppeDetaljer'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import ConfirmTooltip from '~/components/ui/confirmTooltip/ConfirmTooltip'
import FavoriteButtonConnector from '~/components/ui/button/FavoriteButton/FavoriteButtonConnector'
import EksporterExcel from '~/pages/gruppe/EksporterExcel/EksporterExcel'

import './GruppeHeader.less'

export default function GruppeHeader({ gruppe, isDeletingGruppe, deleteGruppe }) {
	const [visRedigerState, visRediger, skjulRediger] = useBoolean(false)

	const groupActions = []

	// Vise redigeringsknapp eller stjerne
	if (gruppe.erMedlemAvTeamSomEierGruppe) {
		groupActions.push({
			icon: 'edit',
			label: 'REDIGER',
			onClick: visRediger
		})
	}

	return (
		<Fragment>
			<div className="header-valg">
				<Overskrift label={gruppe.navn} actions={groupActions}>
					{isDeletingGruppe ? (
						<Loading label="Sletter gruppe" panel />
					) : (
						<ConfirmTooltip
							label="SLETT"
							className="flexbox--align-center"
							message="Vil du slette denne testdatagruppen?"
							onClick={deleteGruppe}
						/>
					)}
					{!gruppe.erMedlemAvTeamSomEierGruppe && <FavoriteButtonConnector groupId={gruppe.id} />}
				</Overskrift>
				<div className="hoyre">
					<EksporterExcel testidenter={gruppe.testidenter} gruppeId={gruppe.id} />
				</div>
			</div>

			{visRedigerState && <RedigerGruppeConnector gruppe={gruppe} onCancel={skjulRediger} />}

			<GruppeDetaljer gruppe={gruppe} />
		</Fragment>
	)
}
