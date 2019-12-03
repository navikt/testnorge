import React, { Fragment } from 'react'
import Button from '~/components/ui/button/Button'
import useBoolean from '~/utils/hooks/useBoolean'
import Loading from '~/components/ui/loading/Loading'
import Overskrift from '~/components/ui/overskrift/Overskrift'
import GruppeDetaljer from '~/pages/gruppe/GruppeDetaljer/GruppeDetaljer'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import ConfirmTooltip from '~/components/ui/confirmTooltip/ConfirmTooltip'
import FavoriteButtonConnector from '~/components/ui/button/FavoriteButton/FavoriteButtonConnector'
import EksporterExcel from '~/pages/gruppe/EksporterExcel/EksporterExcel'

import './GruppeHeader.less'

export default function GruppeHeader({ gruppe, identArray, isDeletingGruppe, deleteGruppe }) {
	const [visRedigerState, visRediger, skjulRediger] = useBoolean(false)

	return (
		<Fragment>
			<div className="header-valg">
				<Overskrift label={gruppe.navn}>
					{gruppe.erEierAvGruppe ? (
						// Vise redigeringsknapp eller favoriseringsstjerne
						<Button className="flexbox--align-center" kind="edit" onClick={visRediger}>
							REDIGER
						</Button>
					) : (
						<FavoriteButtonConnector groupId={gruppe.id} />
					)}
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
				</Overskrift>
				<div className="hoyre">
					<EksporterExcel identer={gruppe.identer} gruppeId={gruppe.id} />
				</div>
			</div>

			{visRedigerState && <RedigerGruppeConnector gruppe={gruppe} onCancel={skjulRediger} />}

			<GruppeDetaljer gruppe={gruppe} identArray={identArray} />
		</Fragment>
	)
}
